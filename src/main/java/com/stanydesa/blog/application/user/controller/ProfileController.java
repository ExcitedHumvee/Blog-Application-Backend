package com.stanydesa.blog.application.user.controller;

import com.stanydesa.blog.application.user.service.ProfileService;
import com.stanydesa.blog.domain.user.ProfileVO;
import com.stanydesa.blog.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/api/profiles/{username}")
    public ProfileRecord getProfile(User me, @PathVariable("username") String target) {
        //Authentication not required, User can be anonymous
        ProfileVO profile = profileService.getProfile(me, target);
        return new ProfileRecord(profile);
    }
}
