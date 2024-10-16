package com.devashish94.video_processing_service.service;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FFmpegService {

    public static String getBitrate(String resolution) {
        return switch (resolution) {
            case "144p" -> "200k";
            case "360p" -> "500k";
            case "720p" -> "700k";
            case "1080p" -> "800k";
            default -> "400k";
        };
    }

    private static final Logger log = LoggerFactory.getLogger(FFmpegService.class);
    private static final String FFmpeg_EXECUTABLE = "ffmpeg";
    private static final String FFPREP_EXECUTABLE = "ffprobe";
    private static final String TIME_PATTERN_REGEX = "time=(\\d{2}:\\d{2}:\\d{2}\\.\\d{2})";

    private final RedissonClient redissonClient;

    @Autowired
    public FFmpegService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void convertVideoToHls(final Path filePath, final Path outputDirectory, final String resolution) throws IOException, InterruptedException {

        final String videoId = outputDirectory.getParent().getFileName().toString();

        log.info("ffmpeg input filePath: {}", filePath.toString());

        final List<String> command = List.of(
                FFmpeg_EXECUTABLE,
                "-i", filePath.toString(),
                "-progress", "-", "-nostats",
                "-vf", String.format("scale=-2:%s", resolution.substring(0, resolution.length() - 1)),
                "-b:v", getBitrate(resolution),
                "-maxrate", getBitrate(resolution),
                "-bufsize", getBitrate(resolution),
                "-f", "hls",
                "-hls_time", "4",
                "-hls_list_size", "0",
                "-hls_playlist_type", "vod",
                "-threads", "1",
                "-profile:v", "baseline",
                "-preset", "ultrafast",
                "-hls_segment_filename", outputDirectory.resolve(resolution + "_%03d.ts").toString(),
                outputDirectory.resolve(resolution + ".m3u8").toString()
        );

        log.info("ffmpeg command: {}", command);

        final ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        final Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            final String duration = getDuration(filePath);
            final Pattern timePattern = Pattern.compile(TIME_PATTERN_REGEX);

            reader.lines().forEach(line -> {
                final Matcher matcher = timePattern.matcher(line);
                if (matcher.find()) {
                    String time = matcher.group(1);
                    double progress = calculateProgress(time, duration);
                    log.info("Processing progress for videoId {}@{}: {}%", videoId, resolution, progress);

                    // Update progress in Redis
                    updateProgressInRedis(videoId, resolution, progress);
                }
            });
        } catch (Exception e) {
            log.error("Something went wrong while running ffmpeg for videoId: {}, resolution: {}", videoId, resolution, e);
            throw e;
        }

        final int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg process exited with error code: " + exitCode);
        }

        log.info("Video processing completed for video: {}", videoId);

        // Optionally, set progress to 100% upon completion
        updateProgressInRedis(videoId, resolution, 100.0);
    }

    private String getDuration(Path filePath) throws IOException, InterruptedException {
        ProcessBuilder durationProcess = new ProcessBuilder(FFPREP_EXECUTABLE, "-v", "error", "-show_entries",
                "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", filePath.toString());

        Process process = durationProcess.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String duration = reader.readLine();
            process.waitFor();
            return duration != null ? duration : "1"; // Handle null duration
        }
    }

    private double calculateProgress(String currentTime, String totalDuration) {
        double current = parseTime(currentTime);
        double total = Double.parseDouble(totalDuration);
        double progress = (current / total) * 100;
        // Ensure progress does not exceed 100%
        return Math.min(progress, 100.0);
    }

    private double parseTime(String time) {
        String[] parts = time.split(":");
        return Double.parseDouble(parts[0]) * 3600
                + Double.parseDouble(parts[1]) * 60
                + Double.parseDouble(parts[2]);
    }

    private void updateProgressInRedis(String videoId, String resolution, double progress) {
        String redisKey = "video:" + videoId + ":progress";
        RMap<String, Double> progressMap = redissonClient.getMap(redisKey);
        progressMap.put(resolution, progress);
        log.debug("Updated Redis - Key: {}, Resolution: {}, Progress: {}%", redisKey, resolution, progress);
    }
}
