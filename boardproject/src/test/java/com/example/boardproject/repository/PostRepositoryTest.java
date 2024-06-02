package com.example.boardproject.repository;

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
                //     System.out.println(reply.getWriter());
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
        Long pno = 57L;
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
                for (Object[]  reReply : reReplies) {
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
        System.out.println(prevPost);
        if (totalPost.getPost().getLastReference() != null) {
            List<TotalPostListRow> prevPostList = new ArrayList<>();
            
            if (totalPost.getPost().getOriginalReference() != prevPost.getPno()) {
                Post oriPost = new Post();
                User oriPostWriter = new User();
                List<Object[]> originalPost = postRepository.findByPnoWithWriter(prevPost.getOriginalReference());
                for (Object[] obj : originalPost) {
                    oriPost = (Post) obj[0];
                    oriPostWriter = (User) obj[1];
                }
                if (oriPost == null) {
                    totalPost.setOriCheck(false);
                }

                while (prevPost.getPno() != totalPost.getPost().getOriginalReference()) {
                    TotalPostListRow prevPostrow = new TotalPostListRow();
                    prevPostrow.setPost(prevPost);
                    prevPostrow.setWriter(prevPostWriter);
                    prevPostrow.setImageList(postImageRepository.findByPost(prevPost));
    
                    List<Post> replyList2 = new ArrayList<>();
                    List<User> replyWriter = new ArrayList<>();
                    List<Object[]> replies2 = postRepository.findByLastReferenceWithWriter(prevPost.getPno());
                    for (Object[] obj : replies2) {
                        Post post2 = (Post) obj[0];
                        User user2 = (User) obj[1];
                        if (post2.getPno() != pno) {
                            replyList2.add(post2);
                            replyWriter.add(user2);
                        }
                    }
                    prevPostrow.setReplyList(replyList2);
                    prevPostrow.setReplyWriters(replyWriter);
                    prevPostList.add(prevPostrow);

                    List<Object[]> prevPost2 = postRepository.findByLastReferenceWithWriter(prevPost.getLastReference());
                    if (prevPost2 != null) {
                        for (Object[] obj : prevPost2) {
                            prevPost = (Post) obj[0];
                            prevPostWriter = (User) obj[1];
                        }
                    } else {
                        totalPost.setLinkCheck(false);
                        break;
                    }
                }
            } else {
                TotalPostListRow prevPostrow = new TotalPostListRow();
                prevPostrow.setPost(prevPost);
                prevPostrow.setWriter(prevPostWriter);
                prevPostrow.setImageList(postImageRepository.findByPost(prevPost));
    
                List<Post> replyList2 = new ArrayList<>();
                List<User> replyWriter = new ArrayList<>();
                List<Object[]> replies2 = postRepository.findByLastReferenceWithWriter(prevPost.getPno());
                for (Object[] obj : replies2) {
                    Post post2 = (Post) obj[0];
                    User user2 = (User) obj[1];
                    if (post2.getPno() != pno) {
                        replyList2.add(post2);
                        replyWriter.add(user2);
                    }
                }
                prevPostrow.setReplyList(replyList2);
                prevPostrow.setReplyWriters(replyWriter);
                prevPostList.add(prevPostrow);
            }
            totalPost.setPrevPostList(prevPostList);
        }


        System.out.println(totalPost);
        

    }

}
