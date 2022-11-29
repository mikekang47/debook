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


@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final Mapper mapper;

    public UserService(UserRepository userRepository, Mapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User createUser(UserRequestData userRequestData) {
        User user = userRequestData.toEntity();
        return userRepository.save(user);
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
    public User updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User user = findUser(id);
        user.changeWith(mapper.map(userUpdateRequest, User.class));

        return user;
    }

    private User findUser(Long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new CustomException("[ERROR] User not found(Id: " + id + ")", HttpStatus.NOT_FOUND));
    }

    public User deleteUser(Long id) {
        User user = findUser(id);
        user.changeStatus(true);

        return user;
    }
}
