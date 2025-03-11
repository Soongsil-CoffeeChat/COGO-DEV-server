package com.soongsil.CoffeeChat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Refresh { // 사용 가능 Refresh토큰 DB저장용
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // 어떤 유저의 토큰인지 구분자
    private String refresh; // 토큰
    private String expiration; // 만료시간
}
