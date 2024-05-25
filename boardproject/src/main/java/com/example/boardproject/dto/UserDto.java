package com.example.boardproject.dto;

import java.time.LocalDateTime;

import com.example.boardproject.constant.MemberRole;
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
public class UserDto {
    private Long uno;

    private String userId;

    private String password;

    private String nickname;

    private MemberRole role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
