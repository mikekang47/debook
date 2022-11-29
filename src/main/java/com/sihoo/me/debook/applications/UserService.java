package com.sihoo.me.debook.applications;

import com.github.dozermapper.core.Mapper;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.UserRequestData;
import com.sihoo.me.debook.dto.UserUpdateRequest;
import com.sihoo.me.debook.errors.CustomException;
import com.sihoo.me.debook.infra.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Service
@Transactional(readOnly = true)
public class UserService {
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final Mapper mapper;

    public UserService(RoleService roleService, UserRepository userRepository, Mapper mapper) {
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User createUser(UserRequestData userRequestData) {
        User user = userRepository.save(userRequestData.toEntity());

        roleService.createRole(user);

        return user;
    }

    public User getUserById(Long id) {
        return findUser(id);
    }

    public List<User> getUserByNickName(String nickName) {
        return userRepository.findUserByNickName(nickName);
    }

    public List<User> getUsers() {
        return userRepository.findUsers();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User updateUser(Long id, UserUpdateRequest userUpdateRequest, Long userId) {
        if (!Objects.equals(id, userId)) {
            throw new CustomException("[ERROR] Can not modify others information(UserId: " + id +
                    ", Current User Id: " + userId + ")", HttpStatus.UNAUTHORIZED);
        }

        User user = findUser(id);
        user.changeWith(mapper.map(userUpdateRequest, User.class));

        return user;
    }

    private User findUser(Long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new CustomException("[ERROR] User not found(Id: " + id + ")", HttpStatus.NOT_FOUND));
    }

    public User deleteUser(Long id, Long userId) {
        if (!Objects.equals(id, userId)) {
            throw new CustomException("[ERROR] Can not delete other user(UserId: " + id +
                    ", Current User Id: " + userId + ")", HttpStatus.UNAUTHORIZED);
        }

        User user = findUser(id);
        user.changeStatus(true);

        return user;
    }

    @Transactional
    public User increaseReviewCount(Long userId) {
        User user = findUser(userId);

        user.increaseReviewCount();

        return user;
    }
}
