package com.soongsil.CoffeeChat.dto.UserController;

import com.soongsil.CoffeeChat.entity.Introduction;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MentorInfoDto {
    private PartEnum part;
    private ClubEnum club;
    private Long introductionId;

    public static MentorInfoDto toDto(Mentor mentor){
        return MentorInfoDto.builder()
                .part(mentor.getPart())
                .club(mentor.getClub())
                .introductionId(mentor.getIntroduction().getId())
                .build();
    }
}
