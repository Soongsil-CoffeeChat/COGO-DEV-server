package com.soongsil.CoffeeChat.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRefresh is a Querydsl query type for Refresh
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRefresh extends EntityPathBase<Refresh> {

    private static final long serialVersionUID = -2045345053L;

    public static final QRefresh refresh1 = new QRefresh("refresh1");

    public final StringPath expiration = createString("expiration");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath refresh = createString("refresh");

    public final StringPath username = createString("username");

    public QRefresh(String variable) {
        super(Refresh.class, forVariable(variable));
    }

    public QRefresh(Path<? extends Refresh> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRefresh(PathMetadata metadata) {
        super(Refresh.class, metadata);
    }

}

