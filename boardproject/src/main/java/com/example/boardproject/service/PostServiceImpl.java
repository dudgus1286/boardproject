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
        // 개별글의 이미지 리스트, 댓글 조회
        totalPost.setImageList(postImageRepository.findByPost(totalPost.getPost()));
        // 댓글 처리 (댓글의 댓글 리스트, 이미지 리스트)
        List<Object[]> result = postRepository.findByLastReferenceWithWriter(pno);
        totalPost.setReplList(getTotalPostListRow(result));

        // originalReference와 lastReference를 기준으로 이전글을 조회해서 리스트로 담기
        // 개별글에 아예 이전글, 최초글 정보가 없는 경우인지 확인(맨 처음에 작성된 글인지 확인)
        if (totalPost.getPost().getOriginalReference() == null) {
            System.out.println("최초글임");
            return entityToDto(totalPost);
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
                return entityToDto(totalPost);
            }

        } else {
            // 이전글 아이디는 있는데 찾지 못했다면(삭제된 경우) 체크
            totalPost.setLinkCheck(false);
            if (!check1) {
                // 이전글 == 최초글인 경우 체크 후 리턴
                totalPost.setOriCheck(false);
                System.out.println("개별글 상위글이 최초글인데 삭제됨");
                return entityToDto(totalPost);
            }
        }

        // 최초글 정보 조회(삭제된 경우 oriCheck 체크)
        List<Object[]> originalObject = postRepository.findByPnoWithWriter(totalPost.getPost().getOriginalReference());
        TotalPostListRow oriPostRow = new TotalPostListRow();
        if (!originalObject.isEmpty()) {
            oriPostRow = getPrevPostReply(originalObject, prevPostList);
            if (!totalPost.getLinkCheck()) {
                // 최초글은 조회되는데 개별글 바로 상단의 글은 조회 안 될 경우 바로 리턴
                prevPostList.add(oriPostRow);
                totalPost.setPrevPostList(prevPostList);
                System.out.println("개별글 바로 상단의 글 조회안됨");
                return entityToDto(totalPost);

            }
        } else {
            totalPost.setOriCheck(false);
            if (!totalPost.getLinkCheck()) {
                // 이전글이 삭제된 상황에서 최초글도 삭제되었으면 찾을 수 있는 게 없기 때문에 리턴
                totalPost.setLinkCheck(false);
                System.out.println("이전글도 상위글도 찾을 수 없음");
                return entityToDto(totalPost);
            }
        }

        // 직전에 찾은 상위 글의 바로 위의 글 == 최초글이 되기 전까지 이전글 거슬러 올라가기 반복
        Long lastRefNum = prevPost.getLastReference();
        while (lastRefNum != totalPost.getPost().getOriginalReference()) {
            // 이전글 조회
            List<Object[]> prevPostObject = postRepository.findByPnoWithWriter(lastRefNum);

            if (!prevPostObject.isEmpty()) {
                // 최초글 조회할 때와 같은 코드 메소드로 처리
                TotalPostListRow prevPostRow1 = getPrevPostReply(prevPostObject, prevPostList);
                lastRefNum = prevPostRow1.getPost().getLastReference();
                prevPostList.add(prevPostRow1);
            } else {
                // 이전글을 조회할 수 없는 경우 linkCheck 체크 후 반복문에서 나옴
                totalPost.setLinkCheck(false);
                System.out.println("최초글과 개별글 중간에 글 삭제됨");
                break;
            }
        }

        // 최초글을 마지막으로 리스트에 추가
        if (totalPost.getOriCheck()) {
            List<Post> replyList = oriPostRow.getReplyList();
            for (int i = 0; i < replyList.size(); i++) {
                if (replyList.get(i).getLastReference() == oriPostRow.getPost().getPno()) {
                    // ㅅㄴㅁㄹㄴㅁㅇㄹ
                }
            }
            prevPostList.add(oriPostRow);
        }

        // 찾을 수 있는 정보가 모두 입력된 리스트를 역순으로 정렬 후 totalPost에 담기
        if (!prevPostList.isEmpty()) {
            Collections.reverse(prevPostList);
            totalPost.setPrevPostList(prevPostList);
        }
        return entityToDto(totalPost);
    }

    @Override
    public boolean removePost(Long pno) {
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

        if (!list.isEmpty()) {
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
                    row.setReplyWriters(replyWriters);
                } else if (imageList.size() > 0) {
                    row.setImageList(imageList);
                } else if (replyList.size() > 0) {
                    row.setReplyList(replyList);
                    row.setReplyWriters(replyWriters);
                }
                list.set(i, row);
            }
        }
        return list;
    }

    public TotalPostListRow getPrevPostReply(List<Object[]> object, List<TotalPostListRow> list) {
        // 이전글과 거기에 달린 댓글, 이미지 처리해서 TotalPostListRow에 담는 메소드
        TotalPostListRow row = new TotalPostListRow();

        // result 변수에서 포스트와 작성자 정보 추출
        Post rowPost = new Post();
        for (Object[] obj : object) {
            rowPost = (Post) obj[0];
            row.setPost(rowPost);
            row.setWriter((User) obj[1]);
        }

        // 이미지 처리
        row.setImageList(postImageRepository.findByPost(rowPost));

        // 댓글 조회
        List<Object[]> rowPostReplies = postRepository.findByLastReferenceWithWriter(rowPost.getPno());
        List<Post> rowPostReplyList = new ArrayList<>();
        List<User> rowPostReplyWriter = new ArrayList<>();
        // 댓글이 조회될 경우 리스트로 처리해서 row에 담기
        if (!rowPostReplies.isEmpty()) {
            for (Object[] obj : rowPostReplies) {
                Post rowPostReply = (Post) obj[0];
                // 직전에 찾은 글 제외 다른 댓글이 있는 경우 이전글 댓글 리스트에 추가
                if (list.size() != 0) {
                    if (rowPostReply.getPno() != list.get(list.size() - 1).getPost().getPno()) {
                        rowPostReplyList.add(rowPostReply);
                        rowPostReplyWriter.add((User) obj[1]);
                    }
                } else {
                    rowPostReplyList.add(rowPostReply);
                    rowPostReplyWriter.add((User) obj[1]);
                }
            }
            if (rowPostReplyList.size() > 0) {
                row.setReplyList(rowPostReplyList);
                row.setReplyWriters(rowPostReplyWriter);
            }
        }
        return row;
    }
}
