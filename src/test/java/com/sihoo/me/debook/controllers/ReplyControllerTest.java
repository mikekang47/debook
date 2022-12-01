package com.sihoo.me.debook.controllers;

import com.sihoo.me.debook.applications.AuthenticationService;
import com.sihoo.me.debook.applications.ReplyService;
import com.sihoo.me.debook.domains.Reply;
import com.sihoo.me.debook.domains.Role;
import com.sihoo.me.debook.domains.RoleType;
import com.sihoo.me.debook.dto.ReplyRequestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReplyController.class)
public class ReplyControllerTest {
    private static final Long EXISTS_USER_ID = 1L;
    private static final Long EXISTS_REPLY_ID = 5L;
    private static final Long EXISTS_REVIEW_ID = 6L;

    private static final String EXISTS_TOKEN = "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ" +
            ".eyJ1c2VySWQiOjF9.xShOSgEwVSlvgg699JR4ieN8k3thMgbuDcV_rKEA8dA";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private ReplyService replyService;


    @Nested
    @DisplayName("create 메서드는")
    class Describe_create {
        @Nested
        @DisplayName("인증되었으며 권한이 있고")
        class Context_when_authenticated_and_has_authority {
            @Nested
            @DisplayName("올바른 요청이 들어왔을 때")
            class Context_when_valid_requests {
                @BeforeEach
                void setUp() {
                    given(authenticationService.getRoles(EXISTS_USER_ID)).willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));
                    given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);
                    given(replyService.createReply(any(ReplyRequestData.class), eq(EXISTS_REVIEW_ID), eq(EXISTS_USER_ID))).will(invocation -> {
                        ReplyRequestData source = invocation.getArgument(0);
                        return Reply.builder()
                                .id(EXISTS_REPLY_ID)
                                .message(source.getMessage())
                                .reviewId(EXISTS_REVIEW_ID)
                                .userId(EXISTS_USER_ID)
                                .build();
                    });
                }

                @Test
                @DisplayName("201과 생성된 리뷰를 응답한다.")
                void It_responds_201_and_review() throws Exception {
                    mvc.perform(post("/replies/" + EXISTS_REVIEW_ID)
                                    .content("{\"message\":\"이거 좋은지 모르겠던데\"}")
                                    .accept(MediaType.APPLICATION_JSON_UTF8)
                                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                                    .header("Authorization", "Bearer " + EXISTS_TOKEN)
                            )
                            .andExpect(content().string(containsString("이거 좋은지 모르겠던데")))
                            .andExpect(status().isCreated());
                }
            }

            @Nested
            @DisplayName("올바르지 않은 요청이 들어왔을 때")
            class Context_when_invalid_requests {
                @BeforeEach
                void setUp() {
                    given(authenticationService.getRoles(EXISTS_USER_ID)).willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));
                    given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);
                }

                @Test
                @DisplayName("201과 생성된 리뷰를 응답한다.")
                void It_responds_201_and_review() throws Exception {
                    mvc.perform(post("/replies/" + EXISTS_REVIEW_ID)
                                    .content("{\"message\":\"이\"}")
                                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                                    .header("Authorization", "Bearer " + EXISTS_TOKEN)
                            )
                            .andDo(print())
                            .andExpect(content().string(containsString("[ERROR] Message length must be longer than 2")))
                            .andExpect(status().isBadRequest());
                }
            }
        }
    }

    @Nested
    @DisplayName("detailById 메서드는")
    class Describe_detailById {
        @Nested
        @DisplayName("댓글이 존재할 때")
        class Context_when_reply_exists {
            @BeforeEach
            void setUp() {
                Reply reply = Reply.builder()
                        .id(EXISTS_REPLY_ID)
                        .build();

                given(replyService.getReviewById(EXISTS_REPLY_ID)).willReturn(reply);
            }
            @Test
            @DisplayName("200과 댓글을 응답한다.")
            void It_responds_200_and_reply() throws Exception {
                mvc.perform(get("/replies/" + EXISTS_REPLY_ID))
                        .andExpect(status().isOk());
            }
        }
    }
}
