package com.devashish94.video_catalog_service.service;

import com.devashish94.video_catalog_service.constant.AppConstants;
import com.devashish94.video_catalog_service.controller.VideoCatalogController;
import io.minio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class VideoStorageService {

    private static final Logger log = LoggerFactory.getLogger(VideoCatalogController.class);

    @Autowired
    private MinioClient minioClient;

    public String uploadVideoToTemporaryStorage(final MultipartFile file, final String videoId) throws Exception {
        boolean isBucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(videoId).build());
        if (!isBucketExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(videoId).build());
            final String bucketPolicy = "{" +
                    "\"Version\": \"2012-10-17\"," +
                    "\"Statement\": [{" +
                    "\"Effect\": \"Allow\"," +
                    "\"Principal\": {\"AWS\": \"*\"}," +
                    "\"Action\": \"s3:GetObject\"," +
                    "\"Resource\": [\"arn:aws:s3:::" + videoId + "/*\"]" +
                    "}]" +
                    "}";
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(videoId)
                    .config(bucketPolicy)
                    .build()
            );
        }

        final String filename = "/temp/" + file.getOriginalFilename();
        try (final InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(videoId)
                    .object(filename)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return filename;
        } catch (Exception e) {
            log.error("Exception while uploading video to temp location, filename: {}, videoId: {}", filename, videoId);
            throw new Exception(e);
        }
    }

    public Path uploadVideoToLocalFileSystem(MultipartFile file, final UUID videoId) throws IOException {
        final Path uploadDir = Paths.get(AppConstants.TEMP_VIDEO_UPLOADS_DIRECTORY);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        final Path destinationPath = uploadDir.resolve(videoId.toString());
        try {
            file.transferTo(destinationPath);
            return destinationPath;
        } catch (IOException e) {
            log.info("Exception during videoId: {} transfer to destination path: {}", videoId, destinationPath);
            throw e;
        }
    }

}
