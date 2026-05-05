package com.soongsil.CoffeeChat.domain.chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.application.repository.ApplicationRepository;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatConverter;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatRequest;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse.ChatMessageResponse;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse.ChatRoomDetailResponse;
import com.soongsil.CoffeeChat.domain.chat.dto.ChatResponse.ChatRoomPageResponse;
import com.soongsil.CoffeeChat.domain.chat.entity.Chat;
import com.soongsil.CoffeeChat.domain.chat.entity.ChatRoom;
import com.soongsil.CoffeeChat.domain.chat.entity.ChatRoomUser;
import com.soongsil.CoffeeChat.domain.chat.repository.ChatRepository;
import com.soongsil.CoffeeChat.domain.chat.repository.ChatRoomRepository;
import com.soongsil.CoffeeChat.domain.chat.repository.ChatRoomUserRepository;
import com.soongsil.CoffeeChat.domain.push.service.NotificationService;
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
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsernameWithDeleted(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
    }

    private User getOtherPartyByChatRoom(ChatRoom chatRoom, String username) {
        User user = findUserByUsername(username);
        if (user.isMentee()) return chatRoom.getApplication().getMentor().getUser();
        else return chatRoom.getApplication().getMentee().getUser();
    }

    public ChatRoomPageResponse getChatRooms(String username, int page, int size) {
        User currentUser = findUserByUsername(username);
        Pageable pageable = PageRequest.of(page, size);

        Page<ChatRoom> chatRooms =
                chatRoomRepository.findActiveChatRoomsByUserId(currentUser.getId(), pageable);

        List<Optional<Chat>> lastChatOptList =
                chatRooms.getContent().stream()
                        .map(
                                room ->
                                        chatRepository.findTopByChatRoomIdOrderByCreatedAtDesc(
                                                room.getId()))
                        .toList();

        List<String> lastChats =
                lastChatOptList.stream().map(opt -> opt.map(Chat::getMessage).orElse("")).toList();

        List<LocalDateTime> updatedAts =
                IntStream.range(0, chatRooms.getContent().size())
                        .mapToObj(
                                i ->
                                        lastChatOptList
                                                .get(i)
                                                .map(Chat::getCreatedAt)
                                                .orElse(
                                                        chatRooms
                                                                .getContent()
                                                                .get(i)
                                                                .getUpdatedDate()))
                        .toList();

        List<List<ChatResponse.ChatParticipantResponse>> partiesList =
                chatRooms.getContent().stream()
                        .map(
                                room -> {
                                    User otherUser = getOtherPartyByChatRoom(room, username);
                                    ChatResponse.ChatParticipantResponse otherUserDto =
                                            ChatResponse.ChatParticipantResponse.builder()
                                                    .isDeleted(otherUser.getIsDeleted())
                                                    .userId(otherUser.getId())
                                                    .username(otherUser.getUsername())
                                                    .name(otherUser.getName())
                                                    .profileImage(otherUser.getPicture())
                                                    .build();
                                    return List.of(otherUserDto);
                                })
                        .toList();
        return ChatConverter.toChatRoomPageResponse(chatRooms, lastChats, partiesList, updatedAts);
    }

    @Transactional
    public ChatRoomDetailResponse createChatRoom(
            String username, ChatRequest.CreateChatRoomRequest request) {
        User currentUser = findUserByUsername(username);

        List<User> participants = new ArrayList<>();
        participants.add(currentUser);

        if (request.getParticipantUserId().equals(currentUser.getId())) {
            throw new GlobalException(GlobalErrorCode.BAD_REQUEST);
        }

        User otherUser =
                userRepository
                        .findById(request.getParticipantUserId())
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
        participants.add(otherUser);

        Application application =
                applicationRepository
                        .findById(request.getApplicationId())
                        .orElseThrow(
                                () -> new GlobalException(GlobalErrorCode.APPLICATION_NOT_FOUND));

        ChatRoom chatRoom = ChatConverter.toChatRoom(application, participants);
        chatRoomRepository.save(chatRoom);

        return ChatConverter.toChatRoomDetailResponse(chatRoom);
    }

    @Transactional(readOnly = true)
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

    //    @Transactional(readOnly = true)
    //    public ChatMessagePageResponse getChatMessages(
    //            String username, Long roomId, int page, int size) {
    //        User currentUser = findUserByUsername(username);
    //
    //        // 채팅방 존재 확인
    //        if (!chatRoomRepository.existsById(roomId)) {
    //            throw new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND);
    //        }
    //
    //        // 참여자 확인
    //        chatRoomUserRepository
    //                .findByChatRoomIdAndUserId(roomId, currentUser.getId())
    //                .orElseThrow(() -> new
    // GlobalException(GlobalErrorCode.CHATROOM_NOT_PARTICIPANT));
    //
    //        Pageable pageable = PageRequest.of(page, size);
    //        Page<Chat> chats;
    //
    //        chats =
    //                chatRepository.findByChatRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(
    //                        roomId, LocalDateTime.now(), pageable);
    //
    //        // 날짜 맞게 조회 필요 시 이용
    //        //        if (before != null) {
    //        //            chats =
    //        //
    // chatRepository.findByChatRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(
    //        //                            roomId, before, pageable);
    //        //        } else {
    //        //            chats = chatRepository.findByChatRoomIdOrderByCreatedAtDesc(roomId,
    // pageable);
    //        //        }
    //
    //        return ChatConverter.toChatMessagePageResponse(chats);
    //    }
    @Transactional(readOnly = true)
    public ChatResponse.ChatMessageCursorResponse getChatMessages(
            String username,
            Long roomId,
            LocalDateTime cursorCreatedAt,
            Long cursorChatId,
            int size) {
        User currentUser = findUserByUsername(username);

        // 채팅방 존재 확인
        if (!chatRoomRepository.existsById(roomId)) {
            throw new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND);
        }

        // 참여자 확인
        chatRoomUserRepository
                .findByChatRoomIdAndUserId(roomId, currentUser.getId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_PARTICIPANT));

        // hasNext 판단 위해 size+1개 조회
        List<Chat> chats;
        if (cursorCreatedAt == null || cursorChatId == null) {
            chats = chatRepository.findFirstPageChats(roomId, size + 1);
        } else {
            chats =
                    chatRepository.findChatsByCursor(
                            roomId, cursorCreatedAt, cursorChatId, size + 1);
        }
        return ChatConverter.toChatMessageCursorResponse(chats, size);
    }

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

    @Transactional
    public ChatMessageResponse sendMessage(
            String username, ChatRequest.SendMessageRequest request) {
        User currentUser = findUserByUsername(username);

        // 채팅방 조회
        ChatRoom chatRoom =
                chatRoomRepository
                        .findById(request.getRoomId())
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND));

        // 채팅방 상대 참여자 확인 for push
        User receiver = getOtherPartyByChatRoom(chatRoom, username);

        // 참여자 본인 확인
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

        notificationService.sendChatPush(
                receiver,
                chatRoom.getId(),
                currentUser.getName(), // title
                request.getMessage() // body
                );

        return ChatConverter.toChatMessageResponse(chat);
    }

    @Transactional(readOnly = true)
    public ChatResponse.ChatApplicationResponse getChatRoomApplication(Long chatRoomId) {

        ChatRoom chatRoom =
                chatRoomRepository
                        .findWithApplicationById(chatRoomId)
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND));

        Application application = chatRoom.getApplication();
        if (application == null) throw new GlobalException(GlobalErrorCode.APPLICATION_NOT_FOUND);

        return ChatConverter.toChatApplicationResponse(application);
    }
}
