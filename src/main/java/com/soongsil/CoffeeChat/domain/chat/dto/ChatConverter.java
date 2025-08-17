package com.soongsil.CoffeeChat.domain.chat.dto;

import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse.*;
import com.soongsil.CoffeeChat.domain.chat.entity.Chat;
import com.soongsil.CoffeeChat.domain.chat.entity.ChatRoom;
import com.soongsil.CoffeeChat.domain.chat.entity.ChatRoomUser;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class ChatConverter {

    public static ChatRoomResponse toChatRoomResponse(ChatRoom chatRoom, String lastChat) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .lastChat(lastChat)
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    public static ChatRoomDetailResponse toChatRoomDetailResponse(ChatRoom chatRoom) {

        List<ChatParticipantResponse> participantResponses =
                chatRoom.getParticipants().stream()
                        .map(
                                p ->
                                        ChatParticipantResponse.builder()
                                                .userId(p.getUser().getId())
                                                .username(p.getUser().getUsername())
                                                .profileImage(p.getUser().getPicture())
                                                .build())
                        .collect(Collectors.toList());

        return ChatRoomDetailResponse.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .participants(participantResponses)
                .build();
    }

    public static ChatMessageResponse toChatMessageResponse(Chat chat) {
        return ChatMessageResponse.builder()
                .id(chat.getId())
                .senderId(chat.getSender().getId())
                .message(chat.getMessage())
                .createdAt(chat.getCreatedAt())
                .build();
    }

    public static ChatMessagePageResponse toChatMessagePageResponse(Page<Chat> chatPage) {
        List<ChatMessageResponse> messages =
                chatPage.getContent().stream()
                        .map(chat -> toChatMessageResponse(chat))
                        .collect(Collectors.toList());

        return ChatMessagePageResponse.builder()
                .content(messages)
                .pageNumber(chatPage.getNumber())
                .pageSize(chatPage.getSize())
                .totalElements(chatPage.getTotalElements())
                .totalPages(chatPage.getTotalPages())
                .last(chatPage.isLast())
                .build();
    }

    public static ChatRoomPageResponse toChatRoomPageResponse(
            Page<ChatRoom> chatRoomPage, List<String> lastChats) {

        List<ChatRoomResponse> content =
                chatRoomPage.getContent().stream()
                        .map(
                                room -> {
                                    int index = chatRoomPage.getContent().indexOf(room);
                                    String lastChat =
                                            index < lastChats.size() ? lastChats.get(index) : "";
                                    return toChatRoomResponse(room, lastChat);
                                })
                        .collect(Collectors.toList());

        return ChatRoomPageResponse.builder()
                .content(content)
                .pageNumber(chatRoomPage.getNumber())
                .pageSize(chatRoomPage.getSize())
                .totalElements(chatRoomPage.getTotalElements())
                .totalPages(chatRoomPage.getTotalPages())
                .last(chatRoomPage.isLast())
                .build();
    }

    public static ChatRoom toChatRoom(
            ChatRequest.CreateChatRoomRequest request, List<User> userList) {
        ChatRoom chatRoom = ChatRoom.builder().name(request.getName()).build();
        List<ChatRoomUser> chatRoomUserList =
                userList.stream()
                        .map(user -> ChatRoomUser.builder().chatRoom(chatRoom).user(user).build())
                        .toList();
        chatRoom.getParticipants().addAll(chatRoomUserList);
        return chatRoom;
    }
}
