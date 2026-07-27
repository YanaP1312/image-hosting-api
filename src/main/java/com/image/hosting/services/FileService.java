package com.image.hosting.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileService {

    private final S3Client s3Client;

    @Value("${b2.bucket}")
    private String bucket;

    public String upload(MultipartFile file) throws Exception {
        String key = "uploads/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket).key(key)
                        .contentType(file.getContentType()).build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );
        return key;
    }


    public byte[] download(String key) throws IOException {
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(
                GetObjectRequest.builder().bucket(bucket).key(key).build()
        );
        return response.readAllBytes();
    }


    public void delete(String key) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucket).key(key).build()
        );
    }
}
