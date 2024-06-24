package com.soongsil.CoffeeChat.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPossibleDate is a Querydsl query type for PossibleDate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPossibleDate extends EntityPathBase<PossibleDate> {

    private static final long serialVersionUID = 391086519L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPossibleDate possibleDate = new QPossibleDate("possibleDate");

    public final BooleanPath apply = createBoolean("apply");

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> endTime = createTime("endTime", java.time.LocalTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMentor mentor;

    public final TimePath<java.time.LocalTime> startTime = createTime("startTime", java.time.LocalTime.class);

    public QPossibleDate(String variable) {
        this(PossibleDate.class, forVariable(variable), INITS);
    }

    public QPossibleDate(Path<? extends PossibleDate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPossibleDate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPossibleDate(PathMetadata metadata, PathInits inits) {
        this(PossibleDate.class, metadata, inits);
    }

    public QPossibleDate(Class<? extends PossibleDate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.mentor = inits.isInitialized("mentor") ? new QMentor(forProperty("mentor")) : null;
    }

}

