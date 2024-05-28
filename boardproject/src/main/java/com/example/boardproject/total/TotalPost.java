package com.example.boardproject.total;

import java.util.List;

import com.example.boardproject.entity.Post;
import com.example.boardproject.entity.PostImage;

public class TotalPost {
    private Post post;

    private List<TotalPostListRow> prevPostList;

    private List<PostImage> imageList;

    private List<TotalPostListRow> replList;

    private Boolean oriCheck;

    private Boolean linkCheck;
}
