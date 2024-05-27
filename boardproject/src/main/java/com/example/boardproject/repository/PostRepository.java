package com.example.boardproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.boardproject.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 원글 pno 를 기준으로 댓글 리스트 반환
    List<Post> findByLastReferenceOrderByCreatedAtAsc(Long pno);
    
    @Query(
        "select distinct p1 , u1 , p2 , u2 " +
        " from Post p1 " +
        " left join User u1 on p1.writer = u1 " +
        " left join Post p2 on p1.lastReference = p2.pno " +
        " left join User u2 on p2.writer = u2 " + 
        " order by p1.pno asc "
        )
    List<Object[]> findAllWithPrevPost();

    @Query(
        "select distinct p1 , u1 " +
        " from Post p1 " +
        " left join User u1 on p1.writer = u1 "
    )
    List<Object[]> findAllWithUser();
}
