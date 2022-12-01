package com.sihoo.me.debook.controllers;


import com.sihoo.me.debook.applications.ReplyService;
import com.sihoo.me.debook.domains.Reply;
import com.sihoo.me.debook.dto.ReplyRequestData;
import com.sihoo.me.debook.security.UserAuthentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public Reply create(@RequestBody @Valid ReplyRequestData replyRequestData, @PathVariable("id") Long reviewId, UserAuthentication userAuthentication) {
        Long userId = userAuthentication.getUserId();
        return replyService.createReply(replyRequestData, reviewId, userId);
    }

}
