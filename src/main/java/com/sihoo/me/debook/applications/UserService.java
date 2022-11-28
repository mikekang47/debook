package com.sihoo.me.debook.applications;

import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.UserRequestData;
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

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User createUser(UserRequestData userRequestData) {
        User user = userRequestData.toEntity();
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new CustomException("[ERROR] User not found(Id: " + id + ")", HttpStatus.NOT_FOUND));
    }

    public List<User> getUserByNickName(String nickName) {
        return userRepository.findUserByNickName(nickName);
    }
}
