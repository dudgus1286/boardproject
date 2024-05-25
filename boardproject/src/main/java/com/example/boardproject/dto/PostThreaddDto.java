package com.example.boardproject.dto;
import java.time.LocalDateTime;

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
public class PostThreaddDto {
    private Long ptid;

    private PostDto pDto;

    private ThreaddDto tDto;
    
    private LocalDateTime createdDate;
}
