package com.sihoo.me.debook.controllers;


import com.sihoo.me.debook.applications.ReplyService;
import com.sihoo.me.debook.domains.Reply;
import com.sihoo.me.debook.dto.ReplyRequestData;
import com.sihoo.me.debook.security.UserAuthentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/replies")
@RestController
public class ReplyController {
    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public Reply create(@RequestBody @Valid ReplyRequestData replyRequestData,
                        @PathVariable("id") Long reviewId,
                        @RequestParam(required = false, value = "targetReplyId") Long targetReplyId,
                        UserAuthentication userAuthentication) {
        Long authorId = userAuthentication.getUserId();
        if (targetReplyId == null) {
            targetReplyId = 0L;
        }
        return replyService.createReply(replyRequestData, reviewId, targetReplyId, authorId);
    }

    @GetMapping("/{id}")
    public Reply detailById(@PathVariable Long id) {
        return replyService.getReplyById(id);
    }

    @GetMapping("/reviews/{id}")
    public List<Reply> list(@PathVariable Long id) {
        return replyService.getRepliesByReviewId(id);
    }

    @GetMapping("/myreplies")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public List<Reply> getMyReplies(UserAuthentication userAuthentication) {
        Long userId = userAuthentication.getUserId();
        return replyService.getMyReplies(userId);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public Reply update(
            @PathVariable Long id,
            @RequestBody @Valid ReplyRequestData replyRequestData,
            UserAuthentication userAuthentication
    ) {
        Long userId = userAuthentication.getUserId();
        return replyService.updateReply(id, replyRequestData, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public void delete(
            @PathVariable Long id,
            UserAuthentication userAuthentication
    ) {
        Long userId = userAuthentication.getUserId();
        replyService.deleteReply(id, userId);
    }
}
