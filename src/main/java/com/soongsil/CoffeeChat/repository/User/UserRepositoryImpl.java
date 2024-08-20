package com.soongsil.CoffeeChat.repository.User;

import static com.soongsil.CoffeeChat.entity.QMentee.*;
import static com.soongsil.CoffeeChat.entity.QMentor.*;
import static com.soongsil.CoffeeChat.entity.QPossibleDate.*;
import static com.soongsil.CoffeeChat.entity.QUser.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.entity.User;

import lombok.RequiredArgsConstructor;

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
