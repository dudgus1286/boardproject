package com.example.boardproject.repository;

import static org.mockito.ArgumentMatchers.isNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.boardproject.constant.MemberRole;
import com.example.boardproject.entity.Post;
import com.example.boardproject.entity.PostImage;
import com.example.boardproject.entity.PostThreadd;
import com.example.boardproject.entity.Threadd;
import com.example.boardproject.entity.User;

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
    private PostThreaddRepository postThreadRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 더미 유저 생성
    @Test
    public void insertUserTest() {
        IntStream.rangeClosed(1, 120).forEach(i -> {
            userRepository.save(
                    User.builder().userId("user" + i)
                            .password(passwordEncoder.encode("0000"))
                            .nickname("user" + i)
                            .role(MemberRole.MEMBER).build());
        });
    }

    // 더미 포스트 생성 1 (최상단글)
    @Test
    public void insertPostTest1() {
        LongStream.rangeClosed(1L, 30L).forEach(i -> {
            User user = User.builder().uno(i).build();
            Post newPost = postRepository.save(Post.builder().text("text... " + i).writer(user).build());
            newPost.setOriginalReference(newPost.getPno());
            newPost.setLastReference(newPost.getPno());
            postRepository.save(newPost);
        });
    }

    // 더미 포스트 생성 2 (최상단글 바로 밑 댓글)
    @Test
    public void insertPostTest2() {
        LongStream.rangeClosed(31L, 60L).forEach(i -> {
            Long randPno = (long) ((int)(Math.random()*29)+1) ;
            Post randPost = postRepository.findById(randPno).get();
            User user = User.builder().uno(i).build();
            postRepository.save(Post.builder()
                .text("text... " + i)
                .writer(user)
                .originalReference(randPost.getOriginalReference())
                .lastReference(randPost.getPno())
                .build());
        });
    }

    // 더미 포스트 생성 3 (최상단글 바로 밑 댓글의 댓글)
    @Test
    public void insertPostTest3() {
        LongStream.rangeClosed(61L, 90L).forEach(i -> {
            Long randPno = (long) ((int)(Math.random()*29)+31) ;
            Post randPost = postRepository.findById(randPno).get();
            User user = User.builder().uno(i).build();
            postRepository.save(Post.builder()
                .text("text... " + i)
                .writer(user)
                .originalReference(randPost.getOriginalReference())
                .lastReference(randPost.getPno())
                .build());
        });
    }

    // 더미 포스트 생성 4 (댓글의 댓글)
    @Test
    public void insertPostTest4() {
        LongStream.rangeClosed(91L, 120L).forEach(i -> {
            Long randPno = (long) ((int)(Math.random()*29)+61) ;
            Post randPost = postRepository.findById(randPno).get();
            User user = User.builder().uno(i).build();
            postRepository.save(Post.builder()
                .text("text... " + i)
                .writer(user)
                .originalReference(randPost.getOriginalReference())
                .lastReference(randPost.getPno())
                .build());
        });
    }

    // 포스트에 상위글이 있는지, 처음으로 작성된 글인지 확인하기
    @Test
    public void checkOriRefTest() {
        LongStream.rangeClosed(1L, 90L).forEach(i -> {
            Post chPost = postRepository.findById(i).get();
            
            if (chPost.getPno() == chPost.getOriginalReference()) {
                System.out.println(chPost.getPno() + " 번 게시글은 상위게시글이 없는 글임");
            }
            else if (chPost.getLastReference() == chPost.getOriginalReference()) {
                System.out.println(chPost.getPno()+" 번 게시글은 최상위글인 "+ chPost.getOriginalReference()+ " 의 바로 밑에 달린 댓글임");
            }
            else {
                System.out.println(chPost.getPno() + " 번 게시글은 다른 글의 댓글임");
                System.out.println("최상위글은 "+chPost.getOriginalReference()+" 번 글이고 " + chPost.getPno() + " 바로 상단의 글은 "+chPost.getLastReference()+"번 게시글임");
            }
        });
    }

    // 특정글의 상위글 리스트를 확인하기
    @Test
    public void readPrevPostList() {
        //
    }

    // 특정글의 댓글 리스트를 확인하기

    // 랜덤하게 글에 달리는 더미 이미지 생성
    @Test
    public void insertPostImageTest() {
        // LongStream.rangeClosed(1L, 120L).forEach(i -> {
        //     Post post = Post.builder().pno(i).build();
        //     postImageRepository.save(PostImage.builder().post(post).imgName("imgName" + i + ".png").build());
        // });
    }

    @Test
    public void insertThreaddTest() {
        LongStream.rangeClosed(1L, 100L).forEach(i -> {
            User user = User.builder().uno(i).build();
            threaddRepository.save(
                    Threadd.builder().creater(user).title("Threadd... " + i).text("Threadd text... " + i).build());
        });
    }

    @Test
    public void insertPostThreadTest() {
        LongStream.rangeClosed(1L, 100L).forEach(i -> {
            Post post = Post.builder().pno(i).build();
            Threadd threadd = Threadd.builder().tno(i).build();

            postThreadRepository.save(PostThreadd.builder().post(post).threadd(threadd).build());
        });
    }

    @Transactional
    @Test
    public void readPostThreadTest() {
        // 같은 쓰레드에 담긴 포스트 리스트를 출력
        List<PostThreadd> list = postThreadRepository.findByThreadd(2L);
        for (PostThreadd postThread : list) {
            System.out.println(postThread);
        }
    }
}
