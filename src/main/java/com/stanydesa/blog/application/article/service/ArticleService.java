package com.stanydesa.blog.application.article.service;

import com.stanydesa.blog.application.article.controller.CreateArticleRequest;
import com.stanydesa.blog.application.article.controller.UpdateArticleRequest;
import com.stanydesa.blog.domain.article.*;
import com.stanydesa.blog.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;

    @Transactional
    public ArticleVO createArticle(User me, CreateArticleRequest request) {
        if (articleRepository.existsByTitle(request.title())) {
            throw new IllegalArgumentException("Title `%s` is already in use.".formatted(request.title()));
        }

        Article article = Article.builder()
                .author(me)
                .title(request.title())
                .description(request.description())
                .content(request.body())
                .build();

        for (Tag invalidTag : request.tags()) {
            Optional<Tag> optionalTag = tagRepository.findByName(invalidTag.getName());
            Tag validTag = optionalTag.orElseGet(() -> tagRepository.save(invalidTag));
            validTag.addTo(article);
        }

        article = articleRepository.save(article);
        return new ArticleVO(me, article);
    }

    @Transactional
    public ArticleVO updateArticle(User me, String slug, UpdateArticleRequest request) {
        Article article = findBySlug(slug);

        if (article.isNotWritten(me)) {
            throw new IllegalArgumentException("You can't edit articles written by others.");
        }

        article.updateTitle(request.title());
        article.updateDescription(request.description());
        article.updateContent(request.body());

        return new ArticleVO(me, article);
    }

    private Article findBySlug(String slug) {
        return articleRepository
                .findBySlug(slug)
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));
    }

    @Transactional
    public void deleteArticle(User me, String slug) {
        Article article = findBySlug(slug);

        if (article.isNotWritten(me)) {
            throw new IllegalArgumentException("You can't delete articles written by others.");
        }

        articleRepository.delete(article);
    }

    @Transactional(readOnly = true)
    public ArticleVO getSingleArticle(User me, String slug) {
        Article article = findBySlug(slug);
        return new ArticleVO(me, article);
    }
}
