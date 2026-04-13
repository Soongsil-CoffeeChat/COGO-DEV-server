package com.soongsil.CoffeeChat.domain.mentor.repository;

import static com.soongsil.CoffeeChat.domain.mentor.entity.QIntroduction.introduction;
import static com.soongsil.CoffeeChat.domain.mentor.entity.QMentor.mentor;
import static com.soongsil.CoffeeChat.domain.user.entity.QUser.user;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorResponse.MentorDetailResponse;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorResponse.MentorListResponse;
import com.soongsil.CoffeeChat.domain.mentor.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.mentor.enums.PartEnum;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MentorRepositoryImpl implements MentorRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // report 필터링 조회용
    @Override
    public List<MentorListResponse> getMentorListByPartAndClub(
            Long currentUserId, PartEnum part, ClubEnum club) { // 일반 join
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
                                user.id.as("userId"),
                                introduction.title,
                                introduction.description))
                .from(user)
                .join(user.mentor, mentor)
                .leftJoin(mentor.introduction, introduction)
                .on(introduction.isNotNull())
                .where( // 계정 삭제 X, reported 쌍으로 등록 되지 않은 mentor
                        user.isDeleted.isFalse(),
                        clubEq(club),
                        partEq(part),
                        introduction.title.isNotNull(),
                        introduction.description.isNotNull(),
                        introduction.answer1.isNotNull(),
                        introduction.answer2.isNotNull())
                .fetch();
    }

    // report 필터링 X, 공통 조회
    @Override
    public List<MentorListResponse> getMentorListByPartAndClub(PartEnum part, ClubEnum club) {
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
                                user.id.as("userId"),
                                introduction.title,
                                introduction.description))
                .from(user)
                .join(user.mentor, mentor)
                .leftJoin(mentor.introduction, introduction)
                .on(introduction.isNotNull())
                .where( // 계정 삭제 X, reported 쌍으로 등록 되지 않은 mentor
                        user.isDeleted.isFalse(),
                        clubEq(club),
                        partEq(part),
                        introduction.title.isNotNull(),
                        introduction.description.isNotNull(),
                        introduction.answer1.isNotNull(),
                        introduction.answer2.isNotNull())
                .fetch();
    }

    @Override
    public MentorDetailResponse getMentorInfoByMentorId(Long mentorId) {

        return queryFactory
                .select(
                        Projections.constructor(
                                MentorDetailResponse.class,
                                user.id.as("userId"),
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
                        user.isDeleted.isFalse(),
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
