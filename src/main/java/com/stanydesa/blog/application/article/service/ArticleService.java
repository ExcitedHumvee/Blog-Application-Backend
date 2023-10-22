package com.stanydesa.blog.application.article.service;

import com.stanydesa.blog.application.article.controller.CreateArticleRequest;
import com.stanydesa.blog.domain.article.*;
import com.stanydesa.blog.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
