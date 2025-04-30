package com.soongsil.CoffeeChat.domain.chat.controller;

import org.springframework.stereotype.Controller;

import com.soongsil.CoffeeChat.domain.chat.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    //
    //    @MessageMapping("/{roomId}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    //    @SendTo("/room/{roomId}")   //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건
    // 앞에 붙어줘야됨
    //    public ChatMessage chat(@DestinationVariable Long roomId, ChatMessage message) {
    //
    //        //채팅 저장
    //        Chat chat = chatService.createChat(roomId, message.getSender(),
    // message.getSenderEmail(), message.getMessage());
    //        return ChatMessage.builder()
    //                .roomId(roomId)
    //                .sender(chat.getSender())
    //                .senderEmail(chat.getSenderEmail())
    //                .message(chat.getMessage())
    //                .build();
    //    }
}
