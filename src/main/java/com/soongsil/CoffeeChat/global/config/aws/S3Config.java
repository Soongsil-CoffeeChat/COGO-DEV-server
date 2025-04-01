package com.soongsil.CoffeeChat.global.config.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {
    @Value("${cloud.aws.credentials.access-key}") // AWS 액세스 키를 application.properties 또는
    // application.yml 파일에서 가져옵니다.
    private String awsAccessKey;

    @Value("${cloud.aws.credentials.secret-key}") // AWS 비밀 키를 application.properties 또는
    // application.yml 파일에서 가져옵니다.
    private String awsSecretKey;

    @Value("${cloud.aws.region.static}") // AWS 리전을 application.properties 또는 application.yml 파일에서
    // 가져옵니다.
    private String region;

    @Bean // 이 어노테이션은 이 메소드가 Spring Bean을 생성하는 팩토리 메소드임을 나타냅니다.
    public AmazonS3 s3client() {
        BasicAWSCredentials awsCredentials =
                new BasicAWSCredentials(
                        awsAccessKey, awsSecretKey); // AWS 액세스 키와 비밀 키를 사용하여 AWS 자격 증명을 생성합니다.

        // AmazonS3 클라이언트를 생성합니다. 이 클라이언트는 AWS S3 서비스와 상호 작용하는 데 사용됩니다.
        // 클라이언트는 설정된 리전과 자격 증명을 사용합니다.
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
