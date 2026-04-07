package com.soongsil.CoffeeChat.domain.auth.entity;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(indexes = {
        @Index(name = "idx_refresh_token", columnList = "refresh")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refresh { // 사용 가능 Refresh토큰 DB저장용
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // 어떤 유저의 토큰인지 구분자
    private String refresh; // 토큰
    private String expiration; // 만료시간
}
