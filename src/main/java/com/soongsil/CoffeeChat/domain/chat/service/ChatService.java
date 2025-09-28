package com.soongsil.CoffeeChat.domain.chat.service;

import com.soongsil.CoffeeChat.domain.chat.dto.ChatRequest;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse.ChatMessagePageResponse;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse.ChatMessageResponse;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse.ChatRoomDetailResponse;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse.ChatRoomPageResponse;

public interface ChatService {
    ChatRoomPageResponse getChatRooms(String username, int page, int size);

    ChatRoomDetailResponse createChatRoom(
            String username, ChatRequest.CreateChatRoomRequest request);

    ChatRoomDetailResponse getChatRoomDetail(String username, Long roomId);

    ChatMessagePageResponse getChatMessages(String username, Long roomId, int page, int size);

    void leaveChatRoom(String username, Long roomId);

    ChatMessageResponse sendMessage(String username, ChatRequest.SendMessageRequest request);

    ChatResponse.ChatRoomApplicationResponse getChatRoomApplication(Long applicationId);
}
