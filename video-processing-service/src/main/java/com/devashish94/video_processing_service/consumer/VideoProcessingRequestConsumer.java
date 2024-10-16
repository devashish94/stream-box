package com.devashish94.video_processing_service.consumer;

import com.devashish94.video_processing_service.constant.AppConstants;
import com.devashish94.video_processing_service.dto.ResolutionProcessEvent;
import com.devashish94.video_processing_service.dto.VideoProcessRequestEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoProcessingRequestConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public VideoProcessingRequestConsumer(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = AppConstants.VIDEO_PROCESS_REQUEST_TOPIC,
            groupId = AppConstants.VIDEO_PROCESS_GROUP,
            containerFactory = "videoProcessRequestContainerFactory"
    )
    public void listenToVideoProcessingRequest(final VideoProcessRequestEvent event) {
        for (String resolution : List.of("1080p", "360p", "144p")) {
            kafkaTemplate.send(
                    AppConstants.VIDEO_PROCESS_RESOLUTION_TOPIC,
                    ResolutionProcessEvent.builder().
                            videoId(event.getVideoId())
                            .bucketName(event.getBucketName())
                            .objectName(event.getObjectName())
                            .resolution(resolution)
                            .build()
            );
        }
    }

}
