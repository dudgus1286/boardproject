package com.example.boardproject.dto;

import java.util.List;
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
public class TotalPostDto {
    private PostDto post;

    private List<PostDto> prevPostList;

    private List<PostDto> replyList;
}
