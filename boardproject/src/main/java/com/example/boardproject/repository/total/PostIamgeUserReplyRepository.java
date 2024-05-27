package com.example.boardproject.repository.total;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostIamgeUserReplyRepository {
    Page<Object[]> getList(String type, String keyword, Pageable pageable);
}
