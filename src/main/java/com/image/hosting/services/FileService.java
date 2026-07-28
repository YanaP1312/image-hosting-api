package com.image.hosting.services;

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

import java.io.IOException;

@Service
public class FileService {

    private final S3Client s3Client;
    private final String bucket;

    public FileService(S3Client s3Client, @Value("${b2.bucket}") String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public void upload(MultipartFile file, String key) throws Exception {

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket).key(key)
                        .contentType(file.getContentType()).build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );
    }


    public byte[] download(String key) throws IOException {
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(
                GetObjectRequest.builder().bucket(bucket).key(key).build()
        );
        return response.readAllBytes();
    }


    public void delete(String key) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket).key(key).build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from storage", e);
        }
    }
}
