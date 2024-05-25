package com.example.boardproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardproject.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 원글 pno 를 기준으로 댓글 리스트 반환
    List<Post> findByLastReferenceOrderByCreatedAtAsc(Long pno);
}
