package com.sihoo.me.debook.dto;

import com.github.dozermapper.core.Mapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserUpdateRequest {

    @Mapping("password")
    @Size(message = "[ERROR] Password must over 8", min = 8)
    @NotEmpty(message = "[ERROR] Password can not be empty")
    private String password;

    @Mapping("nickName")
    @NotEmpty(message = "[ERROR] Nickname can not be empty")
    private String nickName;

    @Mapping("githubId")
    @NotEmpty(message = "[ERROR] GithubId can not be empty")
    private String githubId;
}
