package com.soongsil.CoffeeChat.domain.repository.PossibleDate;

import static com.soongsil.CoffeeChat.domain.entity.QMentor.mentor;
import static com.soongsil.CoffeeChat.domain.entity.QPossibleDate.possibleDate;
import static com.soongsil.CoffeeChat.domain.entity.QUser.user;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.domain.dto.PossibleDateResponse.*;

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
