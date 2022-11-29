package com.sihoo.me.debook.controllers;

import com.sihoo.me.debook.applications.UserService;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.UserRequestData;
import com.sihoo.me.debook.dto.UserUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody @Valid UserRequestData userRequestData) {
        return userService.createUser(userRequestData);
    }

    @GetMapping("/{id}")
    public User detailById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/search/{nickName}")
    public List<User> detailByNickName(@PathVariable String nickName) {
        return userService.getUserByNickName(nickName);
    }

    @GetMapping
    public List<User> list() {
        return userService.getUsers();
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        return userService.updateUser(id, userUpdateRequest);
    }
}
