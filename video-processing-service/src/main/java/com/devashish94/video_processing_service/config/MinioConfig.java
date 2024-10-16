package com.devashish94.video_processing_service.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String MINIO_ENDPOINT;

    @Value("${minio.access-key}")
    private String MINIO_ACCESS_KEY;

    @Value("${minio.secret-key}")
    private String MINIO_SECRET_KEY;

    @Bean
    public MinioClient getMinioClient() {
        return MinioClient.builder()
                .endpoint(MINIO_ENDPOINT)
                .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
                .build();
    }

}
