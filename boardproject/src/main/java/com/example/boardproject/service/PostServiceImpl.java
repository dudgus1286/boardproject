package com.example.boardproject.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.boardproject.repository.PostImageRepository;
import com.example.boardproject.repository.PostRepository;
import com.example.boardproject.repository.UserRepository;
import com.example.boardproject.repository.total.TotalPostListObject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;

    @Override
    public List<TotalPostListObject> getList(TotalPostListObject tplo) {
        return null;
    }

}
