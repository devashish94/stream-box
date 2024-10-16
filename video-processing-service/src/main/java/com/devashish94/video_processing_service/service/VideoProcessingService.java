package com.devashish94.video_processing_service.service;

import com.devashish94.video_processing_service.constant.AppConstants;
import com.devashish94.video_processing_service.dto.ResolutionProcessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class VideoProcessingService {

    private static final Logger log = LoggerFactory.getLogger(VideoProcessingService.class);

    private final MinioService minioService;

    private final FFmpegService ffmpegService;

    public VideoProcessingService(final MinioService minioService, final FFmpegService ffmpegService) {
        this.minioService = minioService;
        this.ffmpegService = ffmpegService;
    }

    public void startVideoProcessing(final ResolutionProcessEvent event) throws Exception {
        final Path localTempVideoPath = Path.of(System.getProperty("user.dir"), AppConstants.LOCAL_PARENT_TEMP_UPLOAD_DIRECTORY, event.getVideoId(), event.getObjectName());
        log.info("localTempVideoPath: {}, parent: {}", localTempVideoPath, localTempVideoPath.getParent());
        if (!Files.exists(localTempVideoPath.getParent())) {
            Files.createDirectories(localTempVideoPath.getParent());
        }

        try {
            minioService.downloadVideo(event.getBucketName(), event.getObjectName(), localTempVideoPath);
        } catch (Exception e) {
            log.error("Exception downloading temp video from Minio for processing, filename: {}, videoId: {}, resolution: {}", event.getObjectName(), event.getVideoId(), event.getResolution());
            log.error(Arrays.toString(e.getStackTrace()));
            throw e;
        }

        final Path localOutputDirectoryPath = Path.of(System.getProperty("user.dir"), AppConstants.LOCAL_PARENT_TEMP_UPLOAD_DIRECTORY, event.getVideoId(), "output");
        log.info("localOutputDirectoryPath: {}", localOutputDirectoryPath);
        if (!Files.exists(localOutputDirectoryPath)) {
            Files.createDirectories(localOutputDirectoryPath);
        }

        try {
            ffmpegService.convertVideoToHls(localTempVideoPath, localOutputDirectoryPath, event.getResolution());
        } catch (Exception e) {
            log.error("Exception while converting the video to HLS format, filename: {}, videoId: {}, resolution: {}", event.getObjectName(), event.getVideoId(), event.getResolution());
            log.error(e.getMessage());
            throw e;
        }

        try {
            minioService.uploadFinalOutputToMinio(localOutputDirectoryPath, event.getBucketName());
        } catch (Exception e) {
            log.error("Exception while uploading output directory files to MinIO, videoId: {}, resolution: {}", event.getBucketName(), event.getResolution());
            log.error(e.getMessage());
            throw e;
        }

    }

    public String retrieveVideoResolution(String resolution, String bucketName) throws Exception {
        final Path localTempVideoPath = Path.of(System.getProperty("user.dir"), AppConstants.LOCAL_PARENT_TEMP_UPLOAD_DIRECTORY, bucketName, "output", String.format("%s_000.ts", resolution));
        if (!Files.exists(localTempVideoPath)) {
            log.error("Encoded video chunk {}_000.ts does not exist", resolution);
            throw new IOException("Encoded .ts file does not exist: " + resolution);
        }

        return getVideoResolution(localTempVideoPath);
    }

    public String getVideoResolution(final Path videoFilePath) throws Exception {
        return runCommand(
                "ffprobe", "-v", "error",
                "-select_streams", "v:0",
                "-show_entries", "stream=width,height",
                "-of", "csv=p=0",
                String.format("%s", videoFilePath)
        );
    }

    public String retrieveVideoBitrate(String resolution, String bucketName) throws Exception {
        final Path localTempVideoPath = Path.of(System.getProperty("user.dir"), AppConstants.LOCAL_PARENT_TEMP_UPLOAD_DIRECTORY, bucketName, "output", String.format("%s_000.ts", resolution));
        if (!Files.exists(localTempVideoPath)) {
            log.error("Encoded video chunk {}_000.ts does not exist", resolution);
            throw new IOException("Encoded .ts file does not exist: " + resolution);
        }
        return getVideoBitrate(localTempVideoPath);
    }

    public String getVideoBitrate(final Path videoFilePath) throws Exception {
        return runCommand(
                "ffprobe", "-v", "error",
                "-show_entries", "format=bit_rate",
                "-of", "csv=p=0",
                String.format("%s", videoFilePath)
        );
    }

    public String runCommand(String... args) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            process.waitFor();
            if (line != null) {
                return line;
            }
        }

        return null;
    }

}
