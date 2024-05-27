package com.example.boardproject.entity;

import java.util.List;

public class TotalPost {
    private Post post;
    private User postWriter;

    private Post prevPost;
    private User prevPostWriter;

    private List<PostImage> images;
    private List<Post> replies;
}
