package com.example.boardproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadRepository extends JpaRepository<com.example.boardproject.entity.Thread, Long> {

}
