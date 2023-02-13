package com.sihoo.me.debook.controllers;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.sihoo.me.debook.applications.AuthenticationService;
import com.sihoo.me.debook.applications.UserService;
import com.sihoo.me.debook.domains.Role;
import com.sihoo.me.debook.domains.RoleType;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.UserRequestData;
import com.sihoo.me.debook.dto.UserUpdateRequest;
import com.sihoo.me.debook.errors.UnauthorizedException;
import com.sihoo.me.debook.errors.UserNotFoundException;

@WebMvcTest(UserController.class)
class UserControllerTest {
	private static final Long EXISTS_USER_ID = 1L;
	private static final Long SAME_CURRENT_USER_ID = 1L;
	private static final Long DIFF_CURRENT_USER_ID = 8L;
	private static final Long ROLE_ID = 1L;
	private static final Long NOT_EXISTS_USER_ID = 200L;
	private static final String EXISTS_NICK_NAME = "exists";
	private static final String NOT_EXISTS_NICK_NAME = "notexists";

	private static final String EXISTS_TOKEN = "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ" +
		".eyJ1c2VySWQiOjF9.xShOSgEwVSlvgg699JR4ieN8k3thMgbuDcV_rKEA8dA";

	private static final String DIFFERENT_USER_TOKEN = "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ." +
		"eyJ1c2VySWQiOjh9.FeDkrL0jqnT0_r4WdLiilJuKqHTFpws_5TfzSboTpKw";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private AuthenticationService authenticationService;

	@Nested
	@DisplayName("create 메서드는")
	class Describe_create {
		@Nested
		@DisplayName("올바른 요청이 들어왔을 때")
		class Context_when_gives_valid_requests {
			@BeforeEach
			void setUp() {
				given(userService.createUser(any(UserRequestData.class))).will(invocation -> {
					UserRequestData source = invocation.getArgument(0);
					return User.builder()
						.id(EXISTS_USER_ID)
						.email(source.getEmail())
						.password(source.getPassword())
						.githubId(source.getGithubId())
						.nickName(source.getNickName())
						.build();
				});
			}

