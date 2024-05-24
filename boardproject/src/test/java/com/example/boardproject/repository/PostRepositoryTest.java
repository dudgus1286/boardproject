package com.example.boardproject.repository;

import static org.mockito.ArgumentMatchers.isNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    private PostThreaddRepository postThreaddRepository;

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

    // 포스트 수정 테스트
    @Test
    public void updatePostTest() {
        Post post = postRepository.findById(3L).get();
        post.setText("Update text...");
        postRepository.save(post);
    }

    // 포스트 삭제 테스트
    @Test
    public void deletePostTest() {
        postRepository.deleteById(47L);
    }

    // 더미 포스트 생성 2 (최상단글 바로 밑 댓글)
    @Test
    public void insertPostTest2() {
        LongStream.rangeClosed(31L, 60L).forEach(i -> {
            Long randPno = (long) ((int) (Math.random() * 29) + 1);
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
            Long randPno = (long) ((int) (Math.random() * 29) + 31);
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
            Long randPno = (long) ((int) (Math.random() * 29) + 61);
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
        LongStream.rangeClosed(1L, 120L).forEach(i -> {
            Post chPost = postRepository.findById(i).get();

            if (chPost.getPno() == chPost.getOriginalReference()) {
                System.out.println(chPost.getPno() + " 번 게시글은 상위게시글이 없는 글임");
            } else if (chPost.getLastReference() == chPost.getOriginalReference()) {
                System.out.println(
                        chPost.getPno() + " 번 게시글은 최상위글인 " + chPost.getOriginalReference() + " 의 바로 밑에 달린 댓글임");
            } else {
                System.out.println(chPost.getPno() + " 번 게시글은 다른 글의 댓글임");
                System.out.println("최상위글은 " + chPost.getOriginalReference() + " 번 글이고 " + chPost.getPno()
                        + " 바로 상단의 글은 " + chPost.getLastReference() + "번 게시글임");
            }
        });
    }

    // 특정글의 바로 상단의 글 확인하기
    @Transactional
    @Test
    public void readPrevPostTest() {
        Post post = postRepository.findById(120L).get();
        Post lastRef = postRepository.findById(post.getLastReference()).get();
        System.err.println(lastRef);
    }

    // 특정글 상위의 글들을 전부 리스트로 확인하기
    @Transactional
    @Test
    public void readPrevPostListTest() {
        // 특정글 조회
        Post post = postRepository.findById(118L).get();
        // 상위의 글 조회에 필요한 pno 조회
        Long oriLefNo = post.getOriginalReference();
        Long lastLefNo = post.getLastReference();

        // 최초글, 상위글을 담을 객체 생성
        Post oriPost = new Post();
        Post lastPost = new Post();
        // 조회되는 상위글 담을 리스트 생성
        List<Post> prevList = new ArrayList<>();

        // 객체가 없을 경우 Exception 발생
        // ==> try catch로 select 구문 실행 후 에러 시 check 개체에 표시
        Boolean oriLefCheck = null; // 아예 체크 안 했을 경우 => null, 조회 실패 시 false
        try {
            oriPost = postRepository.findById(oriLefNo).get();
            oriLefCheck = true;
        } catch (Exception e) {
            oriLefCheck = false;
        }

        Boolean lastLefCheck = null;
        try {
            lastPost = postRepository.findById(lastLefNo).get();
            lastLefCheck = true;
        } catch (Exception e) {
            lastLefCheck = false;
        }

        // lastPost 가 존재하는 경우 oriPost 바로 밑 댓글까지 찾아서 리스트에 담는 while문 실행
        if (lastLefCheck) {
            while (oriLefNo != lastPost.getPno()) {
                prevList.add(lastPost);
                try {
                    lastPost = postRepository.findById(lastPost.getLastReference()).get();
                } catch (Exception e) {
                    lastLefCheck = false;
                    break;
                }
            }
        }
        // oriPost 가 존재하는 경우 리스트에 담기
        if (oriLefCheck) {
            prevList.add(oriPost);
        }
        // 역순으로 정렬
        Collections.reverse(prevList);

        // 최종결과물
        for (Post post2 : prevList) {
            System.out.println(post2);
            System.out.println();
        }
        System.out.println();
        System.out.println();
        System.out.println(post);
        System.out.println(oriLefCheck);
        System.out.println(lastLefCheck);
        if (lastLefCheck) {
            System.out.println("중간에 글 삭제된 적 없음");
        } else if (!lastLefCheck && oriLefCheck) {
            System.out.println("최초글과 특정글 사이의 중간글이 삭제됨");
        } else {
            System.out.println("최초글 삭제됨");
        }
    }

    // 특정글의 댓글 리스트를 확인하기
    @Test
    public void readReplyListTest() {
        // PostRepository 에 findBy~() 메소드로 구현
        List<Post> list = postRepository.findByLastReferenceOrderByCreatedAtAsc(87L);
        for (Post list2 : list) {
            System.out.println(list2);
        }

    }

    // 랜덤하게 글에 달리는 더미 이미지 생성
    @Test
    public void insertPostImageTest() {
        LongStream.rangeClosed(1L, 120L).forEach(i -> {
            // 사진 입력할 게시글 조회
            try {
                Post post = postRepository.findById(i).get();

                // 0 ~ 5 까지의 숫자
                int randomNo = (int) (Math.random() * 6);

                // 최소 0 개부터 최대 4개까지의 이미지 삽입
                for (int j = 1; j < randomNo; j++) {
                    postImageRepository.save(PostImage.builder()
                            // .uuid(UUID.randomUUID().toString())
                            .post(post)
                            .imgName("img" + j + ".jpg")
                            .build());
                }
            } catch (Exception e) {
                //
            }
        });
    }

    // 게시글의 이미지와 함께 조회
    @Transactional
    @Test
    public void readPostImageTest() {
        Long pno = 15L;
        Post post = postRepository.findById(pno).get();
        // findBy ~ 메소드로 처리
        List<PostImage> imgList = postImageRepository.findByPostOrderByInoAsc(post);
        for (PostImage postImage : imgList) {
            System.out.println(postImage);
        }
    }

    // 쓰레드 만들기
    @Test
    public void insertThreaddTest() {
        LongStream.rangeClosed(1L, 120L).forEach(i -> {
            User user = User.builder().uno(i).build();
            threaddRepository.save(
                    Threadd.builder().creater(user).title("Threadd... " + i).text("Threadd text... " + i).build());
        });
    }

    // 쓰레드에 포스트 추가(= PostThreadd 생성)
    @Test
    public void insertPostThreadTest() {
        LongStream.rangeClosed(1L, 120L).forEach(i -> {
            Threadd threadd = Threadd.builder().tno(i).build();
            // 0 ~ 3 까지의 랜덤 숫자
            int randomNo = (int) (Math.random() * 4);
            // 랜덤한 개수대로 포스트 골라서 쓰레드에 저장
            for (int j = 0; j < randomNo; j++) {
                try {
                    Long randomPno = (long) (Math.random() * 120) + 1;
                    Post post = Post.builder().pno(randomPno).build();
                    postThreaddRepository.save(PostThreadd.builder().post(post).threadd(threadd).build());
                } catch (Exception e) {
                }
            }
        });
    }

    @Transactional
    @Test
    public void readPostThreadTest() {
        // 같은 쓰레드에 담긴 포스트 리스트를 출력
        List<PostThreadd> list = postThreaddRepository.findByThreadd(2L);
        for (PostThreadd postThread : list) {
            System.out.println(postThread);
        }
    }
}
