package com.devashish94.video_catalog_service.consumer;

import com.devashish94.video_catalog_service.constant.AppConstants;
import com.devashish94.video_catalog_service.dto.VideoResolutionDoneProcessingEvent;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class PlaylistUpdateEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(PlaylistUpdateEventConsumer.class);
    private final MinioClient minioClient;

    public PlaylistUpdateEventConsumer(final MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @KafkaListener(topics = AppConstants.VIDEO_RESOLUTION_DONE_PROCESSING_TOPIC, groupId = AppConstants.PLAYLIST_UPDATE_GROUP)
    public void listenToPlaylistUpdateEvent(final VideoResolutionDoneProcessingEvent event) {
        log.info("Got request to update the master playlist for videoId: {}, for res: {}", event.getVideoId(), event.getResolution());

        String bucketName = event.getVideoId();
        String masterPlaylistName = "master-playlist.m3u8";
        StringBuilder masterPlaylistContent = new StringBuilder();

        try {
            // Attempt to download the master-playlist.m3u8 file
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(masterPlaylistName)
                            .build());

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                masterPlaylistContent.append(line).append("\n");
            }
            reader.close();
            stream.close();

        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                // Initialize the master manifest playlist file if it doesn't exist
                masterPlaylistContent.append("#EXTM3U\n");
                masterPlaylistContent.append("#EXT-X-VERSION:3\n");
                log.warn("Warning: master-playlist.m3u8 file for videoId: {} does not exist. initializing...", event.getVideoId());
            } else {
                log.error("Exception while downloading master playlist for videoId: {} @ {}: ", event.getVideoId(), event.getResolution());
                return;
            }
        } catch (Exception e) {
            log.error("Something went totally wrong downloading master playlist for videoId: {} @ {}: ", event.getVideoId(), event.getResolution());
            return;
        }

        // Append the necessary information for the particular resolution variant playlist
//        masterPlaylistContent.append().append(getBandwidthForResolution(event.getResolution()))
//                .append(",RESOLUTION=").append(event.getResolution()).append("\n");
//        masterPlaylistContent.append(event.getResolution()).append("/playlist.m3u8\n");

        final String variantFileName = event.getResolution().split("x")[1];
        final String content = String.format("#EXT-X-STREAM-INF:BANDWIDTH=%s,RESOLUTION=%s\n%sp.m3u8", event.getBitrate(), event.getResolution(), variantFileName);
        masterPlaylistContent.append(content);


        // Upload the updated master playlist back to the bucket
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(masterPlaylistName)
                            .stream(new ByteArrayInputStream(masterPlaylistContent.toString().getBytes()), masterPlaylistContent.length(), -1)
                            .contentType("application/vnd.apple.mpegurl")
                            .build());

            log.info("Successfully updated master playlist for videoId: {} for {}", event.getVideoId(), event.getResolution());
        } catch (Exception e) {
            log.error("Exception {} uploading updated master playlist for videoId: {} @ {}: ", e, event.getVideoId(), event.getResolution());
        }
    }

}