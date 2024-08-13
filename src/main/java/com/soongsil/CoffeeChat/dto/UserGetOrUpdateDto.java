package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGetOrUpdateDto {
    private String name;
    private String email;
    private String phoneNum;

    public static UserGetOrUpdateDto toDto(User user){
        return UserGetOrUpdateDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phoneNum(user.getPhoneNum())
                .build();
    }
}
