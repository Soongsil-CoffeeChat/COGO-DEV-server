package com.soongsil.CoffeeChat.infra.aws.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private final String bucket;

    public String uploadFile(MultipartFile file, String uploadDir) {
        String originalFileName = file.getOriginalFilename();

        if (!isImageFile(Objects.requireNonNull(originalFileName))) {
            throw new IllegalArgumentException("png, jpeg, jpg에 해당하는 파일만 업로드할 수 있습니다.");
        }

        ObjectMetadata objectMetadata = this.getMetaData(file);
        String fileName = this.getFileName(file, uploadDir);

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(bucket, fileName, inputStream, objectMetadata);
            amazonS3Client.getUrl(bucket, originalFileName);
        } catch (IOException e) {
            throw new GlobalException(GlobalErrorCode.BAD_REQUEST);
        }
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void deleteFile(String fileUrl) {
        try {
            String filePath = this.getFilePath(fileUrl);
            this.amazonS3Client.deleteObject(bucket, filePath);
        } catch (IOException e) {
            throw new GlobalException(GlobalErrorCode.BAD_REQUEST);
        }
    }

    public String updateFile(String existingFileUrl, MultipartFile newFile, String uploadDir) {
        if (existingFileUrl != null) {
            this.deleteFile(existingFileUrl);
        }
        return this.uploadFile(newFile, uploadDir);
    }

    private boolean isImageFile(String fileName) {
        List<String> allowedExtensions = List.of("png", "jpg", "jpeg");
        String extension =
                fileName.contains(".")
                        ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase()
                        : "";
        return allowedExtensions.contains(extension.toLowerCase());
    }

    private String getFileName(MultipartFile file, String uploadDir) {
        return uploadDir + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
    }

    private ObjectMetadata getMetaData(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        return objectMetadata;
    }

    private String getFilePath(String fileUrl) throws IOException {
        URI uri = URI.create(fileUrl);
        return uri.getPath().substring(1);
    }
}
