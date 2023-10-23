package com.stanydesa.blog.application.article.controller;

import com.stanydesa.blog.application.article.service.ArticleService;
import com.stanydesa.blog.domain.article.ArticleVO;
import com.stanydesa.blog.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    @PostMapping("/api/articles")
    public SingleArticleRecord createArticle(User me, @RequestBody CreateArticleRequest request) {
        ArticleVO article = articleService.createArticle(me, request);
        return new SingleArticleRecord(article);
    }

    @PutMapping("/api/articles/{slug}")
    public SingleArticleRecord updateArticle(
            User me, @PathVariable String slug, @RequestBody UpdateArticleRequest request) {
        //you cannot change taglist
        ArticleVO article = articleService.updateArticle(me, slug, request);
        return new SingleArticleRecord(article);
    }

    @DeleteMapping("/api/articles/{slug}")
    public void deleteArticle(User me, @PathVariable String slug) {
        articleService.deleteArticle(me, slug);
    }

    @GetMapping("/api/articles/{slug}")
    public SingleArticleRecord getSingleArticle(User me, @PathVariable String slug) {
        ArticleVO article = articleService.getSingleArticle(me, slug);
        return new SingleArticleRecord(article);
    }
}
