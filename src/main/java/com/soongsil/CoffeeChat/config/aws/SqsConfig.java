package com.soongsil.CoffeeChat.config.aws;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

public class SqsConfig {
    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;
    // 클라이언트 설정
    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .credentialsProvider(() -> new AwsCredentials() {
                    @Override
                    public String accessKeyId() {
                        return accessKey;
                    }
                    @Override
                    public String secretAccessKey() {
                        return secretKey;
                    }
                })
                .region(Region.of(region))
                .build();
    }
    // Listener Factory 설정 (Listener쪽에서만 설정하면 됨)
    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory() {
        return SqsMessageListenerContainerFactory
                .builder()
                .sqsAsyncClient(sqsAsyncClient())
                .build();
    }

    // 메세지 발송을 위한 SQS 템플릿 설정 (Sender쪽에서만 설정하면 됨)
    @Bean
    public SqsTemplate sqsTemplate() {
        return SqsTemplate.newTemplate(sqsAsyncClient());
    }
}
