package com.example.boardproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.boardproject.dto.PageRequestDto;
import com.example.boardproject.dto.PageResultDto;
import com.example.boardproject.dto.PostDto;
import com.example.boardproject.dto.TotalListRowDto;
import com.example.boardproject.dto.TotalPostDto;
import com.example.boardproject.service.PostService;
import com.example.boardproject.total.TotalPostListRow;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@Log4j2
@RequestMapping("/post")
public class BoardController {
    private final PostService service;

    @GetMapping("/list")
    public void getList(@ModelAttribute("requestDto") PageRequestDto requestDto, Model model) {
        log.info("getList()");
        PageResultDto<TotalListRowDto, TotalPostListRow> rDto = service.getList(requestDto);
        model.addAttribute("result", rDto);
    }

    @GetMapping("/read")
    public void getRead(@ModelAttribute("requestDto") PageRequestDto requestDto, Long pno, Model model) {
        log.info("getRead() " + pno);
        TotalPostDto dto = service.getRow(pno);
        model.addAttribute("result", dto);
    }

    @GetMapping("/delete")
    public void getDeletePage(PostDto dto, Model model) {
        log.info("getDeletePage() " + dto);
        model.addAttribute("result", dto);
    }

}
