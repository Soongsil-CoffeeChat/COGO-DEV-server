package com.soongsil.CoffeeChat.global.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credential.json}")
    private String firebaseCredentialsJson;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) return FirebaseApp.getInstance();

        byte[] bytes = firebaseCredentialsJson.getBytes(StandardCharsets.UTF_8);
        GoogleCredentials credentials =
                GoogleCredentials.fromStream(new ByteArrayInputStream(bytes));

        FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();

        return FirebaseApp.initializeApp(options);
    }
}
