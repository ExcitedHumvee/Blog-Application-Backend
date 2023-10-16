package com.stanydesa.blog.domain.user;

public record UserVO(String email, String token, String username, String bio, String image) {
    public UserVO(User user) {
        //User View Object
        this(user.getEmail(), user.getToken(), user.getUsername(), user.getBio(), user.getImage());
    }
}

