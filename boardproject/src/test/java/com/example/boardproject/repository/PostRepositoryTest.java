package com.example.boardproject.repository;

import static org.mockito.Mockito.spy;

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
        LongStream.rangeClosed(61L, 90L).forEach(l -> {
            User user = User.builder().uno(l).build();

            Long randPno = (long) (Math.random() * 30) + 31;
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
            if (prevPost != null) {
                row.setPrevPost(prevPost);
                row.setPrevPostWriter(prevPostWriter);
            }
            list.add(row);
            posts.add(post);
            postNumsList.add(post.getPno());
        }

        Long[] postNums = postNumsList.toArray(new Long[postNumsList.size()]);

        List<PostImage> images = postImageRepository.findByPost(posts);
        List<Object[]> replies = postRepository.findByLastReferenceWithWriter(postNums);

        for (int i = 0; i < list.size(); i++) {
            TotalPostListRow row = list.get(i);

            List<PostImage> imageList = new ArrayList<>();
            for (PostImage postImage : images) {
                if (row.getPost().getPno() == postImage.getPost().getPno()) {
                    imageList.add(postImage);
                }
            }

            List<Post> replyList = new ArrayList<>();
            List<User> replyWriter = new ArrayList<>();
            for (Object[] reply : replies) {
                Post post = (Post) reply[0];
                if (row.getPost().getPno() == post.getLastReference()) {
                    replyList.add(post);
                    replyWriter.add((User) reply[1]);
                }
            }

            if (imageList.size() > 0 && replyList.size() > 0) {
                row.setImageList(imageList);
                row.setReplyList(replyList);
                row.setReplyWriters(replyWriter);
                list.set(i, row);
            } else if (imageList.size() > 0) {
                row.setImageList(imageList);
                list.set(i, row);
            } else if (replyList.size() > 0) {
                row.setReplyList(replyList);
                row.setReplyWriters(replyWriter);
                list.set(i, row);
            }
        }

        // 페이지 나누기
        requestDto.setPage(9);
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
        for (TotalPostListRow row : totalPageList.getContent()) {
            System.out.println(row);
            if (row.getReplyList() != null) {
                // for (Post reply : row.getReplyList()) {
                // System.out.println(reply.getWriter());
                // }

                for (int j = 0; j < row.getReplyList().size(); j++) {
                    System.out.println(row.getReplyWriters().get(j));
                }
            }
        }
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

        // // 개별글의 이미지 리스트 조회
        // totalPost.setImageList(postImageRepository.findByPost(totalPost.getPost()));

        // // 댓글 처리 (댓글의 댓글 리스트, 이미지 리스트)
        // // 개별글 id로 댓글 조회
        // List<Object[]> replies = postRepository.findByLastReferenceWithWriter(pno);
        // // 댓글에 대한 정보들을 담을 리스트 생성
        // List<TotalPostListRow> replyList = new ArrayList<>();
        // // 댓글이 조회된 경우
        // if (replies.size() > 0) {
        // // 댓글의 댓글을 조회하기 위한 아이디 담을 리스트, 배열 생성
        // List<Long> postNumsList = new ArrayList<>();
        // List<Post> posts = new ArrayList<>();

        // // 조회한 전체 댓글 목록으로 반복문
        // for (Object[] reply : replies) {
        // TotalPostListRow replyRow = new TotalPostListRow();
        // Post post = (Post) reply[0];
        // replyRow.setPost(post);
        // replyRow.setWriter((User) reply[1]);
        // replyList.add(replyRow);

        // postNumsList.add(post.getPno());
        // posts.add(post);
        // }

        // // 댓댓글 조회
        // Long[] postNums = postNumsList.toArray(new Long[postNumsList.size()]);
        // List<Object[]> reReplies =
        // postRepository.findByLastReferenceWithWriter(postNums);

        // // 댓글에 달린 이미지 전부 조회
        // List<PostImage> replyImages = postImageRepository.findByPost(posts);

        // // 개별글에 달린 댓글리스트 기준으로 반복
        // for (int i = 0; i < replyList.size(); i++) {
        // TotalPostListRow row = replyList.get(i);

        // // 댓댓글 담을 리스트 생성
        // List<Post> reReplyList = new ArrayList<>();
        // List<User> reReplyWriter = new ArrayList<>();
        // // 조회한 댓댓글로 반복문
        // for (Object[] reReply : reReplies) {
        // Post post = (Post) reReply[0];
        // if (post.getLastReference() == row.getPost().getPno()) {
        // reReplyList.add(post);
        // reReplyWriter.add((User) reReply[1]);
        // }
        // }

        // // 댓글에 달린 이미지 담을 리스트 생성
        // List<PostImage> replyImageList = new ArrayList<>();
        // // 조회한 이미지들로 반복문
        // for (PostImage pi : replyImages) {
        // if (pi.getPost() == row.getPost()) {
        // replyImageList.add(pi);
        // }
        // }

        // // 댓글리스트에 데이터 추가
        // if (reReplyList.size() > 0 && replyImageList.size() > 0) {
        // row.setReplyList(reReplyList);
        // row.setReplyWriters(reReplyWriter);
        // row.setImageList(replyImageList);
        // replyList.set(i, row);
        // } else if (reReplyList.size() > 0) {
        // row.setReplyList(reReplyList);
        // row.setReplyWriters(reReplyWriter);
        // replyList.set(i, row);
        // } else if (replyImageList.size() > 0) {
        // row.setImageList(replyImageList);
        // replyList.set(i, row);
        // }
        // }

        // // 댓글리스트를 개별글 데이터로 추가
        // totalPost.setReplList(replyList);

        // }

        System.out.println(totalPost.getPost().getLastReference());
        System.out.println(totalPost.getPost().getOriginalReference());
        System.out.println(prevPost);

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
                List<Post> replyList = new ArrayList<>();
                List<User> replyWriter = new ArrayList<>();
                if (objects != null) {
                    for (Object[] obj : objects) {
                        if ((Post) obj[0] != totalPost.getPost()) {
                            replyList.add((Post) obj[0]);
                            replyWriter.add((User) obj[1]);
                        }
                    }
                    if (replyList.size() > 0) {
                        prevPostRow.setReplyList(replyList);
                        prevPostRow.setReplyWriters(replyWriter);
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
                    List<Post> replyList = new ArrayList<>();
                    List<User> replyWriter = new ArrayList<>();
                    if (object2s != null) {
                        for (Object[] obj : object2s) {
                            replyList.add((Post) obj[0]);
                            replyWriter.add((User) obj[1]);
                        }
                        oriPostRow.setReplyList(replyList);
                        oriPostRow.setReplyWriters(replyWriter);
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
                            List<Object[]> replies = postRepository
                                    .findByLastReferenceWithWriter(prevPostRow.getPost().getPno());
                            if (replies != null) {
                                List<Post> post = new ArrayList<>();
                                List<User> user = new ArrayList<>();
                                for (Object[] objects3 : replies) {
                                    post.add((Post) objects3[0]);
                                    user.add((User) objects3[1]);
                                }
                                prevPostRow.setReplyList(post);
                                prevPostRow.setReplyWriters(user);
                            }
                        }
                    }
                }
                System.out.println(prevPostRow.getPost().getPno());
                System.out.println(totalPost.getPost().getOriginalReference());

                if (oriPostRow != null) {
                    prevPostList.add(oriPostRow);
                }
            }

            if (prevPostList != null) {
                totalPost.setPrevPostList(prevPostList);
            }
        }

        for (TotalPostListRow row : totalPost.getPrevPostList()) {
            System.out.println(row);
        }

    }

}
