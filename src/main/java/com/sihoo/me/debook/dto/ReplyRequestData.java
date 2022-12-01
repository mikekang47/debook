package com.sihoo.me.debook.dto;

import com.github.dozermapper.core.Mapping;
import com.sihoo.me.debook.domains.Reply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ReplyRequestData {

    @Size(min = 2, message = "[ERROR] Message length must be longer than 2")
    @Mapping("message")
    private String message;

    public Reply toEntity(Long reviewId, Long userId) {
        return Reply.builder()
                .message(this.message)
                .reviewId(reviewId)
                .userId(userId)
                .build();
    }
}
