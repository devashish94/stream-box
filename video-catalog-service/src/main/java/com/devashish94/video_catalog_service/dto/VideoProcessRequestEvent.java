package com.devashish94.video_catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoProcessRequestEvent {
    private String videoId;
    private String bucketName;
    private String objectName;
}
