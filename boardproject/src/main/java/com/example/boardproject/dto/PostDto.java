package com.example.boardproject.dto;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long pno;

    private String text;

    // 작성자 정보
    private Long uno;
    private String writerId;
    private String writerNickname;

    private Long originalReference;
    private Long lastReference;

    // 작성일자 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 포스트이미지
    private List<PostImageDto> imgList;
}
