package com.example.boardproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardproject.entity.Post;
import com.example.boardproject.entity.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    // 포스트를 기준으로 이미지리스트 반환
    List<PostImage> findByPostOrderByInoAsc(Post post);
}
