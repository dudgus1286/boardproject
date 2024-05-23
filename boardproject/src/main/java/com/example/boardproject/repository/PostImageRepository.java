package com.example.boardproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardproject.entity.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

}
