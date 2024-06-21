package com.example.boardproject.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.boardproject.dto.PageRequestDto;
import com.example.boardproject.dto.PageResultDto;
import com.example.boardproject.dto.PostDto;
import com.example.boardproject.dto.TotalListRowDto;
import com.example.boardproject.dto.TotalPostDto;
import com.example.boardproject.total.TotalPostListRow;

@SpringBootTest
public class PostServiceTest {
    @Autowired
    private PostService service;

    @Test
    public void deleteServiceTest() {
        System.out.println(service.removePost(46L));
    }

    @Test
    public void createPostServiceTest() {
        PostDto dto = PostDto.builder()
                .pno(144L) // 글 수정일 때
                .uno(50L)
                .text("댓글을 또 수정")
                .originalReference(22L) // 다른 글의 댓글일 때
                .lastReference(107L) // 다른 글의 댓글일 때
                .build();
        System.out.println(service.createPost(dto));
    }

}
