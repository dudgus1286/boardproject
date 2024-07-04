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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
@Log4j2
@RequestMapping("/post")
public class BoardController {
    private final PostService service;

    @GetMapping("/list")
    public void getList(@ModelAttribute("requestDto") PageRequestDto requestDto, Model model) {
        log.info("리스트로 가기");
        PageResultDto<TotalListRowDto, TotalPostListRow> rDto = service.getList(requestDto);
        model.addAttribute("result", rDto);
    }

    @GetMapping("/read")
    public void getRead(@ModelAttribute("requestDto") PageRequestDto requestDto, Long pno, Model model) {
        log.info("포스트 개별페이지 " + pno);
        TotalPostDto dto = service.getRow(pno);
        model.addAttribute("result", dto);
    }

    @GetMapping("/delete")
    public void getDeletePage(@ModelAttribute("requestDto") PageRequestDto requestDto, Long pno, Model model) {
        log.info("포스트 삭제페이지 " + pno);
        model.addAttribute("result", service.getDeletePage(pno));
        model.addAttribute("requestDto", requestDto);
    }

    @PostMapping("/remove")
    public String removePost(Long pno, RedirectAttributes rttr) {
        log.info("포스트 삭제 " + pno);
        if (service.removePost(pno)) {
            rttr.addFlashAttribute("deletePno", pno);
        }

        return "redirect:/post/list";
    }

    @GetMapping("/posting")
    public void getPosting(PostDto dto, Model model, Long pno) {
        log.info("포스트 작성페이지");
        if (pno != null) {
            model.addAttribute("dto", service.getDeletePage(pno));
        } else {
            model.addAttribute("dto", dto);
        }
    }

    // @GetMapping("/modifying")
    // public void getPostModifying(Long pno) {
    // log.info("포스트 수정페이지 {}", pno);
    // }

    @PostMapping("/createPost")
    public String createPost(PostDto dto, RedirectAttributes rttr) {
        log.info("포스트 작성 " + dto);
        // rttr.addAttribute("pno", service.createPost(dto));
        Long pno = service.createPost(dto);

        // return "redirect:/post/read";
        return String.format("redirect:/post/read?pno=%d#mainPost", pno);
    }

}
