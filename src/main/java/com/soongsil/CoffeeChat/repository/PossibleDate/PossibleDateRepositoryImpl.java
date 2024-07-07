package com.soongsil.CoffeeChat.repository.PossibleDate;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;
import com.soongsil.CoffeeChat.dto.QPossibleDateRequestDto;
import com.soongsil.CoffeeChat.entity.QMentor;
import com.soongsil.CoffeeChat.entity.QPossibleDate;
import com.soongsil.CoffeeChat.entity.QUser;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.soongsil.CoffeeChat.entity.QMentor.mentor;
import static com.soongsil.CoffeeChat.entity.QPossibleDate.possibleDate;
import static com.soongsil.CoffeeChat.entity.QUser.user;

@RequiredArgsConstructor
public class PossibleDateRepositoryImpl implements PossibleDateRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<PossibleDateRequestDto> getPossibleDatesByUsername(String username) {
        return queryFactory.
                select(new QPossibleDateRequestDto(
                        possibleDate.date,
                        possibleDate.startTime,
                        possibleDate.endTime
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
