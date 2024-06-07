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

    boolean removePost(Long pno);

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

        List<PostImageDto> postImageDtos = new ArrayList<>();
        try {
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
            e.printStackTrace();
        }
        dto.setPost(postDto);

        List<PostDto> prevPostList = new ArrayList<>();
        try {
            if (row.getPrevPost() != null) {
                post = row.getPrevPost();
                writer = row.getPrevPostWriter();
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
            e.printStackTrace();
        }

        List<PostDto> replyList = new ArrayList<>();
        try {
            if (row.getReplyList() != null) {
                List<Post> reply = row.getReplyList();
                List<User> replyWriter = row.getReplyWriters();

                int test = 0;
                for (int i = 0; i < row.getReplyList().size(); i++) {
                    test += 1;
                    postDto = PostDto.builder()
                            .pno(reply.get(i).getPno())
                            .text(reply.get(i).getText())
                            .createdAt(reply.get(i).getCreatedAt())
                            .updatedAt(reply.get(i).getUpdatedAt())
                            .originalReference(reply.get(i).getOriginalReference())
                            .lastReference(reply.get(i).getLastReference())
                            .uno(replyWriter.get(i).getUno())
                            .writerId(replyWriter.get(i).getUserId())
                            .writerNickname(replyWriter.get(i).getNickname())
                            .build();
                    replyList.add(postDto);
                }
                dto.setReplyList(replyList);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }

        List<TotalListRowDto> prevPostList = new ArrayList<>();
        try {
            if (tp.getPrevPostList() != null) {
                prevPostList = tp.getPrevPostList().stream().map(e -> entityToDto(e)).collect(Collectors.toList());
                dto.setPrevPostList(prevPostList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<TotalListRowDto> replyList = new ArrayList<>();
        try {
            if (tp.getReplList() != null) {
                replyList = tp.getReplList().stream().map(e -> entityToDto(e)).collect(Collectors.toList());
                dto.setReplyList(replyList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dto.setOriCheck(tp.getOriCheck());
        dto.setLinkCheck(tp.getLinkCheck());

        return dto;
    }
}
