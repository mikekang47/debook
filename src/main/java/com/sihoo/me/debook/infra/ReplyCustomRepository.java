package com.sihoo.me.debook.infra;

import com.sihoo.me.debook.domains.Reply;

import java.util.List;
import java.util.Optional;

public interface ReplyCustomRepository {
    Optional<Reply> findReviewById(Long id);

    List<Reply> findAllByReviewId(Long id);

    List<Reply> findAllByUserId(Long userId);
}
