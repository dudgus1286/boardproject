package com.example.boardproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.boardproject.entity.PostThreadd;

public interface PostThreaddRepository extends JpaRepository<PostThreadd, Long> {
    // 같은 쓰레드에 담긴 포스트 확인용
    // 쓰레드에서 포스트 목록 볼 때, 쓰레드에 포스트 담을 때 이미 담긴 포스트인지 확인할 때 사용
    @Query("SELECT pt FROM PostThreadd pt WHERE pt.threadd.tno = :tno")
    List<PostThreadd> findByThreadd(Long tno);

    // 포스트 삭제 시 해당 포스트와 관련된 쓰레드 관계 삭제용
    @Modifying
    @Query("DELETE FROM PostThreadd pt WHERE pt.post.pno = :pno")
    void deleteByPost(Long pno);

    // 쓰레드 삭제 시 해당 쓰레드 관계 삭제용
    @Modifying
    @Query("DELETE FROM PostThreadd pt WHERE pt.threadd.tno = :tno")
    void deleteByThreadd(Long tno);
}
