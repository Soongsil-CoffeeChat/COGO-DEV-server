package com.soongsil.CoffeeChat.domain.chat.entity;

import jakarta.persistence.*;

import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.global.BaseEntity;

import lombok.*;

@Entity
@Table(indexes = {
        @Index(name = "idx_chat_room_createdat", columnList = "chat_room_id, created_at")
})
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
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String message;
}
