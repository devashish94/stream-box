package com.devashish94.video_processing_service.config;

import com.devashish94.video_processing_service.constant.AppConstants;
import com.devashish94.video_processing_service.dto.VideoProcessRequestEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class VideoProcessRequestConsumerConfig {

    @Value("${kafka.bootstrap-url}")
    private String KAFKA_BOOTSTRAP_SERVERS;

    @Bean
    public ConsumerFactory<String, VideoProcessRequestEvent> videoProcessRequestConsumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, AppConstants.VIDEO_PROCESS_GROUP);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new JsonDeserializer<>(VideoProcessRequestEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, VideoProcessRequestEvent> videoProcessRequestContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, VideoProcessRequestEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(videoProcessRequestConsumerFactory());
        return factory;
    }
}
