package com.example.boardproject.service;

import java.util.List;

import com.example.boardproject.dto.PostDto;
import com.example.boardproject.entity.Post;
import com.example.boardproject.repository.total.TotalPostListObject;

public interface PostService {
    List<TotalPostListObject> getList(TotalPostListObject tplo);

    public default PostDto dtoToEntity(Post post) {
        return null;
    }
}
