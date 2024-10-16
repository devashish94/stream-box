package com.devashish94.video_catalog_service.dto;

import com.devashish94.video_catalog_service.entity.Metadata;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoUploadResponseDto {

    private String message;

    private Metadata metadata;

}
