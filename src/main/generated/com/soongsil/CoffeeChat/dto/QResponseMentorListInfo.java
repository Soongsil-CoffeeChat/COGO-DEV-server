package com.soongsil.CoffeeChat.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.soongsil.CoffeeChat.dto.QResponseMentorListInfo is a Querydsl Projection type for ResponseMentorListInfo
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QResponseMentorListInfo extends ConstructorExpression<ResponseMentorListInfo> {

    private static final long serialVersionUID = -1414167632L;

    public QResponseMentorListInfo(com.querydsl.core.types.Expression<String> picture, com.querydsl.core.types.Expression<String> mentorName, com.querydsl.core.types.Expression<Integer> part, com.querydsl.core.types.Expression<Integer> club, com.querydsl.core.types.Expression<String> username, com.querydsl.core.types.Expression<Long> mentorId, com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<String> description) {
        super(ResponseMentorListInfo.class, new Class<?>[]{String.class, String.class, int.class, int.class, String.class, long.class, String.class, String.class}, picture, mentorName, part, club, username, mentorId, title, description);
    }

}

