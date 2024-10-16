package com.devashish94.video_processing_service.consumer;

import com.devashish94.video_processing_service.constant.AppConstants;
import com.devashish94.video_processing_service.dto.ResolutionProcessEvent;
import com.devashish94.video_processing_service.dto.VideoResolutionDoneProcessingEvent;
import com.devashish94.video_processing_service.service.VideoProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProcessResolutionConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProcessResolutionConsumer.class);

    private final VideoProcessingService videoProcessingService;

    private final KafkaTemplate<String, VideoResolutionDoneProcessingEvent> kafkaTemplate;

    public ProcessResolutionConsumer(VideoProcessingService videoProcessingService, KafkaTemplate<String, VideoResolutionDoneProcessingEvent> kafkaTemplate) {
        this.videoProcessingService = videoProcessingService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = AppConstants.VIDEO_PROCESS_RESOLUTION_TOPIC,
            groupId = AppConstants.VIDEO_RESOLUTION_PROCESS_GROUP,
            containerFactory = "resolutionProcessContainerFactory"
    )
    public void listenToProcessResolutionRequest(final ResolutionProcessEvent event) {
        try {
            videoProcessingService.startVideoProcessing(event);
            final String videoResolution = videoProcessingService.retrieveVideoResolution(event.getResolution(), event.getBucketName()).replace(',', 'x');
            final String videoBitrate = videoProcessingService.retrieveVideoBitrate(event.getResolution(), event.getBucketName());
            VideoResolutionDoneProcessingEvent doneProcessingEventObject = VideoResolutionDoneProcessingEvent.builder()
                    .videoId(event.getVideoId())
                    .bucketName(event.getVideoId())
                    .bucketName(event.getBucketName())
                    .bitrate(videoBitrate)
                    .resolution(videoResolution)
                    .build();
            kafkaTemplate.send(AppConstants.VIDEO_RESOLUTION_DONE_PROCESSING_TOPIC, doneProcessingEventObject);
        } catch (Exception e) {
            for (var trace : e.getStackTrace()) {
                log.error(trace.toString());
            }
            log.error("Exception while processing the videoId: {}", event.getVideoId());
        }
    }

}
