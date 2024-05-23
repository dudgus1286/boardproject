package com.example.boardproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardproject.entity.PostThread;

public interface PostThreadRepository extends JpaRepository<PostThread, Long> {

}
