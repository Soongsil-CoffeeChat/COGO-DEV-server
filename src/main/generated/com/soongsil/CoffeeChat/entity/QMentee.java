package com.soongsil.CoffeeChat.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMentee is a Querydsl query type for Mentee
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMentee extends EntityPathBase<Mentee> {

    private static final long serialVersionUID = 899494262L;

    public static final QMentee mentee = new QMentee("mentee");

    public final SetPath<Application, QApplication> applications = this.<Application, QApplication>createSet("applications", Application.class, QApplication.class, PathInits.DIRECT2);

    public final StringPath birth = createString("birth");

    public final NumberPath<Integer> grade = createNumber("grade", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath nickname = createString("nickname");

    public final StringPath part = createString("part");

    public final StringPath phoneNum = createString("phoneNum");

    public final StringPath picture = createString("picture");

    public QMentee(String variable) {
        super(Mentee.class, forVariable(variable));
    }

    public QMentee(Path<? extends Mentee> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMentee(PathMetadata metadata) {
        super(Mentee.class, metadata);
    }

}

