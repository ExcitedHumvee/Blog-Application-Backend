package com.stanydesa.blog.application.user.service;

import com.stanydesa.blog.application.config.BearerTokenProvider;
import com.stanydesa.blog.application.user.controller.LoginRequest;
import com.stanydesa.blog.application.user.controller.SignUpRequest;
import com.stanydesa.blog.domain.user.User;
import com.stanydesa.blog.domain.user.UserRepository;
import com.stanydesa.blog.domain.user.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BearerTokenProvider bearerTokenProvider;

    @Transactional
    public User signUp(SignUpRequest request) {//2
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email `%s` is already exists.".formatted(request.email()));
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username `%s` is already exists.".formatted(request.username()));
        }

        User newUser = this.createNewUser(request);
        return userRepository.save(newUser);
    }

    private User createNewUser(SignUpRequest request) {//3
        return User.builder()
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build();
    }

    @Transactional(readOnly = true)//If something fails within a transaction, all will be rolled back. Transaction means all data will be committed or rolled back
    public UserVO login(LoginRequest request) {//5
        return userRepository
                .findByEmail(request.email())
                //check if the user with the given email exists and if the provided password matches the stored password
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                //token generation
                .map(user -> {
                    String token = bearerTokenProvider.createBearerToken(user);
                    return new UserVO(user.setToken(token));
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
    }
}
