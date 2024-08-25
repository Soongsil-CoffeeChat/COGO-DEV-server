package com.soongsil.CoffeeChat.dto.UserController;

import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.enums.PartEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenteeInfoDto {
    private PartEnum part;

    public static MenteeInfoDto toDto(Mentee mentee){
        return MenteeInfoDto.builder()
                .part(mentee.getPart())
                .build();
    }
}
