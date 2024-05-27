package com.example.boardproject.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString(exclude = { "writer" })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post extends BaseEntity {
    @Id
    @SequenceGenerator(name = "post_seq_gen", sequenceName = "post_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq_gen")
    private Long pno;

    @Column(nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    private User writer;

    // 최초글의 id 기록
    // 본글이 최초글일 경우 본인의 pno를 자동으로 입력
    // 다른글에 달리는 댓글일 경우 원댓글의 originalReference를 불려와서 입력
    // @ManyToOne(fetch = FetchType.LAZY)
    // private Post originalReference;
    private Long originalReference;

    // 상위의 가장 인접한 글의 id 기록
    // 본글이 최초글일 경우 본인의 pno를 자동으로 입력
    // 다른글에 달리는 댓글일 경우 원댓글의 pno를 불려와서 입력
    // @ManyToOne(fetch = FetchType.LAZY)
    // private Post lastReference;
    private Long lastReference;

    // @OneToMany(mappedBy = "post")
    // private List<PostImage> postImages;

    // @OneToMany(mappedBy = "prevPost")
    // private List<Reply> replies;
}
