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
    public void getListTest() {
        PageRequestDto requestDto = new PageRequestDto();
        PageResultDto<TotalListRowDto, TotalPostListRow> resultDto = service.getList(requestDto);

        System.out.println(resultDto.getPage());
        System.out.println(resultDto.getSize());
        System.out.println("start " + resultDto.getStart());
        System.out.println("end " + resultDto.getEnd());
        System.out.println(resultDto.isNext());
        System.out.println(resultDto.isPrev());
        System.out.println(resultDto.getTotalPage());
        System.out.println(resultDto.getPageList());

        // for (TotalPostDto dto : resultDto.getDtoList()) {
        // System.out.println(dto.getPost());
        // System.out.println(dto.getPrevPostList());
        // System.out.println(dto.getReplyList());

        // }
    }

    @Test
    public void getRow() {
        TotalPostDto dto = service.getRow(130L);
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
