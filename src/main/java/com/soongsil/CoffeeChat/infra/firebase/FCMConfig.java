package com.soongsil.CoffeeChat.infra.firebase;

import java.io.IOException;
import java.io.InputStream;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FCMConfig {

    @PostConstruct
    public void init() {
        try (InputStream serviceAccount =
                     getClass()
                             .getClassLoader()
                             .getResourceAsStream("firebase-serviceAccount-File.json")) {
            if (serviceAccount == null) {
                throw new IllegalStateException("Firebase service account file not found");
            }

            FirebaseOptions options =
                    FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .setDatabaseUrl("https://example-ac805.firebaseio.com")
                            .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
