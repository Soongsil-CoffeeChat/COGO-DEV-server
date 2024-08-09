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

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMentor mentor = new QMentor("mentor");

    public final SetPath<Application, QApplication> applications = this.<Application, QApplication>createSet("applications", Application.class, QApplication.class, PathInits.DIRECT2);

    public final NumberPath<Integer> club = createNumber("club", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QIntroduction introduction;

    public final NumberPath<Integer> part = createNumber("part", Integer.class);

    public final SetPath<PossibleDate, QPossibleDate> possibleDates = this.<PossibleDate, QPossibleDate>createSet("possibleDates", PossibleDate.class, QPossibleDate.class, PathInits.DIRECT2);

    public QMentor(String variable) {
        this(Mentor.class, forVariable(variable), INITS);
    }

    public QMentor(Path<? extends Mentor> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMentor(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMentor(PathMetadata metadata, PathInits inits) {
        this(Mentor.class, metadata, inits);
    }

    public QMentor(Class<? extends Mentor> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.introduction = inits.isInitialized("introduction") ? new QIntroduction(forProperty("introduction")) : null;
    }

}

