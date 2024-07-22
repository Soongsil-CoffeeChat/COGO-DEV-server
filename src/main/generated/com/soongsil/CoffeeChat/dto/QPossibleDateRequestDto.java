package com.soongsil.CoffeeChat.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.soongsil.CoffeeChat.dto.QPossibleDateRequestDto is a Querydsl Projection type for PossibleDateRequestDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QPossibleDateRequestDto extends ConstructorExpression<PossibleDateRequestDto> {

    private static final long serialVersionUID = -203748687L;

    public QPossibleDateRequestDto(com.querydsl.core.types.Expression<java.time.LocalDate> date, com.querydsl.core.types.Expression<java.time.LocalTime> startTime, com.querydsl.core.types.Expression<java.time.LocalTime> endTime, com.querydsl.core.types.Expression<Long> possibleDateId) {
        super(PossibleDateRequestDto.class, new Class<?>[]{java.time.LocalDate.class, java.time.LocalTime.class, java.time.LocalTime.class, long.class}, date, startTime, endTime, possibleDateId);
    }

}