			@Test
			@DisplayName("201 응답과 생성된 사용자를 응답한다.")
			void It_responds_201_and_user() throws Exception {
				mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"test@email.com\"," +
							"\"nickName\":\"Tester\"," +
							"\"password\":\"12345678901234\", " +
							"\"githubId\":\"test\"}")
					)
					.andExpect(content().string(containsString("test@email.com")))
					.andExpect(status().isCreated());
			}
		}

		@Nested
		@DisplayName("올바르지 않은 요청이 들어왔을 때")
		class Context_when_gives_invalid_requests {
			@Test
			@DisplayName("400을 응답한다.")
			void It_responds_400() throws Exception {
				mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"test\"," +
							"\"nickName\":\"Tester\", " +
							"\"gitNickName\":\"test\"}")
					)
					.andExpect(status().isBadRequest());
			}
		}
	}

	@Nested
	@DisplayName("detailById 메서드는")
	class Describe_detail {

		@Nested
		@DisplayName("유저가 존재할 때")
		class Context_when_exists_id {
			@BeforeEach
			void setUp() {
				User user = User.builder()
					.id(EXISTS_USER_ID)
					.email("test@email.com")
					.build();

				given(userService.getUserById(EXISTS_USER_ID)).willReturn(user);
			}

			@Test
			@DisplayName("200과 생성된 유저를 응답한다.")
			void It_responds_200_and_user() throws Exception {
				mockMvc.perform(get("/users/" + EXISTS_USER_ID)
						.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(content().string(containsString("test@email.com")))
					.andExpect(status().isOk());

				verify(userService).getUserById(EXISTS_USER_ID);
			}
		}

		@Nested
		@DisplayName("유저가 존재하지 않을 때")
		class Context_when_not_exists_id {
			@BeforeEach
			void setUp() {
				given(userService.getUserById(NOT_EXISTS_USER_ID)).willThrow(
					new UserNotFoundException(NOT_EXISTS_USER_ID));
			}

			@Test
			@DisplayName("404를 응답한다.")
			void It_responds_404() throws Exception {
				mockMvc.perform(get("/users/" + NOT_EXISTS_USER_ID)
						.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(status().isNotFound());

				verify(userService).getUserById(NOT_EXISTS_USER_ID);
			}
		}
	}

	@Nested
	@DisplayName("detailByName 메서드는")
	class Describe_detailByName {
		@Nested
		@DisplayName("요청 닉네임을 포함하는 유저가 있을 때")
		class Context_when_exists_contains_user_nick_name {
			@BeforeEach
			void setUp() {
				User user1 = User.builder()
					.nickName("exists_1")
					.build();

				User user2 = User.builder()
					.nickName("exists_2")
					.build();

				List<User> users = List.of(user1, user2);

				given(userService.getUserByNickName(EXISTS_NICK_NAME))
					.willReturn(users);
			}

			@Test
			@DisplayName("200과 포함된 유저 전체를 반환한다.")
			void It_responds_200_and_list_of_users() throws Exception {
				mockMvc.perform(get("/users/search/" + EXISTS_NICK_NAME)
						.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(content().string(containsString("exists_1")))
					.andExpect(content().string(containsString("exists_2")))
					.andExpect(status().isOk());

				verify(userService).getUserByNickName(EXISTS_NICK_NAME);
			}
		}

		@Nested
		@DisplayName("요청 닉네임을 포함하는 유저가 없을 때")
		class Context_when_not_exists_contains_user_nick_name {
			@BeforeEach
			void setUp() {
				given(userService.getUserByNickName(NOT_EXISTS_NICK_NAME)).willReturn(List.of());
			}

			@Test
			@DisplayName("200과 빈 리스트를 반환한다.")
			void It_responds_200_and_empty_list() throws Exception {
				mockMvc.perform(get("/users/search/" + NOT_EXISTS_NICK_NAME)
						.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(status().isOk());
			}
		}
	}

	@Nested
	@DisplayName("update 메서드는")
	class Describe_update {
		@Nested
		@DisplayName("사용자가 존재하고")
		class Context_when_user_exists {
			@Nested
			@DisplayName("현재 사용자와 같으며")
			class Context_when_same_user {
				@Nested
				@DisplayName("모든 데이터가 포함되어 있을 때")
				class Context_when_requests_not_null {
					@BeforeEach
					void setUp() {
						given(authenticationService.getRoles(EXISTS_USER_ID))
							.willReturn(List.of(new Role(1L, ROLE_ID, RoleType.USER)));

						given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);

						given(userService.updateUser(eq(EXISTS_USER_ID), any(UserUpdateRequest.class),
							eq(SAME_CURRENT_USER_ID))).will(invocation -> {
							UserUpdateRequest userRequestData = invocation.getArgument(1);
							return User.builder()
								.id(EXISTS_USER_ID)
								.password(userRequestData.getPassword())
								.githubId(userRequestData.getGithubId())
								.nickName(userRequestData.getNickName())
								.build();
						});
					}

					@Test
					@DisplayName("200과 수정된 유저를 응답한다.")
					void It_responds_200_and_updated_user() throws Exception {
						mockMvc.perform(patch("/users/" + EXISTS_USER_ID)
								.accept(MediaType.APPLICATION_JSON)
								.content(
									"{\"githubId\":\"hoo\", \"password\":\"newpassword\", \"nickName\":\"newNickName\"}")
								.contentType(MediaType.APPLICATION_JSON)
								.header("Authorization", "Bearer " + EXISTS_TOKEN)
							)
							.andExpect(content().string(containsString("hoo")))
							.andExpect(status().isOk());
					}
				}
			}

			@Nested
			@DisplayName("현재 사용자와 다를 때")
			class Context_when_different_current_user {
				@BeforeEach
				void setUp() {
					given(authenticationService.getRoles(DIFF_CURRENT_USER_ID))
						.willReturn(List.of(new Role(1L, DIFF_CURRENT_USER_ID, RoleType.USER)));

					given(authenticationService.parseToken(DIFFERENT_USER_TOKEN)).willReturn(DIFF_CURRENT_USER_ID);

					given(userService.updateUser(eq(EXISTS_USER_ID), any(UserUpdateRequest.class),
						eq(DIFF_CURRENT_USER_ID))).willThrow(
						new UnauthorizedException(DIFF_CURRENT_USER_ID)
					);
				}

				@Test
				@DisplayName("401을 응답한다.")
				void It_responds_401() throws Exception {
					mockMvc.perform(patch("/users/" + EXISTS_USER_ID)
							.accept(MediaType.APPLICATION_JSON)
							.content("{\"githubId\":\"hoo\", \"password\":\"newpassword\", \"nickName\":\"newNickName\"}")
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", "Bearer " + DIFFERENT_USER_TOKEN)
						)
						.andExpect(status().isUnauthorized());
				}
			}

			@Nested
			@DisplayName("올바르지 않은 요청이 있을 경우")
			class Context_when_nickname_is_blank {
				@BeforeEach
				void setUp() {
					given(authenticationService.getRoles(EXISTS_USER_ID))
						.willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));

					given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);
				}

				@Test
				@DisplayName("400을 반환한다.")
				void It_responds_400() throws Exception {
					mockMvc.perform(patch("/users/" + EXISTS_USER_ID)
							.accept(MediaType.APPLICATION_JSON)
							.content("{\"nickName\":\"\"}")
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", "Bearer " + EXISTS_TOKEN)
						)
						.andExpect(content().string(containsString("Nickname can not be empty")))
						.andExpect(status().isBadRequest());
				}
			}
		}
	}

	@Nested
	@DisplayName("delete 메서드는")
	class Describe_delete {
		@Nested
		@DisplayName("id가 존재하며")
		class Context_when_exists_id {
			@Nested
			@DisplayName("현재 사용자와 같을 때")
			class Context_when_same_current_user {
				@BeforeEach
				void setUp() {
					User user = User.builder()
						.isDeleted(true)
						.build();

					given(authenticationService.getRoles(EXISTS_USER_ID))
						.willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));

					given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);

					given(userService.deleteUser(EXISTS_USER_ID, SAME_CURRENT_USER_ID)).willReturn(user);
				}

				@Test
				@DisplayName("204를 응답한다.")
				void It_responds_204() throws Exception {
					mockMvc.perform(delete("/users/" + EXISTS_USER_ID)
							.header("Authorization", "Bearer " + EXISTS_TOKEN)
						)
						.andExpect(status().isNoContent());
				}
			}
		}

		@Nested
		@DisplayName("현재 사용자와 다를 때")
		class Context_when_same_current_user {
			@BeforeEach
			void setUp() {
				given(authenticationService.getRoles(DIFF_CURRENT_USER_ID))
					.willReturn(List.of(new Role(1L, DIFF_CURRENT_USER_ID, RoleType.USER)));

				given(authenticationService.parseToken(DIFFERENT_USER_TOKEN)).willReturn(DIFF_CURRENT_USER_ID);

				given(userService.deleteUser(EXISTS_USER_ID, DIFF_CURRENT_USER_ID)).willThrow(
					new UnauthorizedException(DIFF_CURRENT_USER_ID)
				);
			}

			@Test
			@DisplayName("401을 응답한다.")
			void It_responds_401() throws Exception {
				mockMvc.perform(delete("/users/" + EXISTS_USER_ID)
						.header("Authorization", "Bearer " + DIFFERENT_USER_TOKEN)
					)
					.andExpect(status().isUnauthorized());
			}
		}

		@Nested
		@DisplayName("id가 존재하지 않을 때")
		class Context_when_not_exists_id {
			@BeforeEach
			void setUp() {
				given(authenticationService.getRoles(EXISTS_USER_ID))
					.willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));

				given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);

				given(userService.deleteUser(NOT_EXISTS_USER_ID, EXISTS_USER_ID))
					.willThrow(new UserNotFoundException(NOT_EXISTS_USER_ID));
			}

			@Test
			@DisplayName("404를 응답한다.")
			void It_responds_404() throws Exception {
				mockMvc.perform(delete("/users/" + NOT_EXISTS_USER_ID)
						.header("Authorization", "Bearer " + EXISTS_TOKEN)
					)
					.andExpect(status().isNotFound());
			}
		}
	}
}
