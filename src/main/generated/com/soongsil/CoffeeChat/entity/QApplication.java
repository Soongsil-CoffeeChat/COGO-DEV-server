package com.soongsil.CoffeeChat.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QApplication is a Querydsl query type for Application
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApplication extends EntityPathBase<Application> {

    private static final long serialVersionUID = 1862316376L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QApplication application = new QApplication("application");

    public final EnumPath<com.soongsil.CoffeeChat.enums.ApplicationStatus> accept = createEnum("accept", com.soongsil.CoffeeChat.enums.ApplicationStatus.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> endTime = createTime("endTime", java.time.LocalTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMentee mentee;

    public final QMentor mentor;

    public final TimePath<java.time.LocalTime> startTime = createTime("startTime", java.time.LocalTime.class);

    public QApplication(String variable) {
        this(Application.class, forVariable(variable), INITS);
    }

    public QApplication(Path<? extends Application> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QApplication(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QApplication(PathMetadata metadata, PathInits inits) {
        this(Application.class, metadata, inits);
    }

    public QApplication(Class<? extends Application> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.mentee = inits.isInitialized("mentee") ? new QMentee(forProperty("mentee")) : null;
        this.mentor = inits.isInitialized("mentor") ? new QMentor(forProperty("mentor")) : null;
    }

}

