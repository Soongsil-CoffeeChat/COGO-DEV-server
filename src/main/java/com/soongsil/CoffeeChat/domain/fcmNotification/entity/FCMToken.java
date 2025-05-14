package com.soongsil.CoffeeChat.domain.fcmNotification.entity;

import com.soongsil.CoffeeChat.domain.user.entity.User;
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
    @JoinColumn(name = "user_id")  // ✔ 일반적으로 user_id로 명명
    private User user;

    private String token;

    public FCMToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
