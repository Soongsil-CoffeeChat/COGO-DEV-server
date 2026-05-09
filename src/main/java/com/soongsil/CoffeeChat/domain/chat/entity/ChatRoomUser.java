package com.soongsil.CoffeeChat.domain.chat.entity;

import jakarta.persistence.*;

import com.soongsil.CoffeeChat.domain.user.entity.User;

import lombok.*;

@Entity
@Table(indexes = {@Index(name = "idx_chatroom_user", columnList = "chat_room_id, user_id")})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "last_read_chat_id")
    private Long lastReadChatId;

    public void updateLastReadChatId(Long chatId) {
        if (chatId == null) {
            return;
        }

        if (lastReadChatId == null || chatId > lastReadChatId) {
            lastReadChatId = chatId;
        }
    }

}
