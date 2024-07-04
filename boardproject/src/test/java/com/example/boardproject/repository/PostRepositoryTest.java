package com.example.boardproject.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        List<Object[]> result1 = new ArrayList<>();
        if (requestDto.getType().equals("t")) {
            result1 = postRepository.findAllWithPrevPost("%" + requestDto.getKeyword() + "%");
        } else {
            result1 = postRepository.findAllWithPrevPost();
        }

        // TotalPostListRow 가 담긴 List 반환하는 메소드
        List<TotalPostListRow> list = new ArrayList<>();
        // 포스트 조회
        List<Post> posts = new ArrayList<>();
        List<Long> postNumsList = new ArrayList<>();
        for (Object[] obj : result1) {
            Post post = (Post) obj[0];
            User writer = (User) obj[1];

            // TotalPostListRow 객체 만들어서 포스트 담기
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
        // 메소드 끝
        // 페이지 나누기
        // Pageable pageable = requestDto.getPageable(Sort.by("post").descending());
        // int start = (int) pageable.getOffset();
        // int end = Math.min((start + pageable.getPageSize()), list.size());
        // Page<TotalPostListRow> totalPageList = new PageImpl<>(list.subList(start,
        // end), pageable, list.size());
        // Function<TotalPostListRow, TotalListRowDto> fn = (entity ->
        // entityToDto(entity));
        // return new PageResultDto<TotalListRowDto, TotalPostListRow>(totalPageList,
        // fn);
    }

    @Transactional
    @Test
    public void getRow() {
        Long pno = 77L;
        // 전체 데이터를 담을 객체 생성
        TotalPost totalPost = new TotalPost();

        // 조회하려는 개별글 처리
        List<Object[]> result1 = postRepository.findWithPrevPost(pno);
        Post prevPost = new Post();
        User prevPostWriter = new User();
        for (Object[] obj : result1) {
            totalPost.setPost((Post) obj[0]);
            totalPost.setWriter((User) obj[1]);

            prevPost = (Post) obj[2];
            prevPostWriter = (User) obj[3];
        }
        // 개별글의 이미지 리스트, 댓글 조회
        // totalPost.setImageList(postImageRepository.findByPost(totalPost.getPost()));
        // 댓글 처리 (댓글의 댓글 리스트, 이미지 리스트)
        // List<Object[]> result = postRepository.findByLastReferenceWithWriter(pno);
        // totalPost.setReplList(getTotalPostListRow(result));

        // originalReference와 lastReference를 기준으로 이전글을 조회해서 리스트로 담기
        // 개별글에 아예 이전글, 최초글 정보가 없는 경우인지 확인(맨 처음에 작성된 글인지 확인)
        if (totalPost.getPost().getOriginalReference() == null) {
            System.out.println("최초글임");
            return;
        }

        // 이전글 정보를 모두 담을 리스트 생성
        List<TotalPostListRow> prevPostList = new ArrayList<>();

        // 조회하려는 개별글을 기준으로 originalReference != lastReference 인지 체크
        boolean check1 = totalPost.getPost().getOriginalReference() != totalPost.getPost().getLastReference() ? true
                : false;

        // 개별글 조회와 같이 조회된 이전글 정보 찾기
        TotalPostListRow prevPostRow = new TotalPostListRow();
        if (prevPost != null) {
            prevPostRow.setPost(prevPost);
            prevPostRow.setWriter(prevPostWriter);
            prevPostRow.setImageList(postImageRepository.findByPost(prevPost));

            // 이전글에 달린 댓글 조회
            List<Object[]> prevReplies = postRepository.findByLastReferenceWithWriter(prevPost.getPno());
            List<Post> prevPostReplyList = new ArrayList<>();
            List<User> prevPostReplyWriter = new ArrayList<>();
            if (!prevReplies.isEmpty()) {
                for (Object[] obj : prevReplies) {
                    Post prevReply = (Post) obj[0];
                    // 개별글 제외 다른 댓글도 있는 경우 댓글 리스트에 추가
                    if (prevReply.getPno() != totalPost.getPost().getPno()) {
                        prevPostReplyList.add((Post) obj[0]);
                        prevPostReplyWriter.add((User) obj[1]);
                    }
                }
                // 댓글 리스트가 비어있지 않으면 이전글 데이터로 추가
                if (!prevPostReplyList.isEmpty()) {
                    prevPostRow.setReplyList(prevPostReplyList);
                    prevPostRow.setReplyWriters(prevPostReplyWriter);
                }
            }

            // 이전글 리스트에 추가
            prevPostList.add(prevPostRow);

            // 찾은 글이 최초글인 경우 이대로 리턴
            if (!check1) {
                totalPost.setPrevPostList(prevPostList);
                System.out.println("개별글 상위글이 최초글임");
                System.out.println(prevPostRow);
                return;
            }

        } else {
            // 이전글 아이디는 있는데 찾지 못했다면(삭제된 경우) 체크
            totalPost.setLinkCheck(false);
            if (!check1) {
                // 이전글 == 최초글인 경우 체크 후 리턴
                totalPost.setOriCheck(false);
                System.out.println("개별글 상위글이 최초글인데 삭제됨");
                return;
            }
        }

        // 최초글 정보 조회(삭제된 경우 oriCheck 체크)
        List<Object[]> originalObject = postRepository.findByPnoWithWriter(totalPost.getPost().getOriginalReference());
        TotalPostListRow oriPostRow = new TotalPostListRow();
        if (!originalObject.isEmpty()) {
            // 메소드 시작
            Post oriPost = new Post();
            for (Object[] obj : originalObject) {
                oriPost = (Post) obj[0];
                oriPostRow.setPost(oriPost);
                oriPostRow.setWriter((User) obj[1]);
            }
            oriPostRow.setImageList(postImageRepository.findByPost(oriPost));
            List<Object[]> oriPostReplies = postRepository.findByLastReferenceWithWriter(oriPost.getPno());
            List<Post> oriPostReplyList = new ArrayList<>();
            List<User> oriPostReplyWriter = new ArrayList<>();
            if (!oriPostReplies.isEmpty()) {
                for (Object[] obj : oriPostReplies) {
                    oriPostReplyList.add((Post) obj[0]);
                    oriPostReplyWriter.add((User) obj[1]);
                }
                oriPostRow.setReplyList(oriPostReplyList);
                oriPostRow.setReplyWriters(oriPostReplyWriter);
            }
            // 메소드 끝
        } else {
            totalPost.setOriCheck(false);
            if (!totalPost.getLinkCheck()) {
                // 이전글이 삭제된 상황에서 최초글도 삭제되었으면 찾을 수 있는 게 없기 때문에 리턴
                totalPost.setLinkCheck(false);
                System.out.println("이전글도 상위글도 찾을 수 없음");
                return;
            }
        }

        // 직전에 찾은 상위 글의 바로 위의 글 == 최초글이 되기 전까지 반복
        Long lastRefNum = prevPost.getLastReference();
        while (lastRefNum != totalPost.getPost().getOriginalReference()) {
            TotalPostListRow prevPostRow1 = new TotalPostListRow();
            // 이전글 조회
            List<Object[]> prevPostObject = postRepository.findByPnoWithWriter(lastRefNum);

            if (!prevPostObject.isEmpty()) {
                // 최초글 조회할 때와 같은 코드 메소드로 처리
                Post prevPost1 = new Post();
                for (Object[] obj : prevPostObject) {
                    prevPost1 = (Post) obj[0];
                    prevPostRow1.setPost(prevPost1);
                    prevPostRow1.setWriter((User) obj[1]);
                }
                prevPostRow1.setImageList(postImageRepository.findByPost(prevPost1));
                List<Object[]> oriPostReplies = postRepository.findByLastReferenceWithWriter(prevPost1.getPno());
                List<Post> oriPostReplyList = new ArrayList<>();
                List<User> oriPostReplyWriter = new ArrayList<>();
                if (!oriPostReplies.isEmpty()) {
                    for (Object[] obj : oriPostReplies) {
                        oriPostReplyList.add((Post) obj[0]);
                        oriPostReplyWriter.add((User) obj[1]);
                    }
                    prevPostRow1.setReplyList(oriPostReplyList);
                    prevPostRow1.setReplyWriters(oriPostReplyWriter);
                }
                // 메소드 끝
            } else {
                // 이전글을 조회할 수 없는 경우 linkCheck 체크 후 반복문에서 나옴
                totalPost.setLinkCheck(false);
                break;
            }

            lastRefNum = prevPostRow1.getPost().getLastReference();
            prevPostList.add(prevPostRow1);
        }

        // 마지막으로 최초글 조회된 경우 리스트에 추가
        if (totalPost.getOriCheck()) {
            prevPostList.add(oriPostRow);
        }

        // 찾을 수 있는 정보가 모두 입력된 리스트를 역순으로 정렬 후 totalPost에 담기
        if (!prevPostList.isEmpty()) {
            Collections.reverse(prevPostList);
            totalPost.setPrevPostList(prevPostList);
        }

        System.out.println(totalPost.getOriCheck() ? "" : "최초글 삭제됨");
        System.out.println(!totalPost.getLinkCheck() ? "상위글 삭제됨" : "");
        for (TotalPostListRow obj : totalPost.getPrevPostList()) {
            System.out.println(obj);
        }
        return;

        // try {
        // while (prevPostRow.getPost().getPno() !=
        // totalPost.getPost().getOriginalReference()) {
        // prevPostList.add(prevPostRow);

        // List<Object[]> objects = postRepository
        // .findByPnoWithWriter(prevPostRow.getPost().getLastReference());
        // prevPostRow = new TotalPostListRow();

        // if (objects != null) {
        // for (Object[] objects2 : objects) {
        // prevPostRow.setPost((Post) objects2[0]);
        // prevPostRow.setWriter((User) objects2[1]);
        // prevPostRow.setImageList(postImageRepository.findByPost((Post) objects2[0]));
        // List<Object[]> prevPostReplies = postRepository
        // .findByLastReferenceWithWriter(prevPostRow.getPost().getPno());
        // if (prevPostReplies != null) {
        // List<Post> post = new ArrayList<>();
        // List<User> user = new ArrayList<>();
        // for (Object[] objects3 : prevPostReplies) {
        // Post p = (Post) objects3[0];
        // Post p1 = prevPostList.get(prevPostList.size() - 1).getPost();
        // if (p.getPno() != p1.getPno()) {
        // post.add(p);
        // user.add((User) objects3[1]);
        // }
        // }
        // prevPostRow.setReplyList(post);
        // prevPostRow.setReplyWriters(user);
        // }
        // }
        // }
        // }
        // } catch (Exception e) {
        // totalPost.setLinkCheck(false);

        // if (oriPostRow != null) {
        // prevPostList.add(oriPostRow);
        // }
        // }

        // return entityToDto(totalPost);
    }

    @Test
    public void deletePostTest() {
        Long pno = 4L;
        postImageRepository.deleteByPost(Post.builder().pno(pno).build());
        postRepository.deleteById(pno);
        // System.out.println(postRepository.findById(1L).get());
    }

    @Test
    public void createPostTest() {
        Long uno = 1L;
        Long pno = postRepository.save(Post.builder()
                .text("안녕하세요?")
                .writer(User.builder().uno(uno).build())
                .build()).getPno();
        System.out.println(pno);
    }

    @Test
    public void testtt1() {
        System.out.println(postRepository.findById(182L).get());

    }
}
