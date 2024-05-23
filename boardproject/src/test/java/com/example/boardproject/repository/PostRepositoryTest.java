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
import com.example.boardproject.entity.PostThread;
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
    private PostThreadRepository postThreadRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertUserTest() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            userRepository.save(
                    User.builder().userId("user" + i)
                            .password(passwordEncoder.encode("0000"))
                            .nickname("user" + i)
                            .role(MemberRole.MEMBER).build());
        });
    }

    @Test
    public void insertPostTest() {
        LongStream.rangeClosed(1L, 100L).forEach(i -> {
            User user = User.builder().uno(i).build();
            postRepository.save(Post.builder().text("text... " + i).writer(user).build());
        });
    }

    @Test
    public void insertPostImageTest() {
        LongStream.rangeClosed(1L, 100L).forEach(i -> {
            Post post = Post.builder().pno(i).build();
            postImageRepository.save(PostImage.builder().post(post).imgName("imgName" + i + ".png").build());
        });
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

            postThreadRepository.save(PostThread.builder().post(post).thread(threadd).build());
        });
    }

    @Transactional
    @Test
    public void readPostThreadTest() {
        // 같은 쓰레드에 담긴 포스트 리스트를 출력
        List<PostThread> list = postThreadRepository.findByThread(2L);
        for (PostThread postThread : list) {
            System.out.println(postThread);
        }
    }
}
