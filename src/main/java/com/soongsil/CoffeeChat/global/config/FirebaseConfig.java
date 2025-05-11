package com.soongsil.CoffeeChat.global.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credential.json}")
    private String firebaseCredential;

    @PostConstruct
    public void init() throws IOException {
        // JSON 문자열을 바이트 스트림으로 변환
        try (InputStream serviceAccount =
                new ByteArrayInputStream(firebaseCredential.getBytes(StandardCharsets.UTF_8))) {

            // 옵션 빌드
            FirebaseOptions options =
                    FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            // 필요하면 Database URL 등 추가
                            // .setDatabaseUrl("https://your-project.firebaseio.com")
                            .build();

            // 한 번만 초기화
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }
    }
}
