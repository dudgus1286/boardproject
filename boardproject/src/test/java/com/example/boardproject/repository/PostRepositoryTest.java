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
        TotalPost result = new TotalPost();
        List<Object[]> result1 = postRepository.findWithPrevPost(pno);

        Post prevPost = new Post();
        User prevPostWriter = new User();

        for (Object[] obj : result1) {
            result.setPost((Post) obj[0]);
            result.setWriter((User) obj[1]);

            prevPost = (Post) obj[2];
            prevPostWriter = (User) obj[3];
        }
        result.setImageList(postImageRepository.findByPost(result.getPost()));

        System.out.println(result);

        // 댓글 처리 (댓글의 댓글 리스트, 이미지 리스트)
        List<Post> replies = postRepository.findByLastReference(pno);
        if (replies.size() > 0) {
            for (Post reply : replies) {
                if (reply.getPno() != reply.getOriginalReference()) {
                    System.out.println(reply);
                }
            }
        }

    }
}
