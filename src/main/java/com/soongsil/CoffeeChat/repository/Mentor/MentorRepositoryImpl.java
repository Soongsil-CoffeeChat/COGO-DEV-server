package com.soongsil.CoffeeChat.repository.Mentor;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.dto.QResponseMentorListInfo;
import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.entity.User;

import java.util.List;
import static com.soongsil.CoffeeChat.entity.QMentor.mentor;
import static com.soongsil.CoffeeChat.entity.QUser.user;

public class MentorRepositoryImpl implements MentorRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    public MentorRepositoryImpl(JPAQueryFactory queryFactory){
        this.queryFactory=queryFactory;
    }

    @Override
    public List<ResponseMentorListInfo> getMentorListByPart(String part) {
        return queryFactory
                .select(new QResponseMentorListInfo(
                        mentor.picture,
                        user.name.as("mentorName"),
                        mentor.field,
                        user.username,
                        mentor.part))
                .from(user)
                .join(user.mentor, mentor)
                .where(mentor.part.eq(part))
                .fetch();
    }

    @Override
    public List<User> getMentorListByPart2(String part) {
        return queryFactory
                .selectFrom(user)
                .join(user.mentor, mentor).fetchJoin()
                .where(mentor.part.eq(part))
                .fetch();
    }
}
