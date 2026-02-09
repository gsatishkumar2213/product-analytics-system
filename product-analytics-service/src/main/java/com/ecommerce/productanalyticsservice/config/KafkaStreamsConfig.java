package com.ecommerce.productanalyticsservice.config;

import com.ecommerce.shared.entity.EnrichedProductView;
import com.ecommerce.shared.entity.Product;
import com.ecommerce.shared.entity.ProductView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @PostConstruct
    public void initializeTopics() {
        log.info("Initializing Kafka topics...");

        Properties adminConfig = new Properties();
        adminConfig.put("bootstrap.servers", bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(adminConfig)) {
            Set<String> existingTopics = adminClient.listTopics().names().get();
            log.info("Found {} existing topics", existingTopics.size());

            List<NewTopic> requiredTopics = List.of(
                    new NewTopic("product-catalog", 1, (short) 1),
                    new NewTopic("product-view", 1, (short) 1),
                    new NewTopic("enriched-product-view", 1, (short) 1),
                    new NewTopic("product-count", 1, (short) 1)
            );

            List<NewTopic> missingTopics = requiredTopics.stream()
                                                         .filter(topic -> !existingTopics.contains(topic.name()))
                                                         .collect(Collectors.toList());

            if (missingTopics.isEmpty()) {
                log.info("All required topics already exist");
            } else {
                log.info("Creating {} missing topics: {}",
                        missingTopics.size(),
                        missingTopics.stream().map(NewTopic::name).collect(Collectors.joining(", "))
                );

                CreateTopicsResult result = adminClient.createTopics(missingTopics);
                result.all().get();

                log.info("Successfully created topics");
            }

        } catch (Exception e) {
            log.error("Failed to initialize topics", e);
            throw new RuntimeException("Topic initialization failed", e);
        }
    }

    @Bean
    public Topology buildTopology(StreamsBuilder streamsBuilder) {
        log.info("Building Kafka Streams topology...");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        JsonSerde<ProductView> productViewSerde = new JsonSerde<>(ProductView.class, mapper);
        JsonSerde<Product> productSerde = new JsonSerde<>(Product.class, mapper);
        JsonSerde<EnrichedProductView> enrichedSerde = new JsonSerde<>(EnrichedProductView.class,
                mapper);

        // Topology 1: Windowed view counts
        buildViewCountsTopology(streamsBuilder, productViewSerde);

        // Topology 2: Stream-table join for enrichment
        buildEnrichmentTopology(streamsBuilder, productViewSerde, productSerde, enrichedSerde);

        log.info("Kafka Streams topology built successfully");
        return streamsBuilder.build();
    }

    private void buildViewCountsTopology(StreamsBuilder streamsBuilder,
                                         JsonSerde<ProductView> productViewSerde) {
        streamsBuilder
                .stream("product-view", Consumed.with(Serdes.String(), productViewSerde))
                .groupBy((key, value) -> value.getProductId().toString(),
                        Grouped.with(Serdes.String(), productViewSerde))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
                .count()
                .toStream()
                .map((windowedKey, count) -> KeyValue.pair(
                        windowedKey.key() + "@" + windowedKey.window().startTime(),
                        count
                ))
                .to("product-count", Produced.with(Serdes.String(), Serdes.Long()));

        log.info("View counts topology configured (5-minute tumbling windows)");
    }

    private void buildEnrichmentTopology(
            StreamsBuilder streamsBuilder,
            JsonSerde<ProductView> productViewSerde,
            JsonSerde<Product> productSerde,
            JsonSerde<EnrichedProductView> enrichedSerde) {

        KTable<String, Product> productTable = streamsBuilder.table(
                "product-catalog",
                Consumed.with(Serdes.String(), productSerde)
        );

        KStream<String, ProductView> productViewStream = streamsBuilder.stream(
                "product-view",
                Consumed.with(Serdes.String(), productViewSerde)
        );

        productViewStream
                .join(productTable, (productView, product) -> new EnrichedProductView(
                        productView.getProductId(),
                        product.getName(),
                        product.getPrice(),
                        productView.getUserId(),
                        productView.getDeviceType(),
                        productView.getViewedAt()
                ))
                .to("enriched-product-view", Produced.with(Serdes.String(), enrichedSerde));

        log.info("Enrichment topology configured (stream-table join)");
    }
}