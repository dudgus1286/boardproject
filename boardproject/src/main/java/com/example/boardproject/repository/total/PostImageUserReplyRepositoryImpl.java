package com.example.boardproject.repository.total;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.example.boardproject.entity.Post;
import com.example.boardproject.entity.QPost;
import com.example.boardproject.entity.QPostImage;
import com.example.boardproject.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;

public class PostImageUserReplyRepositoryImpl extends QuerydslRepositorySupport implements PostIamgeUserReplyRepository {

    public PostImageUserReplyRepositoryImpl(Class<?> domainClass) {
        super(Post.class);
    }

    @Override
    public Page<Object[]> getList(String type, String keyword, Pageable pageable) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QPost post2 = QPost.post;
        QUser user2 = QUser.user;
        QPostImage postImage = QPostImage.postImage;

        JPQLQuery<Post> query = from(post);
        query.leftJoin(post.writer, user);
        query.leftJoin(post2).on(post.lastReference.eq(post2.pno));
        query.leftJoin(user2).on(post2.writer.eq(user2));
        
        JPQLQuery<Tuple> tuple = query.select(post, user, post2, user2)
        .orderBy(post.pno.desc());

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.pno.gt(0L));

        // 검색조건
        BooleanBuilder conditionBuilder = new BooleanBuilder();
        if (type.contains("t")) {
            conditionBuilder.or(post.text.contains(keyword));
        }
        builder.and(conditionBuilder);
        tuple.where(builder);

        // Sort 별도지정
        Sort sort = pageable.getSort();
        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String prop = order.getProperty();

            PathBuilder<Post> orderByExpression = new PathBuilder<>(Post.class, "post");
            tuple.orderBy(new OrderSpecifier(direction, orderByExpression.get(prop)));
        });

        // 페이지 처리
        tuple.offset(pageable.getOffset());
        tuple.limit(pageable.getPageSize());

        List<Tuple> result = tuple.fetch();
        long count = tuple.fetchCount();

        return new PageImpl<>(result.stream().map(t -> t.toArray()).collect(Collectors.toList()), pageable, count);
    }
    
}
