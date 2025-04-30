package com.soongsil.CoffeeChat.domain.possibleDate.repository;

import static com.soongsil.CoffeeChat.domain.mentor.entity.QMentor.mentor;
import static com.soongsil.CoffeeChat.domain.possibleDate.entity.QPossibleDate.possibleDate;
import static com.soongsil.CoffeeChat.domain.user.entity.QUser.user;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateResponse.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PossibleDateRepositoryImpl implements PossibleDateRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<PossibleDateCreateResponse> getPossibleDatesByUsername(String username) {
        return queryFactory
                .select(
                        Projections.constructor(
                                PossibleDateCreateResponse.class,
                                possibleDate.date,
                                possibleDate.startTime,
                                possibleDate.endTime,
                                possibleDate.id.as("possibleDateId"),
                                possibleDate.isActive))
                .from(user)
                .join(user.mentor, mentor)
                .join(mentor.possibleDates, possibleDate)
                .where(user.username.eq(username).and(possibleDate.isActive.isTrue()))
                .fetch();
    }
}
