package com.soongsil.CoffeeChat.domain.fcmNotification.entity;

import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FCMToken {

    @Id
    @GeneratedValue
    @Column(name = "fcm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User user;


    private String token;

    public FCMToken(User user, String token) {
        this.user = user;
        this.token = token;
    }
}
