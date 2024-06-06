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
    public void getRow() {
        TotalPostDto dto = service.getRow(77L);
        System.out.println(dto.getPost());
        for (TotalListRowDto prevRow : dto.getPrevPostList()) {
            System.out.println(prevRow);
        }
        if (dto.getReplyList() != null) {
            for (TotalListRowDto reply : dto.getReplyList()) {
                System.out.println(reply);
            }
        }
        System.out.println(dto.getLinkCheck());
        System.out.println(dto.getOriCheck());
        if (!dto.getLinkCheck()) {
            System.out.println("상위글이 삭제되었습니다");
        }
        if (!dto.getOriCheck()) {
            System.out.println("원본글이 삭제되었습니다");
        }
    }

}
