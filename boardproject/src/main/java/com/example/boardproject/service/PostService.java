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
                .build();

        try {
            List<PostImageDto> postImageDtos = new ArrayList<>();
            if (row.getImageList() != null) {
                postImageDtos = row.getImageList().stream().map(i -> {
                    return PostImageDto.builder()
                            .ino(i.getIno())
                            .path(i.getPath())
                            .uuid(i.getUuid())
                            .imgName(i.getImgName())
                            .build();
                }).collect(Collectors.toList());
                postDto.setImageList(postImageDtos);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        dto.setPost(postDto);

        try {
            if (row.getPrevPost() != null) {
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
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        try {
            if (row.getReplyList() != null) {
                List<PostDto> replyList = new ArrayList<>();
                for (Post reply : row.getReplyList()) {
                    postDto = PostDto.builder()
                            .pno(reply.getPno())
                            .text(reply.getText())
                            .createdAt(reply.getCreatedAt())
                            .updatedAt(reply.getUpdatedAt())
                            .originalReference(reply.getOriginalReference())
                            .lastReference(reply.getLastReference())
                            .uno(reply.getWriter().getUno())
                            .writerId(reply.getWriter().getUserId())
                            .writerNickname(reply.getWriter().getNickname())
                            .build();
                    replyList.add(postDto);
                }
                dto.setReplyList(replyList);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return dto;
    }
}
