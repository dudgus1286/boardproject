package com.example.boardproject.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QThreadd is a Querydsl query type for Threadd
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QThreadd extends EntityPathBase<Threadd> {

    private static final long serialVersionUID = -220259879L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QThreadd threadd = new QThreadd("threadd");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final QUser creator;

    public final StringPath text = createString("text");

    public final StringPath title = createString("title");

    public final NumberPath<Long> tno = createNumber("tno", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QThreadd(String variable) {
        this(Threadd.class, forVariable(variable), INITS);
    }

    public QThreadd(Path<? extends Threadd> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QThreadd(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QThreadd(PathMetadata metadata, PathInits inits) {
        this(Threadd.class, metadata, inits);
    }

    public QThreadd(Class<? extends Threadd> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.creator = inits.isInitialized("creator") ? new QUser(forProperty("creator")) : null;
    }

}

