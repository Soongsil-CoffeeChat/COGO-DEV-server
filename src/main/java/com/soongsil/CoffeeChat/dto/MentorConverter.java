package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.dto.MentorRequest.*;
import com.soongsil.CoffeeChat.dto.MentorResponse.*;
import com.soongsil.CoffeeChat.entity.Introduction;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;

public class MentorConverter {
    public static Mentor toEntity(MentorJoinRequest dto) {
        return Mentor.builder().club(dto.getClub()).part(dto.getPart()).build();
    }

    public static MentorIntroductionGetUpdateResponse toMentorIntroductionGetUpdateResponse(
            Introduction introduction) {
        return MentorIntroductionGetUpdateResponse.builder()
                .answer1(introduction.getAnswer1())
                .answer2(introduction.getAnswer2())
                .title(introduction.getTitle())
                .description(introduction.getDescription())
                .build();
    }

    public static MentorGetUpdateDetailResponse toMentorGetUpdateDetailDto(
            Mentor mentor, User user) {
        return MentorGetUpdateDetailResponse.builder()
                .mentorId(mentor.getId())
                .mentorName(user.getName())
                .imageUrl(user.getPicture())
                .part(mentor.getPart())
                .club(mentor.getClub())
                .introductionTitle(mentor.getIntroduction().getTitle())
                .introductionDescription(mentor.getIntroduction().getDescription())
                .introductionAnswer1(mentor.getIntroduction().getAnswer1())
                .introductionAnswer2(mentor.getIntroduction().getAnswer2())
                .build();
    }

    public static MentorInfoResponse toResponse(Mentor mentor) {
        return MentorInfoResponse.builder()
                .part(mentor.getPart())
                .club(mentor.getClub())
                .introductionId(mentor.getIntroduction().getId())
                .isNewAccount(false)
                .build();
    }
}
