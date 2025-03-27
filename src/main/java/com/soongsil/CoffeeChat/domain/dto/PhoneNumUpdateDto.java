package com.soongsil.CoffeeChat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PhoneNumUpdateDto {
    private String name;
    private String phoneNum; // 전화번호
    private boolean isNewAccount;
}
