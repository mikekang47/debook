package com.sihoo.me.debook.applications;

import com.github.dozermapper.core.Mapper;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.UserRequestData;
import com.sihoo.me.debook.dto.UserUpdateRequest;
import com.sihoo.me.debook.errors.CustomException;
import com.sihoo.me.debook.infra.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;

    public UserService(RoleService roleService, UserRepository userRepository, Mapper mapper, PasswordEncoder passwordEncoder) {
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User createUser(UserRequestData userRequestData) {
        User user = userRepository.save(userRequestData.toEntity());

        user.changePassword(user.getPassword(), passwordEncoder);
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
        authorize(id, userId);

        User user = findUser(id);
        User source = mapper.map(userUpdateRequest, User.class);
        user.changeWith(source, passwordEncoder);

        return user;
    }

    public User deleteUser(Long id, Long userId) {
        authorize(id, userId);

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

    @Transactional
    public User decreaseReviewCount(Long userId) {
        User user = findUser(userId);

        user.decreaseReviewCount();

        return user;
    }

    private User findUser(Long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new CustomException("[ERROR] User not found(Id: " + id + ")", HttpStatus.NOT_FOUND));
    }

    private static void authorize(Long id, Long userId) {
        if (!Objects.equals(id, userId)) {
            throw new CustomException("[ERROR] No authorization for user(UserId: " + id +
                    ", Current User Id: " + userId + ")", HttpStatus.UNAUTHORIZED);
        }
    }

    public User increaseReplyCount(Long userId) {
        User user = findUser(userId);
        user.increaseReviewCount();

        return user;
    }
}
