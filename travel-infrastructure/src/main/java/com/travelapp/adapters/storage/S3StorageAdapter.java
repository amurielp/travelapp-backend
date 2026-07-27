package com.travelapp.adapters.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3StorageAdapter {

    private final S3Client s3;

    @Value("${storage.s3.bucket:travelapp-local}")
    private String bucket;

    @Value("${storage.s3.base-url:}")
    private String baseUrl;

    public String upload(MultipartFile file, String prefix) {
        try {
            var key = prefix + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

            s3.putObject(
                PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            var url = baseUrl.isBlank()
                ? "https://%s.s3.amazonaws.com/%s".formatted(bucket, key)
                : baseUrl + "/" + key;

            log.info("storage.uploaded key={} size={}", key, file.getSize());
            return url;

        } catch (Exception e) {
            throw new StorageException("Failed to upload file: " + e.getMessage(), e);
        }
    }
}
