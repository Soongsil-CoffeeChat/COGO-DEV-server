package com.soongsil.CoffeeChat.repository.Mentor;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;

import com.soongsil.CoffeeChat.entity.User;

import java.util.ArrayList;
import java.util.List;



public class MentorRepositoryImpl implements MentorRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    public MentorRepositoryImpl(JPAQueryFactory queryFactory){
        this.queryFactory=queryFactory;
    }

    @Override
    public List<ResponseMentorListInfo> getMentorListByPart(int part) { //일반 join
        /*
        return queryFactory
                .select(new QResponseMentorListInfo(
                        user.picture,
                        user.name.as("mentorName"),
                        mentor.part,
                        mentor.club,
                        user.username,
                        mentor.id.as("mentorId"),
                        introduction.title,
                        introduction.description
                        ))
                .from(user)mentorcontro
                .join(user.mentor, mentor)
                .join(mentor.introduction, introduction)
                .where(mentor.part.eq(part))
                .fetch();

         */
        return new ArrayList<>();

    }

    @Override
    public List<ResponseMentorListInfo> getMentorListByClub(int club) { //일반 join
        /*
        return queryFactory
                .select(new QResponseMentorListInfo(
                        user.picture,
                        user.name.as("mentorName"),
                        mentor.part,
                        mentor.club,
                        user.username,
                        mentor.id.as("mentorId"),
                        introduction.title,
                        introduction.description
                        ))
                .from(user)
                .join(user.mentor, mentor)
                .join(mentor.introduction, introduction)
                .where(mentor.club.eq(club))
                .fetch();

         */
        return new ArrayList<>();
    }

    @Override
    public List<ResponseMentorListInfo> getMentorListByPartAndClub(int part, int club) { //일반 join
        /*
        return queryFactory
                .select(new QResponseMentorListInfo(
                        user.picture,
                        user.name.as("mentorName"),
                        mentor.part,
                        mentor.club,
                        user.username,
                        mentor.id.as("mentorId"),
                        introduction.title,
                        introduction.description
                ))
                .from(user)
                .join(user.mentor, mentor)
                .join(mentor.introduction, introduction)
                .where(mentor.club.eq(club).and(mentor.part.eq(part)))
                .fetch();

         */
        return new ArrayList<>();
    }

    @Override
    public List<User> getMentorListByPartWithFetch(int part) {  //fetch join
        /*
        return queryFactory
                .selectFrom(user)
                .join(user.mentor, mentor).fetchJoin()
                .where(mentor.part.eq(part))
                .fetch();

         */
        return new ArrayList<>();

    }
}
