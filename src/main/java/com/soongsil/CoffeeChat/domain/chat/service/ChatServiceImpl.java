package com.soongsil.CoffeeChat.domain.chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.domain.application.repository.ApplicationRepository;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatConverter;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatRequest;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse.ChatMessagePageResponse;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse.ChatMessageResponse;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse.ChatRoomDetailResponse;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse.ChatRoomPageResponse;
import com.soongsil.CoffeeChat.domain.chat.entity.Chat;
import com.soongsil.CoffeeChat.domain.chat.entity.ChatRoom;
import com.soongsil.CoffeeChat.domain.chat.entity.ChatRoomUser;
import com.soongsil.CoffeeChat.domain.chat.repository.ChatRepository;
import com.soongsil.CoffeeChat.domain.chat.repository.ChatRoomRepository;
import com.soongsil.CoffeeChat.domain.chat.repository.ChatRoomUserRepository;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    public ChatRoomPageResponse getChatRooms(String username, int page, int size) {
        User currentUser = findUserByUsername(username);
        Pageable pageable = PageRequest.of(page, size);

        Page<ChatRoom> chatRooms =
                chatRoomRepository.findAllByUserName(currentUser.getUsername(), pageable);

        log.trace(">>chatRooms 조회 로그: {}", chatRooms);

        // 각 채팅방의 마지막 메시지 조회
        List<String> lastChats =
                chatRooms.getContent().stream()
                        .map(
                                room -> {
                                    Optional<Chat> lastChat =
                                            chatRepository.findTopByChatRoomIdOrderByCreatedAtDesc(
                                                    room.getId());
                                    return lastChat.map(Chat::getMessage).orElse("");
                                })
                        .collect(Collectors.toList());

        log.trace(">>lastChats 조회 로그: {}", lastChats);

        // 내가 아닌 채팅방 참여자 정보 조회
        List<List<ChatResponse.ChatParticipantResponse>> partiesList =
                chatRooms.getContent().stream()
                        .map(
                                room ->
                                        room.getParticipants().stream()
                                                .map(ChatRoomUser::getUser)
                                                .filter(u -> !u.getId().equals(currentUser.getId()))
                                                .map(
                                                        u ->
                                                                ChatResponse.ChatParticipantResponse
                                                                        .builder()
                                                                        .userId(u.getId())
                                                                        .username(u.getUsername())
                                                                        .profileImage(
                                                                                u.getPicture())
                                                                        .build())
                                                // 읽기 전용 List 형태로 반환
                                                .toList())
                        .toList();
        log.trace(">>chatPartiesList 생성 로그: {}", partiesList);

        return ChatConverter.toChatRoomPageResponse(chatRooms, lastChats, partiesList);
    }

    @Override
    @Transactional
    public ChatRoomDetailResponse createChatRoom(
            String username, ChatRequest.CreateChatRoomRequest request) {
        User currentUser = findUserByUsername(username);

        List<User> participants = new ArrayList<>();
        participants.add(currentUser);

        if (!request.getParticipantId().equals(currentUser.getId())) {
            throw new GlobalException(GlobalErrorCode.BAD_REQUEST);
        }

        User user =
                userRepository
                        .findById(request.getParticipantId())
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
        participants.add(user);

        ChatRoom chatRoom = ChatConverter.toChatRoom(request, participants);
        chatRoomRepository.save(chatRoom);

        return ChatConverter.toChatRoomDetailResponse(chatRoom);
    }

    @Override
    public ChatRoomDetailResponse getChatRoomDetail(String username, Long roomId) {
        User currentUser = findUserByUsername(username);

        // 채팅방 조회
        ChatRoom chatRoom =
                chatRoomRepository
                        .findById(roomId)
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND));

        // 참여자 확인
        chatRoomUserRepository
                .findByChatRoomIdAndUserId(roomId, currentUser.getId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_PARTICIPANT));

        return ChatConverter.toChatRoomDetailResponse(chatRoom);
    }

    @Override
    public ChatMessagePageResponse getChatMessages(
            String username, Long roomId, int page, int size) {
        User currentUser = findUserByUsername(username);

        // 채팅방 존재 확인
        if (!chatRoomRepository.existsById(roomId)) {
            throw new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND);
        }

        // 참여자 확인
        chatRoomUserRepository
                .findByChatRoomIdAndUserId(roomId, currentUser.getId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_PARTICIPANT));

        Pageable pageable = PageRequest.of(page, size);
        Page<Chat> chats;

        chats =
                chatRepository.findByChatRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(
                        roomId, LocalDateTime.now(), pageable);

        // 날짜 맞게 조회 필요 시 이용
        //        if (before != null) {
        //            chats =
        //                    chatRepository.findByChatRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(
        //                            roomId, before, pageable);
        //        } else {
        //            chats = chatRepository.findByChatRoomIdOrderByCreatedAtDesc(roomId, pageable);
        //        }

        return ChatConverter.toChatMessagePageResponse(chats);
    }

    @Override
    @Transactional
    public void leaveChatRoom(String username, Long roomId) {
        User currentUser = findUserByUsername(username);

        // 채팅방 존재 확인
        ChatRoom chatRoom =
                chatRoomRepository
                        .findById(roomId)
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND));

        // 참여자 확인 및 삭제
        ChatRoomUser participant =
                chatRoomUserRepository
                        .findByChatRoomIdAndUserId(roomId, currentUser.getId())
                        .orElseThrow(
                                () ->
                                        new GlobalException(
                                                GlobalErrorCode.CHATROOM_NOT_PARTICIPANT));

        chatRoomUserRepository.delete(participant);

        // 만약 참여자가 없으면 채팅방 삭제
        if (chatRoomUserRepository.findByChatRoomId(roomId).isEmpty()) {
            chatRoomRepository.delete(chatRoom);
        }
    }

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(
            String username, ChatRequest.SendMessageRequest request) {
        User currentUser = findUserByUsername(username);

        // 채팅방 조회
        ChatRoom chatRoom =
                chatRoomRepository
                        .findById(request.getRoomId())
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND));

        // 참여자 확인
        chatRoomUserRepository
                .findByChatRoomIdAndUserId(request.getRoomId(), currentUser.getId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_PARTICIPANT));

        // 채팅 메시지 저장
        Chat chat =
                Chat.builder()
                        .chatRoom(chatRoom)
                        .sender(currentUser)
                        .message(request.getMessage())
                        .build();

        chatRepository.save(chat);

        return ChatConverter.toChatMessageResponse(chat);
    }

    @Override
    @Transactional
    public ChatResponse.ChatRoomApplicationResponse getChatRoomApplication(Long chatRoomId) {

        ChatRoom chatRoom =
                chatRoomRepository
                        .findWithApplicationById(chatRoomId)
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND));

        ChatResponse.ChatRoomApplicationResponse response =
                ChatConverter.toChatRoomApplication(chatRoom.getApplication());

        if (response == null) throw new GlobalException(GlobalErrorCode.APPLICATION_NOT_FOUND);
        return response;
    }

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
    }
}
