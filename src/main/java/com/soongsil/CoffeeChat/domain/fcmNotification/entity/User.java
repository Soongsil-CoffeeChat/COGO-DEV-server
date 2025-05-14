package com.soongsil.CoffeeChat.domain.fcmNotification.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<FCMToken> fcmTokens = new ArrayList<>();

    public List<FCMToken> getFcmTokens() {
        return fcmTokens;
    }

   public Long getId(){
        return id;
   }
}
