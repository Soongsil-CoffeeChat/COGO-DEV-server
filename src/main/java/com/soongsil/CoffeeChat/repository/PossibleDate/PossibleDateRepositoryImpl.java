package com.soongsil.CoffeeChat.repository.PossibleDate;

import static com.soongsil.CoffeeChat.entity.QMentor.*;
import static com.soongsil.CoffeeChat.entity.QPossibleDate.*;
import static com.soongsil.CoffeeChat.entity.QUser.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.dto.PossibleDateCreateGetDto;
import com.soongsil.CoffeeChat.dto.QPossibleDateCreateGetDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PossibleDateRepositoryImpl implements PossibleDateRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<PossibleDateCreateGetDto> getPossibleDatesByUsername(String username) {
        return queryFactory.
                select(new QPossibleDateCreateGetDto(
                        possibleDate.date,
                        possibleDate.startTime,
                        possibleDate.endTime,
                        possibleDate.id.as("possibleDateId")
                ))
                .from(user)
                .join(user.mentor, mentor)
                .join(mentor.possibleDates, possibleDate)
                .where(user.username.eq(username).and(
                        possibleDate.isActive.isTrue()
                ))
                .fetch();
    }
}
