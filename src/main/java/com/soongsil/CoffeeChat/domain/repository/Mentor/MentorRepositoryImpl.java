package com.soongsil.CoffeeChat.domain.repository.Mentor;

import static com.soongsil.CoffeeChat.domain.entity.QIntroduction.introduction;
import static com.soongsil.CoffeeChat.domain.entity.QMentor.mentor;
import static com.soongsil.CoffeeChat.domain.entity.QUser.user;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.domain.dto.MentorResponse.*;
import com.soongsil.CoffeeChat.domain.entity.User;
import com.soongsil.CoffeeChat.domain.entity.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.entity.enums.PartEnum;

public class MentorRepositoryImpl implements MentorRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MentorRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<MentorListResponse> getMentorListByPart(PartEnum part) { // 일반 join

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
                .join(mentor.introduction, introduction)
                .where(mentor.part.eq(part))
                .fetch();
    }

    @Override
    public List<MentorListResponse> getMentorListByClub(ClubEnum club) { // 일반 join
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
                .join(mentor.introduction, introduction)
                .where(mentor.club.eq(club))
                .fetch();
    }

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
                .join(mentor.introduction, introduction)
                .where(mentor.club.eq(club).and(mentor.part.eq(part)))
                .fetch();
    }

    @Override
    public List<User> getMentorListByPartWithFetch(PartEnum part) { // fetch join
        return queryFactory
                .selectFrom(user)
                .join(user.mentor, mentor)
                .fetchJoin()
                .where(mentor.part.eq(part))
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
                .where(mentor.id.eq(mentorId))
                .fetchOne();
    }
}
