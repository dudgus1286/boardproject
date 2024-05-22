package com.example.boardproject.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = -1202785599L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final QPost lastReference;

    public final QPost originalReference;

    public final NumberPath<Long> pno = createNumber("pno", Long.class);

    public final StringPath text = createString("text");

    public final QUser writer;

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lastReference = inits.isInitialized("lastReference") ? new QPost(forProperty("lastReference"), inits.get("lastReference")) : null;
        this.originalReference = inits.isInitialized("originalReference") ? new QPost(forProperty("originalReference"), inits.get("originalReference")) : null;
        this.writer = inits.isInitialized("writer") ? new QUser(forProperty("writer")) : null;
    }

}

