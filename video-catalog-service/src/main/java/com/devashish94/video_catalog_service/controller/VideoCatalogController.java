package com.devashish94.video_catalog_service.controller;

import com.devashish94.video_catalog_service.dto.VideoProcessRequestEvent;
import com.devashish94.video_catalog_service.dto.VideoUploadResponseDto;
import com.devashish94.video_catalog_service.dto.ResponseObject;
import com.devashish94.video_catalog_service.entity.Metadata;
import com.devashish94.video_catalog_service.service.VideoCatalogService;
import com.devashish94.video_catalog_service.service.VideoStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/video")
public class VideoCatalogController {

    private static final Logger log = LoggerFactory.getLogger(VideoCatalogController.class);
    @Autowired
    private VideoCatalogService videoCatalogService;

    @Autowired
    private VideoStorageService videoStorageService;

    @PostMapping
    public ResponseEntity<ResponseObject<?>> uploadVideo(@RequestHeader("X-USER-ID") final String userId, @RequestParam("file") final MultipartFile file) {
        if (file.isEmpty()) {
            final var responseObject = VideoUploadResponseDto.builder().message("Video File not provided.").build();
            return ResponseEntity.internalServerError().body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value(), responseObject));
        }

        final Metadata videoMetadata = videoCatalogService.createVideoMetadata(userId);
        final String videoId = videoMetadata.getVideoId().toString();

        try {
            final String uploadedVideoObjectName = videoStorageService.uploadVideoToTemporaryStorage(file, videoId);
            final VideoProcessRequestEvent event = VideoProcessRequestEvent.builder()
                    .videoId(videoId)
                    .bucketName(videoId)
                    .objectName(uploadedVideoObjectName)
                    .build();
            videoCatalogService.sendProcessVideoRequest(event);
        } catch (Exception e) {
            log.error(e.getMessage());
            final var responseObject = VideoUploadResponseDto.builder().message("Could not transfer the video to temporary storage.").build();
            return ResponseEntity.internalServerError().body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value(), responseObject));
        }

        final var responseObject = VideoUploadResponseDto.builder().message("Video uploaded, started processing video.").metadata(videoMetadata).build();
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(), responseObject));
    }

}
