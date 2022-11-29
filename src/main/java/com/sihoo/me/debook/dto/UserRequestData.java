package com.sihoo.me.debook.dto;

import com.sihoo.me.debook.domains.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class UserRequestData {
    @NotNull
    @NotBlank
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    @NotNull
    private String nickName;

    @NotNull
    @NotBlank
    private String githubId;

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .nickName(this.nickName)
                .githubId(this.githubId)
                .build();
    }
}
