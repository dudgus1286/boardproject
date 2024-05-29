package com.example.boardproject.service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.boardproject.dto.PostDto;
import com.example.boardproject.dto.PostImageDto;
import com.example.boardproject.dto.TotalPostListRowDto;
import com.example.boardproject.entity.Post;
import com.example.boardproject.entity.PostImage;
import com.example.boardproject.entity.User;
import com.example.boardproject.total.TotalPostListRow;

public interface PostService {

    public default TotalPostListRowDto entityToDto(TotalPostListRow row) {
        Post post = row.getPost();
        User user = row.getWriter();

        List<PostImageDto> postImageDtos = row.getImageList().stream().map(i -> {
            return PostImageDto.builder()
                    .ino(i.getIno())
                    .path(i.getPath())
                    .uuid(i.getUuid())
                    .imgName(i.getImgName())
                    .build();
        }).collect(Collectors.toList());
        ;
        PostDto postDto = PostDto.builder()
                .pno(post.getPno())
                .text(post.getText())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .originalReference(post.getOriginalReference())
                .lastReference(post.getLastReference())
                .uno(user.getUno())
                .writerId(user.getUserId())
                .writerNickname(user.getNickname())
                .imgList(postImageDtos)
                .build();
        return null;
    }
}
