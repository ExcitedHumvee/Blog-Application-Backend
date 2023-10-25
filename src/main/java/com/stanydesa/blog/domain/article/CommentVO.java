package com.stanydesa.blog.domain.article;

import com.stanydesa.blog.domain.user.ProfileVO;

import java.time.LocalDateTime;

public record CommentVO(int id, LocalDateTime createdAt, LocalDateTime updatedAt, String body, ProfileVO author) {
    public static CommentVO myComment(Comment comment) {
        return new CommentVO(
                comment.getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getContent(),
                ProfileVO.unfollowing(comment.getAuthor()));
    }

    public static CommentVO following(Comment comment) {
        return new CommentVO(
                comment.getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getContent(),
                ProfileVO.following(comment.getAuthor()));
    }

    public static CommentVO unfollowing(Comment comment) {
        return new CommentVO(
                comment.getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getContent(),
                ProfileVO.unfollowing(comment.getAuthor()));
    }
}
