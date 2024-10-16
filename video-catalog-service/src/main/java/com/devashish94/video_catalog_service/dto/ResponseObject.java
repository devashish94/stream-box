package com.devashish94.video_catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseObject<T> {

    private String status;

    private int statusCode;

    private T data;

}
