package com.devashish94.video_catalog_service.repository;

import com.devashish94.video_catalog_service.entity.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, UUID> {
}
