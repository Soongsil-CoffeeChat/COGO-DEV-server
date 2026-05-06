package com.soongsil.CoffeeChat.global.migration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

// 기존 유저 프로필 이미지 마이그레이션


@Slf4j
@Component
@Profile("migration")
@RequiredArgsConstructor
public class ProfileImageMigration implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloudfront.domain:}")
    private String cloudfrontDomain;

    @Override
    public void run(String... args) {
        log.info("================================================");
        log.info("[Migration] 프로필 이미지 리사이즈 마이그레이션 시작");
        log.info("================================================");

        List<User> targets = userRepository.findAll().stream()
                .filter(u -> u.getPicture() != null && !u.getPicture().isBlank())
                .filter(u -> !u.getIsDeleted())
                .filter(u -> !u.getPicture().contains("_resized.jpg"))  // 이미 처리된 유저 스킵
                .filter(u -> !u.getPicture().contains("cloudfront"))    // 이미 CDN URL인 유저 스킵
                .toList();

        log.info("[Migration] 마이그레이션 대상: {}명", targets.size());

        if (targets.isEmpty()) {
            log.info("[Migration] 마이그레이션 대상 없음. 마이그레이션 종료.");
            return;
        }

        int success = 0;
        int failed = 0;

        for (User user : targets) {
            try {
                migrateUserImage(user);
                success++;
                log.info("[Migration] 진행중: {}/{}", success + failed, targets.size());
            } catch (Exception e) {
                failed++;
                log.error("[Migration] 실패 userId={}, picture={}: {}",
                        user.getId(), user.getPicture(), e.getMessage());
            }
        }

        log.info("================================================");
        log.info("[Migration] 완료: 성공={}, 실패={}, 전체={}", success, failed, targets.size());
        log.info("================================================");
    }

    @Transactional
    public void migrateUserImage(User user) throws Exception {
        String pictureUrl = user.getPicture();

        // 1) URL에서 S3 key 추출
        String s3Key = extractS3Key(pictureUrl);
        log.info("[Migration] userId={} 처리 시작: key={}", user.getId(), s3Key);

        // 2) S3에 파일 존재 확인
        if (!amazonS3Client.doesObjectExist(bucket, s3Key)) {
            log.warn("[Migration] S3에 파일 없음, URL만 CDN으로 변환: userId={}", user.getId());
            user.updatePicture(convertToCloudFrontUrl(pictureUrl));
            return;
        }

        // 3) S3에서 원본 다운로드
        S3Object s3Object = amazonS3Client.getObject(bucket, s3Key);
        long originalSize = s3Object.getObjectMetadata().getContentLength();

        // 4) 원본을 original/ 폴더로 백업 복사
        String backupKey = s3Key.contains("/")
                ? s3Key.replaceFirst("/", "/original/")
                : "original/" + s3Key;
        amazonS3Client.copyObject(bucket, s3Key, bucket, backupKey);
        log.info("[Migration] 원본 백업 완료: {}", backupKey);

        // 5) 리사이즈
        byte[] resizedBytes;
        try (InputStream inputStream = s3Object.getObjectContent()) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Thumbnails.of(inputStream)
                    .width(1050)
                    .outputFormat("jpg")
                    .outputQuality(0.85)
                    .toOutputStream(output);
            resizedBytes = output.toByteArray();
        } catch (Exception e) {
            log.warn("[Migration] 리사이즈 실패, URL만 CDN으로 변환: userId={}, {}",
                    user.getId(), e.getMessage());
            user.updatePicture(convertToCloudFrontUrl(pictureUrl));
            return;
        }

        // 6) 리사이즈 이미지 S3에 업로드
        String resizedKey = "user/" + UUID.randomUUID() + "_resized.jpg";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(resizedBytes.length);
        metadata.setContentType("image/jpeg");
        metadata.setCacheControl("public, max-age=604800");

        amazonS3Client.putObject(
                bucket,
                resizedKey,
                new ByteArrayInputStream(resizedBytes),
                metadata);

        // 7) DB 업데이트 — CloudFront URL로
        String newUrl = convertToCloudFrontUrl(
                amazonS3Client.getUrl(bucket, resizedKey).toString());
        user.updatePicture(newUrl);

        // 8) 기존 S3 키 삭제 (원본은 backup에 있음)
        amazonS3Client.deleteObject(bucket, s3Key);

        long reduction = Math.round((1 - (double) resizedBytes.length / originalSize) * 100);
        log.info("[Migration] 성공 userId={}: {}KB → {}KB ({}% 감소), 새 URL={}",
                user.getId(),
                originalSize / 1024,
                resizedBytes.length / 1024,
                reduction,
                newUrl);
    }

    private String extractS3Key(String url) {
        URI uri = URI.create(url);
        String path = uri.getPath();
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private String convertToCloudFrontUrl(String s3Url) {
        if (cloudfrontDomain == null || cloudfrontDomain.isBlank()) {
            return s3Url;
        }
        try {
            URI uri = URI.create(s3Url);
            return "https://" + cloudfrontDomain + uri.getPath();
        } catch (Exception e) {
            return s3Url;
        }
    }
}