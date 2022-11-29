package com.sihoo.me.debook.controllers;

import com.sihoo.me.debook.applications.AuthenticationService;
import com.sihoo.me.debook.dto.SessionRequestData;
import com.sihoo.me.debook.dto.SessionResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/session")
@RestController
public class SessionController {
    private final AuthenticationService authenticationService;

    public SessionController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public SessionResponseData login(@RequestBody @Valid SessionRequestData sessionRequestData) {
        String email = sessionRequestData.getEmail();
        String password = sessionRequestData.getPassword();

        String accessToken = authenticationService.login(email, password);

        return new SessionResponseData(accessToken);
    }
}
