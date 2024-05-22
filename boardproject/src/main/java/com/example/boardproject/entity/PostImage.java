package com.example.boardproject.entity;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
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
public class PostImage {
    @Id
    @SequenceGenerator(name = "post_img_seq_gen", sequenceName = "post_img_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_img_seq_gen")
    private Long ino;

    private String path;

    private String uuid;

    private String imgName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
}
