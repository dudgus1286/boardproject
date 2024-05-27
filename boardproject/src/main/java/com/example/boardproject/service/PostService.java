package com.example.boardproject.service;

import java.util.List;

import com.example.boardproject.dto.PostDto;
import com.example.boardproject.entity.Post;

public interface PostService {

    public default PostDto dtoToEntity(Post post) {
        return null;
    }
}
