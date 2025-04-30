package com.soongsil.CoffeeChat.domain.mentor.dto;

import com.soongsil.CoffeeChat.domain.mentor.dto.MentorResponse.MentorDetailResponse;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorResponse.MentorInfoResponse;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorResponse.MentorIntroductionResponse;
import com.soongsil.CoffeeChat.domain.mentor.entity.Introduction;
import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.user.entity.User;

public class MentorConverter {
    public static Mentor toEntity(MentorRequest.MentorJoinRequest dto, User user) {
        return Mentor.builder().club(dto.getClub()).part(dto.getPart()).user(user).build();
    }

    public static MentorIntroductionResponse toIntroductionResponse(Introduction introduction) {
        return MentorResponse.MentorIntroductionResponse.builder()
                .answer1(introduction.getAnswer1())
                .answer2(introduction.getAnswer2())
                .title(introduction.getTitle())
                .description(introduction.getDescription())
                .build();
    }

    public static MentorDetailResponse toDetailResponse(Mentor mentor, User user) {
        return MentorDetailResponse.builder()
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
        return MentorResponse.MentorInfoResponse.builder()
                .part(mentor.getPart())
                .club(mentor.getClub())
                .build();
    }
}
