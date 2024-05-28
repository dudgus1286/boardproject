package com.example.boardproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.boardproject.entity.PostThreadd;

public interface PostThreaddRepository extends JpaRepository<PostThreadd, Long> {
}
