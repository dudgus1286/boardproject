package com.example.boardproject.repository;

import static org.mockito.Mockito.lenient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.boardproject.constant.MemberRole;
import com.example.boardproject.dto.PageRequestDto;
import com.example.boardproject.entity.Post;
import com.example.boardproject.entity.PostImage;
import com.example.boardproject.entity.User;
import com.example.boardproject.total.TotalPost;
import com.example.boardproject.total.TotalPostListRow;

import jakarta.transaction.Transactional;

@SpringBootTest
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private ThreaddRepository threaddRepository;

    @Autowired
    private PostThreaddRepository postThreaddRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertUser() {
        LongStream.rangeClosed(1L, 120L).forEach(l -> {
            userRepository.save(User.builder()
                    .userId("user_" + l)
                    .nickname("user " + l)
                    .password(passwordEncoder.encode("0000"))
                    .role(MemberRole.MEMBER)
                    .build());
        });
    }

    @Test
    public void insertPost1() {
        LongStream.rangeClosed(1L, 30L).forEach(l -> {
            User user = User.builder().uno(l).build();

            Post newPost = postRepository.save(Post.builder()
                    .text("post text... " + l)
                    .writer(user)
                    .build());

            newPost.setOriginalReference(newPost.getPno());
            newPost.setLastReference(newPost.getPno());
            postRepository.save(newPost);

            int random = (int) (Math.random() * 5);
            for (int i = 1; i <= random; i++) {
                postImageRepository.save(PostImage.builder()
                        .imgName("img" + i + ".jpg")
                        .post(newPost)
                        .build());
            }
        });
    }

    @Test
    public void insertPost2() {
        LongStream.rangeClosed(61L, 90L).forEach(l -> {
            User user = User.builder().uno(l).build();

            Long randPno = (long) (Math.random() * 30) + 31;
            Post randPost = postRepository.findById(randPno).get();

            Post newPost = postRepository.save(Post.builder()
                    .text("post text... " + l)
                    .writer(user)
                    .originalReference(randPost.getOriginalReference())
                    .lastReference(randPost.getPno())
                    .build());
            postRepository.save(newPost);

            int random = (int) (Math.random() * 5);
            for (int i = 1; i <= random; i++) {
                postImageRepository.save(PostImage.builder()
                        .imgName("img" + i + ".jpg")
                        .post(newPost)
                        .build());
            }
        });
    }

    @Transactional
    @Test
    public void getListTest() {
        // 페이지 나누기 전 DB에서 데이터 조회
        List<TotalPostListRow> list = new ArrayList<>();

        PageRequestDto requestDto = new PageRequestDto();
        List<Object[]> result1 = new ArrayList<>();
        if (requestDto.getType().equals("t")) {
            result1 = postRepository.findAllWithPrevPost(requestDto.getKeyword());
        } else {
            result1 = postRepository.findAllWithPrevPost();
        }

        List<Post> posts = new ArrayList<>();
        List<Long> postNumsList = new ArrayList<>();
        for (Object[] obj : result1) {
            Post post = (Post) obj[0];
            User writer = (User) obj[1];
            Post prevPost = (Post) obj[2];
            User prevPostWriter = (User) obj[3];

            TotalPostListRow row = TotalPostListRow.builder()
                    .post(post)
                    .writer(writer)
                    .build();
            if (post != prevPost) {
                row.setPrevPost(prevPost);
                row.setPrevPostWriter(prevPostWriter);
            }
            list.add(row);
            posts.add(post);
            postNumsList.add(post.getPno());
        }
        Long[] postNums = postNumsList.toArray(new Long[postNumsList.size()]);

        List<PostImage> images = postImageRepository.findByPost(posts);
        List<Post> replies = postRepository.findByLastReference(postNums);

        for (int i = 0; i < list.size(); i++) {
            TotalPostListRow row = list.get(i);

            List<PostImage> imageList = new ArrayList<>();
            for (PostImage postImage : images) {
                if (row.getPost().getPno() == postImage.getPost().getPno()) {
                    imageList.add(postImage);
                }
            }

            List<Post> replyList = new ArrayList<>();
            for (Post post : replies) {
                if (row.getPost().getPno() == post.getLastReference()) {
                    replyList.add(post);
                }
            }

            if (imageList.size() > 0 && replyList.size() > 0) {
                row.setImageList(imageList);
                row.setReplyList(replyList);
                list.set(i, row);
            } else if (imageList.size() > 0) {
                row.setImageList(imageList);
                list.set(i, row);
            } else if (replyList.size() > 0) {
                row.setReplyList(replyList);
                list.set(i, row);
            }
        }

        // 페이지 나누기
        // requestDto.setPage(9);
        Pageable pageable = requestDto.getPageable(Sort.by("post").descending());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        Page<TotalPostListRow> totalPageList = new PageImpl<>(list.subList(start, end), pageable, list.size());

        System.out.println(totalPageList);
        System.out.println(totalPageList.getNumber());
        System.out.println(totalPageList.getNumberOfElements());
        System.out.println(totalPageList.getSize());
        System.out.println(totalPageList.getTotalElements());
        System.out.println(totalPageList.getTotalPages());
        System.out.println(totalPageList.getSort());
        System.out.println(totalPageList.getPageable());
        System.out.println(totalPageList.getClass());
        System.out.println(totalPageList.get());
        for (TotalPostListRow row : totalPageList.getContent()) {
            System.out.println(row);
            if (row.getReplyList() != null) {
                for (Post reply : row.getReplyList()) {
                    System.out.println(reply.getWriter());
                }
            }
        }
    }

    @Test
    public void getRow() {
        Long pno = 47L;
        TotalPost totalPost = new TotalPost();
        List<Object[]> result1 = postRepository.findWithPrevPost(pno);

        Post prevPost = new Post();
        User prevPostWriter = new User();

        for (Object[] obj : result1) {
            totalPost.setPost((Post) obj[0]);
            totalPost.setWriter((User) obj[1]);

            prevPost = (Post) obj[2];
            prevPostWriter = (User) obj[3];
        }
        totalPost.setImageList(postImageRepository.findByPost(totalPost.getPost()));

        // 댓글 처리 (댓글의 댓글 리스트, 이미지 리스트)
        // 개별글 id로 댓글 조회
        List<Post> replies = postRepository.findByLastReference(pno);

        // 댓글과 댓글의 댓글, 이미지 등을 담을 리스트 생성
        List<TotalPostListRow> replyList = new ArrayList<>();

        // 댓글이 한 개 이상일 경우
        if (replies.size() > 0) {
            // 댓글의 댓글을 조회하기 위한 아이디 담을 리스트, 배열 생성
            List<Long> postNumsList = new ArrayList<>();

            // 조회한 전체 댓글 목록으로 반복문
            for (Post reply : replies) {
                // 조건에 맞는 포스트를 댓글 리스트, 댓댓글 조회용 리스트에 담음.
                if (reply.getPno() != reply.getOriginalReference()) {
                    TotalPostListRow replyRow = new TotalPostListRow();
                    replyRow.setPost(reply);
                    replyList.add(replyRow);
                    postNumsList.add(reply.getPno());
                }
            }

            // 댓댓글 조회
            Long[] postNums = postNumsList.toArray(new Long[postNumsList.size()]);
            List<Post> reReplies = postRepository.findByLastReference(postNums);

            // 댓글리스트 기준으로 반복
            for (int i = 0; i < replyList.size(); i++) {
                TotalPostListRow row = replyList.get(i);
                List<Post> reReplyList = new ArrayList<>();
                for (Post reReply : reReplies) {
                    if (reReply.getLastReference() == row.getPost().getPno()) {
                        reReplyList.add(reReply);
                    }
                }
                
                if (reReplyList.size() > 0) {
                    row.setReplyList(reReplyList);
                    replyList.set(i, row);
                }
            }

        }

    }
}
