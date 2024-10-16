package com.devashish94.video_processing_service.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonClientConfig {

    @Value("${redis.host}")
    private String REDIS_HOST;

    @Value("${redis.port}")
    private String REDIS_PORT;

    @Bean
    public RedissonClient createClient() {
        Config config = new Config();
        config.useSingleServer();
        config.setCodec(new StringCodec());
        config.useSingleServer().setAddress(String.format("redis://%s:%s", REDIS_HOST, REDIS_PORT));
        return Redisson.create(config);
    }
}
