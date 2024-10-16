package com.devashish94.video_processing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ResolutionProcessEvent {

    private String videoId;

    private String bucketName;

    private String objectName;

    private String resolution;

}
