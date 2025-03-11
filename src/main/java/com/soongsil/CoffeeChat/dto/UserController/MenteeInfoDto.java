package com.soongsil.CoffeeChat.dto.UserController;

import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.enums.PartEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MenteeInfoDto {
    private PartEnum part;
    private boolean isNewAccount;

    public static MenteeInfoDto toDto(Mentee mentee) {
        return MenteeInfoDto.builder().part(mentee.getPart()).isNewAccount(false).build();
    }
}
