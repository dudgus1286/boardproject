package com.example.boardproject.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.boardproject.dto.PageRequestDto;
import com.example.boardproject.dto.PageResultDto;
import com.example.boardproject.dto.TotalListRowDto;
import com.example.boardproject.dto.TotalPostDto;
import com.example.boardproject.entity.Post;
import com.example.boardproject.entity.PostImage;
import com.example.boardproject.entity.User;
import com.example.boardproject.repository.PostImageRepository;
import com.example.boardproject.repository.PostRepository;
import com.example.boardproject.repository.UserRepository;
import com.example.boardproject.total.TotalPost;
import com.example.boardproject.total.TotalPostListRow;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    private final PostImageRepository postImageRepository;

    private final UserRepository userRepository;

    @Override
    public PageResultDto<TotalListRowDto, TotalPostListRow> getList(PageRequestDto requestDto) {
        // 페이지 나누기 전 DB에서 데이터 조회
        List<Object[]> result = new ArrayList<>();
        if (requestDto.getType().equals("t")) {
            result = postRepository.findAllWithPrevPost("%" + requestDto.getKeyword() + "%");
        } else {
            result = postRepository.findAllWithPrevPost();
        }

        // 데이터를 리스트로 처리
        List<TotalPostListRow> list = getTotalPostListRow(result);

        // 페이지 나누기
        Pageable pageable = requestDto.getPageable(Sort.by("post").descending());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        Page<TotalPostListRow> totalPageList = new PageImpl<>(list.subList(start, end), pageable, list.size());
        Function<TotalPostListRow, TotalListRowDto> fn = (entity -> entityToDto(entity));
        return new PageResultDto<TotalListRowDto, TotalPostListRow>(totalPageList, fn);
    }

    @Override
    public TotalPostDto getRow(Long pno) {
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
        // 개별글의 이미지 리스트 조회
        totalPost.setImageList(postImageRepository.findByPost(totalPost.getPost()));
        // 댓글 처리 (댓글의 댓글 리스트, 이미지 리스트)
        List<Object[]> result = postRepository.findByLastReferenceWithWriter(pno);
        totalPost.setReplList(getTotalPostListRow(result));

        // originalReference와 lastReference를 기준으로 이전글을 조회해서 리스트로 담기
        // 이전글 정보를 모두 담을 리스트 생성
        List<TotalPostListRow> prevPostList = new ArrayList<>();

        // 개별글에 아예 이전글, 최초글 정보가 없는 경우인지 확인(맨 처음에 작성된 글인지 확인)
        if (totalPost.getPost().getOriginalReference() != null) {

            // originalReference != lastReference 인지 체크
            Boolean check1 = totalPost.getPost().getOriginalReference() != totalPost.getPost().getLastReference() ? true
                    : false;

            TotalPostListRow prevPostRow = new TotalPostListRow();
            // 이전글 정보 찾기
            if (prevPost != null) {
                prevPostRow.setPost(prevPost);
                prevPostRow.setWriter(prevPostWriter);
                prevPostRow.setImageList(postImageRepository.findByPost(prevPost));
                List<Object[]> objects = postRepository.findByLastReferenceWithWriter(prevPost.getPno());
                List<Post> prevPostReplyList = new ArrayList<>();
                List<User> prevPostReplyWriter = new ArrayList<>();
                if (objects != null) {
                    for (Object[] obj : objects) {
                        Post rrr = (Post) obj[0];
                        if (rrr.getPno() != totalPost.getPost().getPno()) {
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

                try {
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
                } catch (Exception e) {
                    totalPost.setLinkCheck(false);
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

        return entityToDto(totalPost);
    }

    @Override
    public boolean deletePost(Long pno) {
        try {
            postImageRepository.deleteByPost(Post.builder().pno(pno).build());
            postRepository.deleteById(pno);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<TotalPostListRow> getTotalPostListRow(List<Object[]> result) {
        // TotalPostListRow 가 담긴 List 반환하는 메소드
        List<TotalPostListRow> list = new ArrayList<>();
        // 포스트 조회
        List<Post> posts = new ArrayList<>();
        List<Long> postNumsList = new ArrayList<>();
        for (Object[] obj : result) {
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

        if (list.size() > 0) {
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
        }
        return list;
    }
}
