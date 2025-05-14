package com.soongsil.CoffeeChat.domain.fcmNotification.entity;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FCMToken {

    @Id
    @GeneratedValue
    @Column(name = "fcm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private FcmUser fcmUser;

    private String token;

    public FCMToken(FcmUser fcmUser, String token) {
        this.fcmUser = fcmUser;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public FcmUser getFcmUser() {
        return fcmUser;
    }
}
