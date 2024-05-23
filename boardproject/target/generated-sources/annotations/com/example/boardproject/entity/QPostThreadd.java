package com.example.boardproject.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostThreadd is a Querydsl query type for PostThreadd
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostThreadd extends EntityPathBase<PostThreadd> {

    private static final long serialVersionUID = 882437529L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostThreadd postThreadd = new QPostThreadd("postThreadd");

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final QPost post;

    public final NumberPath<Long> ptid = createNumber("ptid", Long.class);

    public final QThreadd threadd;

    public QPostThreadd(String variable) {
        this(PostThreadd.class, forVariable(variable), INITS);
    }

    public QPostThreadd(Path<? extends PostThreadd> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostThreadd(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostThreadd(PathMetadata metadata, PathInits inits) {
        this(PostThreadd.class, metadata, inits);
    }

    public QPostThreadd(Class<? extends PostThreadd> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
        this.threadd = inits.isInitialized("threadd") ? new QThreadd(forProperty("threadd"), inits.get("threadd")) : null;
    }

}

