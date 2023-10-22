package com.stanydesa.blog.application.article.controller;

import com.stanydesa.blog.application.article.service.ArticleService;
import com.stanydesa.blog.domain.article.ArticleVO;
import com.stanydesa.blog.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    @PostMapping("/api/articles")
    public SingleArticleRecord createArticle(User me, @RequestBody CreateArticleRequest request) {
        ArticleVO article = articleService.createArticle(me, request);
        return new SingleArticleRecord(article);
    }
}
