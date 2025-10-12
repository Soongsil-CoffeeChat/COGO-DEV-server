package com.soongsil.CoffeeChat.domain.chat.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soongsil.CoffeeChat.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query(
            "SELECT DISTINCT cr FROM ChatRoom cr JOIN cr.participants p WHERE p.user.username = :username")
    Page<ChatRoom> findAllByUserName(@Param("username") String userName, Pageable pageable);

    @Query("SELECT cr from ChatRoom cr join fetch cr.application a where cr.id = :chatRoomId")
    Optional<ChatRoom> findWithApplicationById(@Param("chatRoomId") Long chatRoomID);
}
