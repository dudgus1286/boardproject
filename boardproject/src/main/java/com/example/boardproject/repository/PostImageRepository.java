package com.example.boardproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.boardproject.entity.Post;
import com.example.boardproject.entity.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    @Query("SELECT pi FROM PostImage pi WHERE pi.post In :posts ")
    List<PostImage> findByPost(List<Post> posts);

    List<PostImage> findByPost(Post posts);
}
