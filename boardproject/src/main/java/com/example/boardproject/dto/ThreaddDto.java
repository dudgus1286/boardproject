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
public class ThreaddDto {
    private Long tno;

    private String title;

    private String text;

    private Long creatorUno;
    private String creatorId;
    private String creatorNickname;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<PostDto> pDtoList;
}
