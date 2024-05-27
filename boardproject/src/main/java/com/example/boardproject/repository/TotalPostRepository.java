package com.example.boardproject.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.boardproject.entity.TotalPost;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TotalPostRepository {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;

    public Page<Object[]> getList (String type, String keyword, Pageable pageable) {
        return null;
    }

    public List<TotalPost> getTotalList(String type, String keyword, Pageable pageable) {
        List<TotalPost> totalList = new ArrayList<>();
        
        return totalList;
    }
}
