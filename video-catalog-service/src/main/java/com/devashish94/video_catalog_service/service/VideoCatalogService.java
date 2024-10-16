package com.devashish94.video_catalog_service.service;

import com.devashish94.video_catalog_service.constant.AppConstants;
import com.devashish94.video_catalog_service.dto.VideoProcessRequestEvent;
import com.devashish94.video_catalog_service.entity.Metadata;
import com.devashish94.video_catalog_service.repository.MetadataRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class VideoCatalogService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final MetadataRepository metadataRepository;


    public VideoCatalogService(KafkaTemplate<String, Object> kafkaTemplate, MetadataRepository metadataRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.metadataRepository = metadataRepository;
    }

    public void sendProcessVideoRequest(final VideoProcessRequestEvent event) {
        kafkaTemplate.send(AppConstants.VIDEO_PROCESS_REQUEST_TOPIC, event);
    }

    public Metadata createVideoMetadata(final String userId) {
        final var videoMetadata = Metadata.builder()
                .userId(userId)
                .title("")
                .description("")
                .views(0L)
                .likes(0L)
                .dislikes(0L)
                .build();
        return metadataRepository.save(videoMetadata);
    }

}
