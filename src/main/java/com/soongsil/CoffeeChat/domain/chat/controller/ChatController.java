package com.soongsil.CoffeeChat.domain.chat.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.chat.dto.ChatRequest;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse;
import com.soongsil.CoffeeChat.domain.chat.service.ChatService;
import com.soongsil.CoffeeChat.global.annotation.CurrentUsername;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/chat")
@Tag(name = "CHAT", description = "채팅 관련 API")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/rooms")
    @Operation(summary = "채팅방 목록 조회")
    public ResponseEntity<ChatResponse.ChatRoomPageResponse> getChatRooms(
            @Parameter(hidden = true) @CurrentUsername String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(chatService.getChatRooms(username, page, size));
    }

    @PostMapping("/rooms")
    @Operation(summary = "채팅방 생성")
    public ResponseEntity<ChatResponse.ChatRoomDetailResponse> createChatRoom(
            @Parameter(hidden = true) @CurrentUsername String username,
            @RequestBody ChatRequest.CreateChatRoomRequest request) {
        return ResponseEntity.ok(chatService.createChatRoom(username, request));
    }

    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "채팅방 상세 조회")
    public ResponseEntity<ChatResponse.ChatRoomDetailResponse> getChatRoomDetail(
            @Parameter(hidden = true) @CurrentUsername String username, @PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getChatRoomDetail(username, roomId));
    }

    @GetMapping("/rooms/{roomId}/messages")
    @Operation(summary = "채팅방 메시지 목록 조회")
    public ResponseEntity<ChatResponse.ChatMessagePageResponse> getChatMessages(
            @Parameter(hidden = true) @CurrentUsername String username,
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(chatService.getChatMessages(username, roomId, page, size));
    }

    @PostMapping("/rooms/{roomId}/leave")
    @Operation(summary = "채팅방 나가기")
    public ResponseEntity<Void> leaveChatRoom(
            @Parameter(hidden = true) @CurrentUsername String username, @PathVariable Long roomId) {
        chatService.leaveChatRoom(username, roomId);
        return ResponseEntity.ok().build();
    }

    /** 메시지 전송 (STOMP 웹소켓) */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatRequest.SendMessageRequest request, Principal principal) {
        String username = principal.getName();
        ChatResponse.ChatMessageResponse message = chatService.sendMessage(username, request);
        messagingTemplate.convertAndSend("/topic/room." + request.getRoomId(), message);
    }

    @GetMapping("/chatRoom/application/chatRoomId}")
    @Operation(summary = "채팅방과 연결된 코고 조회")
    public ResponseEntity<ChatResponse.ChatRoomApplicationResponse> getApplication(
            @PathVariable("chatRoomId") Long chatRoomId){
        return ResponseEntity.ok(chatService.getChatRoomApplication(chatRoomId));
    }
}
