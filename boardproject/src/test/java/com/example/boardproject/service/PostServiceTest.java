package com.example.boardproject.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.boardproject.dto.PageRequestDto;
import com.example.boardproject.dto.PageResultDto;
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

}
