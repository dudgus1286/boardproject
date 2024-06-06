package com.example.boardproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.boardproject.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
        @Query("SELECT p1, u1, p2, u2 FROM Post p1 " +
                        " LEFT JOIN User u1 on p1.writer = u1 " +
                        " LEFT JOIN Post p2 on p1.lastReference = p2.pno " +
                        " LEFT JOIN User u2 on p2.writer = u2 " +
                        " ORDER BY p1.pno DESC")
        List<Object[]> findAllWithPrevPost();

        @Query("SELECT p1, u1, p2, u2 FROM Post p1 " +
                        " LEFT JOIN User u1 on p1.writer = u1 " +
                        " LEFT JOIN Post p2 on p1.lastReference = p2.pno " +
                        " LEFT JOIN User u2 on p2.writer = u2 " +
                        " WHERE p1.text like :keyword " +
                        " ORDER BY p1.pno DESC")
        List<Object[]> findAllWithPrevPost(String keyword);

        @Query("SELECT p1, u1, p2, u2 FROM Post p1 " +
                        " LEFT JOIN User u1 on p1.writer = u1 " +
                        " LEFT JOIN Post p2 on p1.lastReference = p2.pno " +
                        " LEFT JOIN User u2 on p2.writer = u2 " +
                        " WHERE p1.pno = :pno " +
                        " ORDER BY p1.pno DESC")
        List<Object[]> findWithPrevPost(Long pno);

        @Query("SELECT p, u FROM Post p LEFT JOIN User u ON p.writer = u WHERE p.lastReference in :postNums")
        List<Object[]> findByLastReferenceWithWriter(Long[] postNums);

        @Query("SELECT p, u FROM Post p LEFT JOIN User u ON p.writer = u WHERE p.lastReference = :pno")
        List<Object[]> findByLastReferenceWithWriter(Long pno);

        @Query("SELECT p, u FROM Post p LEFT JOIN User u ON p.writer = u WHERE p.pno = :pno")
        List<Object[]> findByPnoWithWriter(Long pno);
}
