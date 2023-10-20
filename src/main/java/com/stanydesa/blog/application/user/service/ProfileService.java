package com.stanydesa.blog.application.user.service;

import com.stanydesa.blog.domain.user.ProfileVO;
import com.stanydesa.blog.domain.user.User;
import com.stanydesa.blog.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileVO getProfile(User me, String targetUsername) {
        User targetUser = findUserByUsername(targetUsername);
        return new ProfileVO(me, targetUser);
    }

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)//returns Optional Class
                .orElseThrow(() -> new NoSuchElementException("User(`%s`) not found".formatted(username)));
    }
}
