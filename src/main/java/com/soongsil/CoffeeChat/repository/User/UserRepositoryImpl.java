package com.soongsil.CoffeeChat.repository.User;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.entity.*;
import lombok.RequiredArgsConstructor;

import static com.soongsil.CoffeeChat.entity.QMentee.mentee;
import static com.soongsil.CoffeeChat.entity.QMentor.mentor;
import static com.soongsil.CoffeeChat.entity.QPossibleDate.possibleDate;
import static com.soongsil.CoffeeChat.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public User findByMentorIdWithFetch(Long mentorId) {
        return queryFactory.
                selectFrom(user)
                .join(user.mentor, mentor).fetchJoin()
                .join(mentor.possibleDates, possibleDate).fetchJoin()
                .where(mentor.id.eq(mentorId))
                .fetchOne();
    }

    @Override
    public User findByUsernameWithFetch(String username) {
        return queryFactory.
                selectFrom(user)
                .join(user.mentee, mentee).fetchJoin()
                .where(user.username.eq(username))
                .fetchOne();
    }
}
