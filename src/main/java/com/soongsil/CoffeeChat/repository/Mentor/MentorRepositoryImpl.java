package com.soongsil.CoffeeChat.repository.Mentor;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.dto.QResponseMentorListInfo;
import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.entity.QIntroduction;
import com.soongsil.CoffeeChat.entity.User;

import java.util.List;

import static com.soongsil.CoffeeChat.entity.QIntroduction.introduction;
import static com.soongsil.CoffeeChat.entity.QMentor.mentor;
import static com.soongsil.CoffeeChat.entity.QUser.user;

public class MentorRepositoryImpl implements MentorRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    public MentorRepositoryImpl(JPAQueryFactory queryFactory){
        this.queryFactory=queryFactory;
    }

    @Override
    public List<ResponseMentorListInfo> getMentorListByPart(int part) { //일반 join
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
    }

    @Override
    public List<ResponseMentorListInfo> getMentorListByClub(int club) { //일반 join
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
    }

    @Override
    public List<ResponseMentorListInfo> getMentorListByPartAndClub(int part, int club) { //일반 join
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
    }

    @Override
    public List<User> getMentorListByPart2(int part) {  //fetch join
        return queryFactory
                .selectFrom(user)
                .join(user.mentor, mentor).fetchJoin()
                .where(mentor.part.eq(part))
                .fetch();
    }
}
