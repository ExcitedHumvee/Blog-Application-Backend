package com.stanydesa.blog.domain.user;

public record ProfileVO (String username, String bio, String image, boolean following){
    public ProfileVO(User from, User to) {
        this(to.getUsername(), to.getBio(), to.getImage(), from != null && from.isAlreadyFollowing(to));
    }
}
