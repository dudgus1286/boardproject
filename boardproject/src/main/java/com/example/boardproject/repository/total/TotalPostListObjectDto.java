package com.example.boardproject.repository.total;

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
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TotalPostListObjectDto {
    private Post post;
    private User writer;

    private Post prevPost;
    private User prevPostWriter;

    private List<PostImage> postImages;
    private List<Post> replies;

}
