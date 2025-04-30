package com.soongsil.CoffeeChat.domain.chat.entity;

import jakarta.persistence.*;

import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.global.BaseEntity;

import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String message;
}
