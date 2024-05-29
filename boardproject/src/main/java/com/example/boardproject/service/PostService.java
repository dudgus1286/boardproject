package com.example.boardproject.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.boardproject.dto.PageRequestDto;
import com.example.boardproject.dto.PageResultDto;
import com.example.boardproject.dto.PostDto;
import com.example.boardproject.dto.PostImageDto;
import com.example.boardproject.dto.TotalPostDto;
import com.example.boardproject.entity.Post;
import com.example.boardproject.entity.User;
import com.example.boardproject.total.TotalPostListRow;

public interface PostService {
    PageResultDto getList(PageRequestDto requestDto);

    public default TotalPostDto entityToDto(TotalPostListRow row) {
        TotalPostDto dto = new TotalPostDto();

        Post post = row.getPost();
        User writer = row.getWriter();

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
                .uno(writer.getUno())
                .writerId(writer.getUserId())
                .writerNickname(writer.getNickname())
                .imgList(postImageDtos)
                .build();
        dto.setPost(postDto);

        post = row.getPrevPost();
        writer = row.getPrevPostWriter();
        List<PostDto> prevPostList = new ArrayList<>();
        postDto = PostDto.builder()
                .pno(post.getPno())
                .text(post.getText())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .originalReference(post.getOriginalReference())
                .lastReference(post.getLastReference())
                .uno(writer.getUno())
                .writerId(writer.getUserId())
                .writerNickname(writer.getNickname())
                .build();
        prevPostList.add(postDto);
        dto.setPrevPostList(prevPostList);

        return dto;
    }
}
