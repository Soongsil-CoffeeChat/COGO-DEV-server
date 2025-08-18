package com.soongsil.CoffeeChat.domain.chat.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.soongsil.CoffeeChat.global.BaseEntity;

import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomUser> participants = new ArrayList<>();
}
