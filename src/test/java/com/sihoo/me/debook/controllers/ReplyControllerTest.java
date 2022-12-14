package com.sihoo.me.debook.controllers;

import com.sihoo.me.debook.applications.AuthenticationService;
import com.sihoo.me.debook.applications.ReplyService;
import com.sihoo.me.debook.domains.Reply;
import com.sihoo.me.debook.domains.Role;
import com.sihoo.me.debook.domains.RoleType;
import com.sihoo.me.debook.dto.ReplyRequestData;
import com.sihoo.me.debook.errors.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReplyController.class)
public class ReplyControllerTest {
    private static final Long EXISTS_USER_ID = 1L;
    private static final Long EXISTS_REPLY_ID = 5L;
    private static final Long EXISTS_REVIEW_ID = 6L;
    private static final Long NEW_REPLY_ID = 0L;

    private static final String EXISTS_TOKEN = "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ" +
            ".eyJ1c2VySWQiOjF9.xShOSgEwVSlvgg699JR4ieN8k3thMgbuDcV_rKEA8dA";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private ReplyService replyService;


    @Nested
    @DisplayName("create ????????????")
    class Describe_create {
        @Nested
        @DisplayName("?????????????????? ????????? ??????")
        class Context_when_authenticated_and_has_authority {
            @Nested
            @DisplayName("????????? ????????? ???????????? ???")
            class Context_when_valid_requests {
                @BeforeEach
                void setUp() {
                    given(authenticationService.getRoles(EXISTS_USER_ID)).willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));
                    given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);
                    given(replyService.createReply(any(ReplyRequestData.class), eq(EXISTS_REVIEW_ID), eq(NEW_REPLY_ID), eq(EXISTS_USER_ID))).will(invocation -> {
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
                @DisplayName("201??? ????????? ????????? ????????????.")
                void It_responds_201_and_review() throws Exception {
                    mvc.perform(post("/replies/" + EXISTS_REVIEW_ID)
                                    .content("{\"message\":\"?????? ????????? ???????????????\"}")
                                    .accept(MediaType.APPLICATION_JSON_UTF8)
                                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                                    .header("Authorization", "Bearer " + EXISTS_TOKEN)
                            )
                            .andExpect(content().string(containsString("?????? ????????? ???????????????")))
                            .andExpect(status().isCreated());
                }
            }

            @Nested
            @DisplayName("???????????? ?????? ????????? ???????????? ???")
            class Context_when_invalid_requests {
                @BeforeEach
                void setUp() {
                    given(authenticationService.getRoles(EXISTS_USER_ID)).willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));
                    given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);
                }

