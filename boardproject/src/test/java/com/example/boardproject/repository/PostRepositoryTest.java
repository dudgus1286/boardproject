package com.example.boardproject.repository;

import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
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
import com.example.boardproject.dto.PageResultDto;
import com.example.boardproject.dto.TotalListRowDto;
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

            // newPost.setOriginalReference(newPost.getPno());
            // newPost.setLastReference(newPost.getPno());
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
        LongStream.rangeClosed(91L, 120L).forEach(l -> {
            User user = User.builder().uno(l).build();

            Long randPno = (long) (Math.random() * 30) + 61;
            Post randPost = postRepository.findById(randPno).get();

            Post newPost = postRepository.save(Post.builder()
                    .text("post text... " + l)
                    .writer(user)
                    // .originalReference(randPost.getOriginalReference())
                    .lastReference(randPost.getPno())
                    .build());
            if (randPost.getOriginalReference() != null) {
                newPost.setOriginalReference(randPost.getOriginalReference());
            } else {
                newPost.setOriginalReference(randPost.getPno());
            }
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
        PageRequestDto requestDto = new PageRequestDto();

        /// 페이지 나누기 전 DB에서 데이터 조회
        List<TotalPostListRow> list = new ArrayList<>();

        List<Object[]> result1 = new ArrayList<>();
        if (requestDto.getType().equals("t")) {
            result1 = postRepository.findAllWithPrevPost("%" + requestDto.getKeyword() + "%");
        } else {
            result1 = postRepository.findAllWithPrevPost();
        }

        // 메소드로 만들 영역 시작
        // 포스트 조회
        List<Post> posts = new ArrayList<>();
        List<Long> postNumsList = new ArrayList<>();
        for (Object[] obj : result1) {
            Post post = (Post) obj[0];
            User writer = (User) obj[1];

            // 포스트 담기
            TotalPostListRow row = TotalPostListRow.builder()
                    .post(post)
                    .writer(writer)
                    .build();

            // 조회된 데이터 중 이전에 작성된 글이 있다면 이전글도 담기
            if (obj.length > 2) {
                Post prevPost = (Post) obj[2];
                User prevPostWriter = (User) obj[3];

                if (prevPost != null) {
                    row.setPrevPost(prevPost);
                    row.setPrevPostWriter(prevPostWriter);
                }
            }

            // 출력할 리스트, 이미지와 댓글 조회용 리스트에 담기
            list.add(row);
            posts.add(post);
            postNumsList.add(post.getPno());
        }

        Long[] postNums = postNumsList.toArray(new Long[postNumsList.size()]);
        // 조회한 포스트에 달린 이미지와 댓글 전부 조회
        List<PostImage> images = postImageRepository.findByPost(posts);
        List<Object[]> replies = postRepository.findByLastReferenceWithWriter(postNums);

        // 포스트 별로 이미지, 댓글 나눠서 삽입
        for (int i = 0; i < list.size(); i++) {
            // 개별 포스트 불려오기
            TotalPostListRow row = list.get(i);

            // 포스트별 이미지 분류
            List<PostImage> imageList = new ArrayList<>();
            for (PostImage postImage : images) {
                if (row.getPost().getPno() == postImage.getPost().getPno()) {
                    imageList.add(postImage);
                }
            }

            // 포스트별 댓글 분류
            List<Post> replyList = new ArrayList<>();
            List<User> replyWriters = new ArrayList<>();
            for (Object[] replyObj : replies) {
                Post reply = (Post) replyObj[0];
                if (row.getPost().getPno() == reply.getLastReference()) {
                    replyList.add(reply);
                    replyWriters.add((User) replyObj[1]);
                }
            }

            // 분류해서 리스트에 담긴 데이터가 있을 경우 row 에 담고 다시 리스트에 담기
            if (imageList.size() > 0 && replyList.size() > 0) {
                row.setImageList(imageList);
                row.setReplyList(replyList);
            } else if (imageList.size() > 0) {
                row.setImageList(imageList);
            } else if (replyList.size() > 0) {
                row.setReplyList(replyList);
            }
            list.set(i, row);
        }
        // 메소드로 만들 영역 끝

        // 페이지 나누기
        Pageable pageable = requestDto.getPageable(Sort.by("post").descending());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        Page<TotalPostListRow> totalPageList = new PageImpl<>(list.subList(start, end), pageable, list.size());

        // Function<TotalPostListRow, TotalListRowDto> fn = (entity ->
        // entityToDto(entity));
        // return new PageResultDto<TotalListRowDto, TotalPostListRow>(totalPageList,
        // fn);
    }

    @Transactional
    @Test
    public void getRow() {
        // 전체 데이터를 담을 객체 생성
        TotalPost totalPost = new TotalPost();

        // 조회하려는 개별글 조회
        Long pno = 89L;
        List<Object[]> result1 = postRepository.findWithPrevPost(pno);

        Post prevPost = new Post();
        User prevPostWriter = new User();

        for (Object[] obj : result1) {
            totalPost.setPost((Post) obj[0]);
            totalPost.setWriter((User) obj[1]);

            prevPost = (Post) obj[2];
            prevPostWriter = (User) obj[3];
        }

        // 개별글의 이미지 리스트 조회
        totalPost.setImageList(postImageRepository.findByPost(totalPost.getPost()));

        // 댓글 처리 (댓글의 댓글 리스트, 이미지 리스트)
        // 개별글 id로 댓글 조회
        List<Object[]> replies = postRepository.findByLastReferenceWithWriter(pno);
        // 댓글에 대한 정보들을 담을 리스트 생성
        List<TotalPostListRow> replyList = new ArrayList<>();
        // 댓글이 조회된 경우
        if (replies.size() > 0) {
            // 댓글의 댓글을 조회하기 위한 아이디 담을 리스트, 배열 생성
            List<Long> postNumsList = new ArrayList<>();
            List<Post> posts = new ArrayList<>();

            // 조회한 전체 댓글 목록으로 반복문
            for (Object[] reply : replies) {
                TotalPostListRow replyRow = new TotalPostListRow();
                Post post = (Post) reply[0];
                replyRow.setPost(post);
                replyRow.setWriter((User) reply[1]);
                replyList.add(replyRow);

                postNumsList.add(post.getPno());
                posts.add(post);
            }

            // 댓댓글 조회
            Long[] postNums = postNumsList.toArray(new Long[postNumsList.size()]);
            List<Object[]> reReplies = postRepository.findByLastReferenceWithWriter(postNums);

            // 댓글에 달린 이미지 전부 조회
            List<PostImage> replyImages = postImageRepository.findByPost(posts);

            // 개별글에 달린 댓글리스트 기준으로 반복
            for (int i = 0; i < replyList.size(); i++) {
                TotalPostListRow row = replyList.get(i);

                // 댓댓글 담을 리스트 생성
                List<Post> reReplyList = new ArrayList<>();
                List<User> reReplyWriter = new ArrayList<>();
                // 조회한 댓댓글로 반복문
                for (Object[] reReply : reReplies) {
                    Post post = (Post) reReply[0];
                    if (post.getLastReference() == row.getPost().getPno()) {
                        reReplyList.add(post);
                        reReplyWriter.add((User) reReply[1]);
                    }
                }

                // 댓글에 달린 이미지 담을 리스트 생성
                List<PostImage> replyImageList = new ArrayList<>();
                // 조회한 이미지들로 반복문
                for (PostImage pi : replyImages) {
                    if (pi.getPost() == row.getPost()) {
                        replyImageList.add(pi);
                    }
                }

                // 댓글리스트에 데이터 추가
                if (reReplyList.size() > 0 && replyImageList.size() > 0) {
                    row.setReplyList(reReplyList);
                    row.setReplyWriters(reReplyWriter);
                    row.setImageList(replyImageList);
                    replyList.set(i, row);
                } else if (reReplyList.size() > 0) {
                    row.setReplyList(reReplyList);
                    row.setReplyWriters(reReplyWriter);
                    replyList.set(i, row);
                } else if (replyImageList.size() > 0) {
                    row.setImageList(replyImageList);
                    replyList.set(i, row);
                }
            }

            // 댓글리스트를 개별글 데이터로 추가
            totalPost.setReplList(replyList);

        }

        // originalReference와 lastReference를 기준으로 이전글을 조회해서 리스트로 담기
        // 이전글 정보를 모두 담을 리스트 생성
        List<TotalPostListRow> prevPostList = new ArrayList<>();

        // 개별글에 아예 이전글, 최초글 정보가 없는 경우인지 확인(맨 처음에 작성된 글인지 확인)
        if (totalPost.getPost().getOriginalReference() != null) {

            // originalReference != lastReference 인지 체크
            Boolean check1 = totalPost.getPost().getOriginalReference() != totalPost.getPost().getLastReference() ? true
                    : false;

            // 이전글 정보 찾기
            TotalPostListRow prevPostRow = new TotalPostListRow();
            if (prevPost != null) {
                prevPostRow.setPost(prevPost);
                prevPostRow.setWriter(prevPostWriter);
                prevPostRow.setImageList(postImageRepository.findByPost(prevPost));
                List<Object[]> objects = postRepository.findByLastReferenceWithWriter(prevPost.getPno());
                List<Post> prevPostReplyList = new ArrayList<>();
                List<User> prevPostReplyWriter = new ArrayList<>();
                if (objects != null) {
                    for (Object[] obj : objects) {
                        if ((Post) obj[0] != totalPost.getPost()) {
                            prevPostReplyList.add((Post) obj[0]);
                            prevPostReplyWriter.add((User) obj[1]);
                        }
                    }
                    if (prevPostReplyList.size() > 0) {
                        prevPostRow.setReplyList(prevPostReplyList);
                        prevPostRow.setReplyWriters(prevPostReplyWriter);
                    }
                }
            } else {
                totalPost.setLinkCheck(false);
            }

            if (!check1) {
                if (prevPostRow.getPost() != null) {
                    prevPostList.add(prevPostRow);
                    totalPost.setPrevPostList(prevPostList);
                } else {
                    totalPost.setOriCheck(false);
                }
            } else {
                // 최초글 정보 조회(삭제된 경우 oriCheck 체크)
                List<Object[]> originalObject = postRepository
                        .findByPnoWithWriter(totalPost.getPost().getOriginalReference());
                TotalPostListRow oriPostRow = new TotalPostListRow();
                Post oriPost = new Post();
                if (originalObject != null) {
                    for (Object[] obj : originalObject) {
                        oriPost = (Post) obj[0];
                        oriPostRow.setPost(oriPost);
                        oriPostRow.setWriter((User) obj[1]);
                    }
                    oriPostRow.setImageList(postImageRepository.findByPost(oriPost));
                    List<Object[]> object2s = postRepository.findByLastReferenceWithWriter(oriPost.getPno());
                    List<Post> oriPostReplyList = new ArrayList<>();
                    List<User> oriPostReplyWriter = new ArrayList<>();
                    if (object2s != null) {
                        for (Object[] obj : object2s) {
                            oriPostReplyList.add((Post) obj[0]);
                            oriPostReplyWriter.add((User) obj[1]);
                        }
                        oriPostRow.setReplyList(oriPostReplyList);
                        oriPostRow.setReplyWriters(oriPostReplyWriter);
                    }
                } else {
                    totalPost.setOriCheck(false);
                }

                while (prevPostRow.getPost().getPno() != totalPost.getPost().getOriginalReference()) {
                    prevPostList.add(prevPostRow);

                    List<Object[]> objects = postRepository
                            .findByPnoWithWriter(prevPostRow.getPost().getLastReference());
                    prevPostRow = new TotalPostListRow();

                    if (objects != null) {
                        for (Object[] objects2 : objects) {
                            prevPostRow.setPost((Post) objects2[0]);
                            prevPostRow.setWriter((User) objects2[1]);
                            prevPostRow.setImageList(postImageRepository.findByPost((Post) objects2[0]));
                            List<Object[]> prevPostReplies = postRepository
                                    .findByLastReferenceWithWriter(prevPostRow.getPost().getPno());
                            if (prevPostReplies != null) {
                                List<Post> post = new ArrayList<>();
                                List<User> user = new ArrayList<>();
                                for (Object[] objects3 : prevPostReplies) {
                                    Post p = (Post) objects3[0];
                                    Post p1 = prevPostList.get(prevPostList.size() - 1).getPost();
                                    if (p.getPno() != p1.getPno()) {
                                        post.add(p);
                                        user.add((User) objects3[1]);
                                    }
                                }
                                prevPostRow.setReplyList(post);
                                prevPostRow.setReplyWriters(user);
                            }
                        }
                    }
                }

                if (oriPostRow != null) {
                    prevPostList.add(oriPostRow);
                }
            }

            if (prevPostList != null) {
                Collections.reverse(prevPostList);
                totalPost.setPrevPostList(prevPostList);
            }
        }

        for (TotalPostListRow row : totalPost.getPrevPostList()) {
            System.out.println(row.getPost().getPno());
            for (Post reply : row.getReplyList()) {
                System.out.println("\t" + reply.getPno());
            }
        }
        for (TotalPostListRow row : totalPost.getReplList()) {
            System.out.println(row.getPost().getPno());
        }

    }

    // @Test
    // public void tttest() {
    // LongStream.rangeClosed(5, 232).forEach(e -> {
    // try {
    // PostImage img = postImageRepository.findById(e).get();
    // img.setPath("test");
    // img.setUuid("uuid");
    // postImageRepository.save(img);
    // } catch (Exception i) {
    // }
    // });
    // }

    @Test
    public void deletePostTest() {
    }
}
