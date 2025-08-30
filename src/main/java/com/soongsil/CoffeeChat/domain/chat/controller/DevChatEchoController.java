package com.soongsil.CoffeeChat.domain.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DevChatEchoController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.echo")
    public void echo(@Payload DevEchoPayload payload) {
        messagingTemplate.convertAndSend("/topic/room." + payload.getRoomId(), payload);
    }

    public static class DevEchoPayload {
        private Long roomId;
        private String message;

        public DevEchoPayload() {} // 기본 생성자

        public Long getRoomId() {
            return roomId;
        } // getter

        public void setRoomId(Long v) {
            roomId = v;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String v) {
            message = v;
        }
    }
}
