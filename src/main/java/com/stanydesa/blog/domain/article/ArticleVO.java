package com.stanydesa.blog.domain.article;

import com.stanydesa.blog.domain.user.ProfileVO;
import com.stanydesa.blog.domain.user.User;

import java.time.LocalDateTime;

public record ArticleVO(
        String slug,
        String title,
        String description,
        String body,
        String[] tagList,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean favorited,
        int favoritesCount,
        ProfileVO author) {
    public ArticleVO(User me, Article article) {
        this(
                article.getSlug(),
                article.getTitle(),
                article.getDescription(),
                article.getContent(),
                article.getTagNames(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                me != null && me.isAlreadyFavorite(article),
                article.numberOfLikes(),
                new ProfileVO(me, article.getAuthor()));
    }
}
