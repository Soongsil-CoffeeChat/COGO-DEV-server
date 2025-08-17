package com.soongsil.CoffeeChat.global.config.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Config {
    @Value("${cloud.aws.credentials.access-key}")
    private final String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private final String secretKey;

    @Value("${cloud.aws.region.static}")
    private final String region;

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client)
                AmazonS3ClientBuilder.standard()
                        .withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                        .build();
    }
}
