package com.example.boardproject.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.boardproject.dto.PageRequestDto;
import com.example.boardproject.dto.PageResultDto;
import com.example.boardproject.dto.TotalPostDto;
import com.example.boardproject.total.TotalPostListRow;


@SpringBootTest
public class PostServiceTest {
    @Autowired
    private PostService service;

    @Test
    public void getListTest() {
        PageRequestDto requestDto = new PageRequestDto();
        PageResultDto<TotalPostDto, TotalPostListRow> resultDto = service.getList(requestDto);

        for (TotalPostDto dto : resultDto.getDtoList()) {
            System.out.println(dto.getPost());
            System.out.println(dto.getPrevPostList());
            System.out.println(dto.getReplyList());
            
        }
    }
}
