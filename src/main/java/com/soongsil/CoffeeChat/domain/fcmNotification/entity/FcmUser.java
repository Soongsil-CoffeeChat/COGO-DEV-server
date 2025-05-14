package com.soongsil.CoffeeChat.domain.fcmNotification.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class FcmUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "fcmUser", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<FCMToken> fcmTokens = new ArrayList<>();

    public List<FCMToken> getFcmTokens() {
        return fcmTokens;
    }

    public Long getId() {
        return id;
    }
}
