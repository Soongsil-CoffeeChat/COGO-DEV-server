package com.soongsil.CoffeeChat.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMentor is a Querydsl query type for Mentor
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMentor extends EntityPathBase<Mentor> {

    private static final long serialVersionUID = 899494585L;

    public static final QMentor mentor = new QMentor("mentor");

    public final SetPath<Application, QApplication> applications = this.<Application, QApplication>createSet("applications", Application.class, QApplication.class, PathInits.DIRECT2);

    public final StringPath birth = createString("birth");

    public final ListPath<Club, QClub> clubs = this.<Club, QClub>createList("clubs", Club.class, QClub.class, PathInits.DIRECT2);

    public final StringPath field = createString("field");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath part = createString("part");

    public final StringPath phoneNum = createString("phoneNum");

    public final StringPath picture = createString("picture");

    public final SetPath<PossibleDate, QPossibleDate> possibleDates = this.<PossibleDate, QPossibleDate>createSet("possibleDates", PossibleDate.class, QPossibleDate.class, PathInits.DIRECT2);

    public QMentor(String variable) {
        super(Mentor.class, forVariable(variable));
    }

    public QMentor(Path<? extends Mentor> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMentor(PathMetadata metadata) {
        super(Mentor.class, metadata);
    }

}

