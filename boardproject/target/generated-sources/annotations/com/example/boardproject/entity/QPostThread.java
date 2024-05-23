package com.example.boardproject.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostThread is a Querydsl query type for PostThread
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostThread extends EntityPathBase<PostThreadd> {

    private static final long serialVersionUID = -525723605L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostThread postThread = new QPostThread("postThread");

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final QPost post;

    public final NumberPath<Long> ptid = createNumber("ptid", Long.class);

    public final QThreadd threadd;

    public QPostThread(String variable) {
        this(PostThreadd.class, forVariable(variable), INITS);
    }

    public QPostThread(Path<? extends PostThreadd> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostThread(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostThread(PathMetadata metadata, PathInits inits) {
        this(PostThreadd.class, metadata, inits);
    }

    public QPostThread(Class<? extends PostThreadd> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
        this.threadd = inits.isInitialized("threadd") ? new QThreadd(forProperty("threadd"), inits.get("threadd")) : null;
    }

}

