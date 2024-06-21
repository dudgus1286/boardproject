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
import com.example.boardproject.entity.PostImage;
import com.example.boardproject.entity.User;
import com.example.boardproject.total.TotalPost;
import com.example.boardproject.total.TotalPostListRow;

public interface PostService {
    PageResultDto<TotalListRowDto, TotalPostListRow> getList(PageRequestDto requestDto);

    TotalPostDto getRow(Long pno);

    PostDto getDeletePage(Long pno);

    boolean removePost(Long pno);

    Long createPost(PostDto post);

    public default PostDto entityToDto(Post post, User writer, List<PostImage> list) {
        PostDto dto = PostDto.builder()
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

        if (list != null) {
            List<PostImageDto> postImageDtos = list.stream().map(i -> {
                return PostImageDto.builder()
                        .ino(i.getIno())
                        .path(i.getPath())
                        .uuid(i.getUuid())
                        .imgName(i.getImgName())
                        .build();
            }).collect(Collectors.toList());
            dto.setImageList(postImageDtos);
        }

        return dto;
    }

    public default Post dtoToEntity(PostDto dto) {
        return Post.builder()
                .pno(dto.getPno())
                .text(dto.getText())
                .writer(User.builder().uno(dto.getUno()).build())
                .originalReference(dto.getOriginalReference())
                .lastReference(dto.getLastReference())
                .build();
    }

    public default TotalListRowDto entityToDto(TotalPostListRow row) {
        TotalListRowDto dto = new TotalListRowDto();

        dto.setPost(entityToDto(row.getPost(), row.getWriter(), row.getImageList()));

        List<PostDto> prevPostList = new ArrayList<>();
        try {
            if (row.getPrevPost() != null) {
                prevPostList.add(entityToDto(row.getPrevPost(), row.getPrevPostWriter(), null));
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

                for (int i = 0; i < row.getReplyList().size(); i++) {
                    replyList.add(entityToDto(reply.get(i), replyWriter.get(i), null));
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

        dto.setPost(entityToDto(tp.getPost(), tp.getWriter(), tp.getImageList()));

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
