package com.example.boardproject.dto;

import java.util.List;

public class TotalPostDto {
    private PostDto postDto;
    private UserDto writerDto;

    private PostDto prevPostDto;
    private UserDto prevPostWriterDto;

    private List<PostImageDto> imagesDto;
    private List<PostDto> repliesDto;

}
