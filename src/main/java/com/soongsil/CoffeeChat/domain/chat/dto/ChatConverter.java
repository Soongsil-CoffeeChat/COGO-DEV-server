package com.soongsil.CoffeeChat.domain.chat.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse.*;
import com.soongsil.CoffeeChat.domain.chat.entity.Chat;
import com.soongsil.CoffeeChat.domain.chat.entity.ChatRoom;
import com.soongsil.CoffeeChat.domain.chat.entity.ChatRoomUser;
import com.soongsil.CoffeeChat.domain.user.entity.User;

public class ChatConverter {

    public static ChatRoomResponse toChatRoomResponse(
            ChatRoom chatRoom,
            String lastChat,
            List<ChatParticipantResponse> participants,
            LocalDateTime updatedAt) {

        return ChatRoomResponse.builder()
                .roomId(chatRoom.getId())
                .lastChat(lastChat)
                .participants(participants)
                .updatedAt(updatedAt)
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
                                                .name(p.getUser().getName())
                                                .profileImage(p.getUser().getPicture())
                                                .build())
                        .collect(Collectors.toList());

        return ChatRoomDetailResponse.builder()
                .roomId(chatRoom.getId())
                .participants(participantResponses)
                .build();
    }

    public static ChatMessageResponse toChatMessageResponse(Chat chat) {
        return ChatMessageResponse.builder()
                .chatId(chat.getId())
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
            Page<ChatRoom> chatRoomPage,
            List<String> lastChats,
            List<List<ChatParticipantResponse>> partiesList,
            List<LocalDateTime> updatedAts) {

        final List<ChatRoom> rooms = chatRoomPage.getContent();
        final int n = rooms.size();

        // 최근 메시지 가져옴
        List<ChatRoomResponse> content =
                IntStream.range(0, n)
                        .mapToObj(
                                i -> {
                                    ChatRoom room = rooms.get(i);

                                    String last =
                                            (i < lastChats.size() && lastChats.get(i) != null)
                                                    ? lastChats.get(i)
                                                    : "";

                                    List<ChatParticipantResponse> parties =
                                            (i < partiesList.size() && partiesList.get(i) != null)
                                                    ? partiesList.get(i)
                                                    : List.of();

                                    LocalDateTime updatedAt =
                                            (updatedAts != null
                                                            && i < updatedAts.size()
                                                            && updatedAts.get(i) != null)
                                                    ? updatedAts.get(i)
                                                    : room.getUpdatedDate();
                                    return toChatRoomResponse(room, last, parties, updatedAt);
                                })
                        .toList();

        // ChatRoomPageResponse

        return ChatRoomPageResponse.builder()
                .content(content)
                .pageNumber(chatRoomPage.getNumber())
                .pageSize(chatRoomPage.getSize())
                .totalElements(chatRoomPage.getTotalElements())
                .totalPages(chatRoomPage.getTotalPages())
                .last(chatRoomPage.isLast())
                .build();
    }

    public static ChatRoom toChatRoom(Application application, List<User> userList) {
        ChatRoom chatRoom = ChatRoom.builder().application(application).build();
        List<ChatRoomUser> chatRoomUserList =
                userList.stream()
                        .map(user -> ChatRoomUser.builder().chatRoom(chatRoom).user(user).build())
                        .toList();
        chatRoom.getParticipants().addAll(chatRoomUserList);
        return chatRoom;
    }

    public static ChatApplicationResponse toChatApplicationResponse(Application application) {
        return ChatApplicationResponse.builder()
                .applicationId(application.getId())
                .date(application.getPossibleDate().getDate())
                .startTime(application.getPossibleDate().getStartTime())
                .build();
    }
}
