package com.sihoo.me.debook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class SessionRequestData {
    @Email(message = "[ERROR] Email must wel-formed email address")
    @NotEmpty(message = "[ERROR] Email can not be empty or null")
    private String email;

    @NotEmpty(message = "[ERROR] Password can not be empty or null")
    @Size(min = 8, message = "[ERROR] Password must longer than 8")
    private String password;
}
