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
    public User signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email `%s` is already exists.".formatted(request.email()));
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username `%s` is already exists.".formatted(request.username()));
        }

        User newUser = this.createNewUser(request);
        return userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public UserVO login(LoginRequest request) {
        return userRepository
                .findByEmail(request.email())
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .map(user -> {
                    String token = bearerTokenProvider.createBearerToken(user);
                    return new UserVO(user.setToken(token));
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
    }

    private User createNewUser(SignUpRequest request) {
        return User.builder()
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build();
    }
}