                @Test
                @DisplayName("201??? ????????? ????????? ????????????.")
                void It_responds_201_and_review() throws Exception {
                    mvc.perform(post("/replies/" + EXISTS_REVIEW_ID)
                                    .content("{\"message\":\"???\"}")
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
    @DisplayName("detailById ????????????")
    class Describe_detailById {
        @Nested
        @DisplayName("????????? ????????? ???")
        class Context_when_reply_exists {
            @BeforeEach
            void setUp() {
                Reply reply = Reply.builder()
                        .id(EXISTS_REPLY_ID)
                        .build();

                given(replyService.getReplyById(EXISTS_REPLY_ID)).willReturn(reply);
            }

            @Test
            @DisplayName("200??? ????????? ????????????.")
            void It_responds_200_and_reply() throws Exception {
                mvc.perform(get("/replies/" + EXISTS_REPLY_ID))
                        .andExpect(status().isOk());
            }
        }
    }

    @Nested
    @DisplayName("getMyReplies ????????????")
    class Describe_getMyReplies {
        @Nested
        @DisplayName("??????????????? ????????? ?????????")
        class Context_when_authorization_and_has_authority {
            @Nested
            @DisplayName("????????? ????????? ????????? ????????? ???")
            class Context_when_exists_reply {
                @BeforeEach
                void setUp() {
                    Reply reply1 = Reply.builder()
                            .userId(EXISTS_USER_ID)
                            .message("????????? ?????????")
                            .build();
                    Reply reply2 = Reply.builder()
                            .userId(EXISTS_USER_ID)
                            .message("????????? ?????????")
                            .build();

                    given(authenticationService.getRoles(EXISTS_USER_ID)).willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));
                    given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);
                    given(replyService.getMyReplies(EXISTS_USER_ID)).willReturn(List.of(reply1, reply2));
                }

                @Test
                @DisplayName("200??? ????????? ?????? ????????????.")
                void It_responds_200_and_replies() throws Exception {
                    mvc.perform(get("/replies/myreplies")
                                    .header("Authorization", "Bearer " + EXISTS_TOKEN)
                            )
                            .andExpect(status().isOk())
                            .andDo(print());
                }
            }

            @Nested
            @DisplayName("????????? ????????? ????????? ???????????? ?????? ???")
            class Context_when_not_exists_reply {
                @BeforeEach
                void setUp() {
                    given(authenticationService.getRoles(EXISTS_USER_ID)).willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));
                    given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);
                    given(replyService.getMyReplies(EXISTS_USER_ID)).willReturn(List.of());
                }

                @Test
                @DisplayName("200??? ??? ?????? ???????????? ????????????.")
                void It_responds_200_and_empty_replies() throws Exception {
                    mvc.perform(get("/replies/myreplies")
                                    .header("Authorization", "Bearer " + EXISTS_TOKEN)
                            )
                            .andExpect(status().isOk())
                            .andDo(print());
                }
            }
        }
    }

    @Nested
    @DisplayName("update ????????????")
    class Describe_update {
        @Nested
        @DisplayName("?????? ????????? ????????? ?????? ???")
        class Context_when_authorized_and_has_authority {
            @Nested
            @DisplayName("????????? ???????????? ????????? ????????? ?????????, ???????????? ????????? ????????? ???")
            class Context_when_reply_exists_and_valid_requests_and_same_user {
                @BeforeEach
                void setUp() {
                    given(authenticationService.getRoles(EXISTS_USER_ID)).willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));
                    given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);
                    given(replyService.updateReply(eq(EXISTS_REPLY_ID),
                            any(ReplyRequestData.class),
                            eq(EXISTS_USER_ID))).will(invocation -> {
                                ReplyRequestData replyRequestData = invocation.getArgument(1);
                                return Reply.builder()
                                        .id(EXISTS_REPLY_ID)
                                        .userId(EXISTS_USER_ID)
                                        .message(replyRequestData.getMessage())
                                        .build();
                            }
                    );
                }

                @Test
                @DisplayName("200??? ????????? ????????? ????????????.")
                void It_responds_200_and_updated_reply() throws Exception {
                    mvc.perform(patch("/replies/" + EXISTS_REPLY_ID)
                                    .accept(MediaType.APPLICATION_JSON_UTF8)
                                    .content("{\"message\":\"?????? ????????? ??????\"}")
                                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                                    .header("Authorization", "Bearer " + EXISTS_TOKEN)
                            )
                            .andDo(print())
                            .andExpect(content().string(containsString("?????? ????????? ??????")))
                            .andExpect(status().isOk());
                }
            }
        }
    }

    @Nested
    @DisplayName("delete ????????????")
    class Describe_delete {
        @Nested
        @DisplayName("????????? ??????????????? ????????? ?????? ???")
        class Context_when_authenticated_and_has_authority {
            @Nested
            @DisplayName("????????? ????????? ???")
            class Context_when_reply_exists {
                @BeforeEach
                void setUp() {
                    Reply reply = Reply.builder()
                            .id(EXISTS_REPLY_ID)
                            .userId(EXISTS_USER_ID)
                            .isDeleted(true)
                            .build();

                    given(authenticationService.getRoles(EXISTS_USER_ID)).willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));
                    given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);
                    given(replyService.deleteReply(EXISTS_REPLY_ID, EXISTS_USER_ID)).willReturn(reply);
                }

                @Test
                @DisplayName("204??? ????????????.")
                void It_responds_204() throws Exception {
                    mvc.perform(delete("/replies/" + EXISTS_REPLY_ID)
                                    .header("Authorization", "Bearer " + EXISTS_TOKEN)
                            )
                            .andExpect(status().isNoContent());
                }
            }

            @Nested
            @DisplayName("????????? ???????????? ?????? ???")
            class Context_when_reply_not_exists {
                @BeforeEach
                void setUp() {
                    given(authenticationService.getRoles(EXISTS_USER_ID)).willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));
                    given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);
                    given(replyService.deleteReply(EXISTS_REPLY_ID, EXISTS_USER_ID)).willThrow(
                            new CustomException("[ERROR] Reply not found(Id: " + EXISTS_REPLY_ID + ")", HttpStatus.NOT_FOUND)
                    );
                }

                @Test
                @DisplayName("204??? ????????????.")
                void It_responds_204() throws Exception {
                    mvc.perform(delete("/replies/" + EXISTS_REPLY_ID)
                                    .header("Authorization", "Bearer " + EXISTS_TOKEN)
                            )
                            .andExpect(status().isNotFound());
                }
            }
        }
    }
}
