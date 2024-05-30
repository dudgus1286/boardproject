package com.example.boardproject.total;

import java.util.List;

import com.example.boardproject.entity.Post;
import com.example.boardproject.entity.PostImage;
import com.example.boardproject.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TotalPost {
    private Post post;

    private User writer;

    private List<TotalPostListRow> prevPostList;

    private List<PostImage> imageList;

    private List<TotalPostListRow> replList;

    private Boolean oriCheck;

    private Boolean linkCheck;
}
