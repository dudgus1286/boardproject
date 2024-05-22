package com.example.boardproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardproject.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
