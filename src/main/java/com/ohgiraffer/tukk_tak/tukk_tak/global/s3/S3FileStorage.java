package com.ohgiraffer.tukk_tak.tukk_tak.global.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileStorage {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.community-bucket}")
    private String bucket;

    // 업로드하고 S3 key를 반환
    public String upload(MultipartFile file, String directory) {
        String key = directory + "/" + UUID.randomUUID() + extensionOf(file.getOriginalFilename());

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return key;
    }


    public void delete(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        } catch (RuntimeException e) {
            log.warn("S3 파일 삭제 실패: {}", key, e);
        }
    }

    // 점 포함해서 반환 (예: ".png"), 확장자가 없으면 빈 문자열
    private String extensionOf(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return dot < 0 ? "" : filename.substring(dot).toLowerCase();
    }
}