package com.soongsil.CoffeeChat.infra.aws.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloudfront.domain:}")
    private String cloudfrontDomain;

    public String uploadProfileImage(MultipartFile file, String uploadDir) {
        String originalFileName = file.getOriginalFilename();

        if (!isImageFile(Objects.requireNonNull(originalFileName))) {
            throw new IllegalArgumentException("png, jpeg, jpg에 해당하는 파일만 업로드할 수 있습니다.");
        }

        // 백업용 원본 저장
        String originalKey = uploadDir + "/original/" + UUID.randomUUID() + "_" + originalFileName;
        uploadToS3(file, originalKey);

        // resized 버전 생성 및 저장
        String resizedKey = uploadDir + "/" + UUID.randomUUID() + "_resized.jpg";
        try {
            ByteArrayOutputStream resizedOutput = new ByteArrayOutputStream();
            Thumbnails.of(file.getInputStream())
                    .width(1050)
                    .outputFormat("jpg")
                    .outputQuality(0.85)
                    .toOutputStream(resizedOutput);

            byte[] resizedBytes = resizedOutput.toByteArray();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(resizedBytes.length);
            metadata.setContentType("image/jpeg");
            metadata.setCacheControl("public, max-age=604800"); //7일

            amazonS3Client.putObject(
                    bucket,
                    resizedKey,
                    new ByteArrayInputStream(resizedBytes),
                    metadata
            );

            log.info("[S3] 프로필 이미지 리사이즈 완료: 원본={}bytes → 리사이즈={}bytes ({}% 감소))",
                    file.getSize(),
                    resizedBytes.length,
                    Math.round((1 - (double) resizedBytes.length / file.getSize()) * 100));
        } catch (IOException e) {
            log.warn("[S3] Resize 실패, 원본으로 대체: {}", e.getMessage());
            String s3Url = amazonS3Client.getUrl(bucket, originalKey).toString();
            return convertToCloudFrontUrl(s3Url);
        }

        String s3Url = amazonS3Client.getUrl(bucket, resizedKey).toString();
        return convertToCloudFrontUrl(s3Url);
    }


    // 원본 파일 업로드 + cloudFront URL 반환
    public String uploadFile(MultipartFile file, String uploadDir) {
        String originalFileName = file.getOriginalFilename();

        if (!isImageFile(Objects.requireNonNull(originalFileName))) {
            throw new IllegalArgumentException("png, jpeg, jpg에 해당하는 파일만 업로드할 수 있습니다.");
        }

        String fileName = this.getFileName(file, uploadDir);
        uploadToS3(file, fileName);

        String s3Url = amazonS3Client.getUrl(bucket, fileName).toString();
        return convertToCloudFrontUrl(s3Url);
    }

    public void deleteFile(String fileUrl) {
        try {
            String filePath = this.getFilePath(fileUrl);
            this.amazonS3Client.deleteObject(bucket, filePath);
        } catch (IOException e) {
            throw new GlobalException(GlobalErrorCode.BAD_REQUEST);
        }
    }


    // S3 URL -> CloudFront URL
    public String convertToCloudFrontUrl(String s3Url) {
        if (cloudfrontDomain == null || cloudfrontDomain.isBlank()) {
            return s3Url;
        }
        if (s3Url == null || s3Url.isBlank()) {
            return s3Url;
        }
        try {
            URI uri = URI.create(s3Url);
            return "https://" + cloudfrontDomain + uri.getPath();
        } catch (IllegalArgumentException e) {
            log.warn("S3 URL 반환 실패, 원본 반환: {} ", s3Url);
            return s3Url;
        }
    }

    // 프로필 이미지 업데이트
    public String updateProfileImage(String existingFileUrl, MultipartFile newFile, String uploadDir) {
        if (existingFileUrl != null) {
            this.deleteFile(existingFileUrl);
        }
        return this.uploadFile(newFile, uploadDir);
    }


    // 공통 - Json 파일 업로드
    public String uploadJsonFile(String jsonData, String uploadDir, String fileNamePrefix) {
        byte[] bytes = jsonData.getBytes(StandardCharsets.UTF_8);

        String fileName =
                uploadDir + "/" + fileNamePrefix + "_" + System.currentTimeMillis() + ".json";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(bytes.length);
        objectMetadata.setContentType("application/json");

        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            amazonS3Client.putObject(bucket, fileName, inputStream, objectMetadata);
            return amazonS3Client.getUrl(bucket, fileName).toString();
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode.BAD_REQUEST);
        }
    }

    // private
    private void uploadToS3(MultipartFile file, String key) {
        ObjectMetadata metadata = getMetaData(file);
        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(bucket, key, inputStream, metadata);
        } catch (Exception e) {
            // S3 업로드 중 발생하는 AWS 예외 등 포괄적 처리
            throw new GlobalException(GlobalErrorCode.BAD_REQUEST);
        }
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
        objectMetadata.setCacheControl("public, max-age=604800");
        return objectMetadata;
    }

    private String getFilePath(String fileUrl) throws IOException {
        URI uri = URI.create(fileUrl);
        return uri.getPath().substring(1);
    }

}
