package com.devashish94.video_catalog_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Metadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID videoId;

    @Column(nullable = false)
    private String userId;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private Long likes;

    @Column
    private Long dislikes;

    @Column
    private Long views;

}
