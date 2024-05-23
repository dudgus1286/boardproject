package com.example.boardproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.boardproject.entity.PostThread;
import com.example.boardproject.entity.Threadd;

public interface PostThreadRepository extends JpaRepository<PostThread, Long> {
    // 같은 쓰레드에 담긴 포스트를 출력
    @Query("SELECT pt FROM PostThread pt WHERE pt.thread.tno = :tno")
    List<PostThread> findByThread(Long tno);
}
