package com.example.boardproject.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.boardproject.dto.PageRequestDto;
import com.example.boardproject.dto.PageResultDto;
import com.example.boardproject.dto.PostDto;
import com.example.boardproject.dto.PostImageDto;
import com.example.boardproject.dto.TotalListRowDto;
import com.example.boardproject.dto.TotalPostDto;
import com.example.boardproject.entity.Post;
import com.example.boardproject.entity.User;
import com.example.boardproject.total.TotalPost;
import com.example.boardproject.total.TotalPostListRow;

public interface PostService {
    PageResultDto<TotalListRowDto, TotalPostListRow> getList(PageRequestDto requestDto);

    TotalPostDto getRow(Long pno);

    boolean deletePost(Long pno);

    public default TotalListRowDto entityToDto(TotalPostListRow row) {
        TotalListRowDto dto = new TotalListRowDto();

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

    public default TotalPostDto entityToDto(TotalPost tp) {
        TotalPostDto dto = new TotalPostDto();

        Post post = tp.getPost();
        User writer = tp.getWriter();
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
        dto.setPost(postDto);

        List<PostImageDto> postImageList = new ArrayList<>();
        List<TotalListRowDto> prevPostList = new ArrayList<>();
        List<TotalListRowDto> replyList = new ArrayList<>();
        try {
            if (tp.getImageList() != null) {
                postImageList = tp.getImageList().stream().map(i -> {
                    return PostImageDto.builder()
                            .ino(i.getIno())
                            .path(i.getPath())
                            .uuid(i.getUuid())
                            .imgName(i.getImgName())
                            .build();
                }).collect(Collectors.toList());
                postDto.setImageList(postImageList);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        try {
            if (tp.getPrevPostList() != null) {
                prevPostList = tp.getPrevPostList().stream().map(e -> entityToDto(e)).collect(Collectors.toList());
                dto.setPrevPostList(prevPostList);
            }
        } catch (Exception e) {
        }

        try {
            if (tp.getReplList() != null) {
                replyList = tp.getReplList().stream().map(e -> entityToDto(e)).collect(Collectors.toList());
                dto.setReplyList(replyList);
            }
        } catch (Exception e) {
        }

        if (tp.getOriCheck() == false || tp.getLinkCheck() == false) {
            dto.setOriCheck(tp.getOriCheck());
            dto.setLinkCheck(tp.getLinkCheck());
        }

        return dto;
    }
}
