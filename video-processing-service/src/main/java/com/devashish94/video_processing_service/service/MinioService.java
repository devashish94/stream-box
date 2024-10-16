package com.devashish94.video_processing_service.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Service
public class MinioService {

    private static final Logger log = LoggerFactory.getLogger(MinioService.class);

    private final MinioClient minioClient;

    public MinioService(final MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void downloadVideo(String bucketName, String objectName, Path downloadTo) throws Exception {
        try (final InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build()
        )) {
            if (Files.exists(downloadTo)) {
                return;
            }
            Files.copy(inputStream, downloadTo);
        } catch (Exception e) {
            log.error("Could not download video: {}, videoId: {}, downloadTo: {}", objectName, bucketName, downloadTo);
            throw e;
        }
    }

    public void uploadFinalOutputToMinio(final Path localOutputDirectory, final String bucketName) throws Exception {
        try (final Stream<Path> paths = Files.list(localOutputDirectory)) {
            for (final Path path : paths.toList()) {
                try (var inputStream = Files.newInputStream(path)) {
                    minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path.getFileName().toString())
                            .stream(inputStream, Files.size(path), -1)
                            .build()
                    );
                } catch (Exception e) {
                    log.error("Unable to Put Object: {}, of videoId: {}, error: {}", path.getFileName(), bucketName, e.toString());
                    throw e;
                }
            }
        }
    }

}
