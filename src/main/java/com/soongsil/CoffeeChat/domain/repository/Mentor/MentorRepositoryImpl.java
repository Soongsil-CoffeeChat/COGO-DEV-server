package com.soongsil.CoffeeChat.domain.repository.Mentor;

import static com.soongsil.CoffeeChat.domain.entity.QIntroduction.introduction;
import static com.soongsil.CoffeeChat.domain.entity.QMentor.mentor;
import static com.soongsil.CoffeeChat.domain.entity.QUser.user;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.domain.dto.MentorResponse.*;
import com.soongsil.CoffeeChat.domain.entity.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.entity.enums.PartEnum;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MentorRepositoryImpl implements MentorRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MentorListResponse> getMentorListByPartAndClub(
            PartEnum part, ClubEnum club) { // 일반 join
        return queryFactory
                .select(
                        Projections.constructor(
                                MentorListResponse.class,
                                user.picture,
                                user.name.as("mentorName"),
                                mentor.part,
                                mentor.club,
                                user.username,
                                mentor.id.as("mentorId"),
                                introduction.title,
                                introduction.description))
                .from(user)
                .join(user.mentor, mentor)
                .leftJoin(mentor.introduction, introduction)
                .on(introduction.isNotNull())
                .where(clubEq(club), partEq(part))
                .fetch();
    }

    @Override
    public MentorGetUpdateDetailResponse getMentorInfoByMentorId(Long mentorId) {

        return queryFactory
                .select(
                        Projections.constructor(
                                MentorGetUpdateDetailResponse.class,
                                mentor.id.as("mentorId"),
                                user.name.as("mentorName"),
                                mentor.part,
                                mentor.club,
                                introduction.title.as("introductionTitle"),
                                introduction.description.as("introductionDescription"),
                                introduction.answer1.as("introductionAnswer1"),
                                introduction.answer2.as("introductionAnswer2"),
                                user.picture.as("imageUrl")))
                .from(user)
                .join(user.mentor, mentor)
                .join(mentor.introduction, introduction)
                .where(
                        mentor.id.eq(mentorId),
                        introduction.title.isNotNull(),
                        introduction.description.isNotNull(),
                        introduction.answer1.isNotNull(),
                        introduction.answer2.isNotNull())
                .fetchOne();
    }

    private BooleanExpression clubEq(ClubEnum club) {
        return club != null ? mentor.club.eq(club) : null;
    }

    private BooleanExpression partEq(PartEnum part) {
        return part != null ? mentor.part.eq(part) : null;
    }
}
