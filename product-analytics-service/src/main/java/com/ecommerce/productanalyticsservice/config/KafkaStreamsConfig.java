package com.ecommerce.productanalyticsservice.config;

import com.ecommerce.shared.entity.DeviceType;
import com.ecommerce.shared.entity.ProductView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Bean
    public Topology topology(StreamsBuilder streamBuilder) {
        // 1. Create a Serde manually that handles Java 8 Dates
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        JsonSerde<ProductView> productViewSerde = new JsonSerde<>(ProductView.class, mapper);

        KStream<String, ProductView> stream = streamBuilder.stream("product-view",
                Consumed.with(Serdes.String(), productViewSerde));

        stream.filter((key, value) -> value != null && value.getDeviceType() != null)
              .filter((key, value) -> {
                  DeviceType type = value.getDeviceType();
                  return type == DeviceType.MOBILE || type == DeviceType.DESKTOP;
              })
              .groupBy(
                      (key, value) -> value.getProductId().toString(),
                      Grouped.with(Serdes.String(), productViewSerde)
              )
              .count()
              .suppress(Suppressed.untilTimeLimit(Duration.ofSeconds(10),
                      Suppressed.BufferConfig.maxRecords(10)))
              .toStream()
              .to("product-trends", Produced.with(Serdes.String(), Serdes.Long()));

        return streamBuilder.build();
    }
}
