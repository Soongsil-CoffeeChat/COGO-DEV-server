package com.soongsil.CoffeeChat.domain.dto;

import com.soongsil.CoffeeChat.domain.entity.Introduction;
import com.soongsil.CoffeeChat.domain.entity.Mentor;
import com.soongsil.CoffeeChat.domain.entity.User;

public class MentorConverter {
    public static Mentor toEntity(MentorRequest.MentorJoinRequest dto) {
        return Mentor.builder().club(dto.getClub()).part(dto.getPart()).build();
    }

    public static MentorResponse.MentorIntroductionGetUpdateResponse
            toMentorIntroductionGetUpdateResponse(Introduction introduction) {
        return MentorResponse.MentorIntroductionGetUpdateResponse.builder()
                .answer1(introduction.getAnswer1())
                .answer2(introduction.getAnswer2())
                .title(introduction.getTitle())
                .description(introduction.getDescription())
                .build();
    }

    public static MentorResponse.MentorGetUpdateDetailResponse toMentorGetUpdateDetailDto(
            Mentor mentor, User user) {
        return MentorResponse.MentorGetUpdateDetailResponse.builder()
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

    public static MentorResponse.MentorInfoResponse toResponse(Mentor mentor) {
        return MentorResponse.MentorInfoResponse.builder()
                .part(mentor.getPart())
                .club(mentor.getClub())
                .introductionId(mentor.getIntroduction().getId())
                .isNewAccount(false)
                .build();
    }
}
