package com.stanydesa.blog.application.article.controller;

import com.stanydesa.blog.application.article.service.ArticleService;
import com.stanydesa.blog.domain.article.ArticleFacets;
import com.stanydesa.blog.domain.article.ArticleVO;
import com.stanydesa.blog.domain.article.CommentVO;
import com.stanydesa.blog.domain.article.SingleCommentRecord;
import com.stanydesa.blog.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/api/articles")
    public MultipleArticlesResponse getArticles(
            User me,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "favorited", required = false) String favorited,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit) {
        //pagination support
        ArticleFacets facets = new ArticleFacets(tag, author, favorited, offset, limit); //filtering and pagination parameters related to querying articles
        List<ArticleVO> articles = articleService.getArticles(me, facets);
        return new MultipleArticlesResponse(articles);
    }

    @PreAuthorize("isAuthenticated()")//TODO why this here, without this, anonymous user is getting through
    @GetMapping("/api/articles/feed")
    public MultipleArticlesResponse getFeedArticles(
            User me,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit) {
        ArticleFacets facets = new ArticleFacets(null, null, null, offset, limit);
        List<ArticleVO> articles = articleService.getFeedArticles(me, facets);
        return new MultipleArticlesResponse(articles);
    }

    @PostMapping("/api/articles/{slug}/favorite")
    public SingleArticleRecord favoriteArticle(User me, @PathVariable String slug) {
        ArticleVO article = articleService.favoriteArticle(me, slug);
        return new SingleArticleRecord(article);
    }

    @DeleteMapping("/api/articles/{slug}/favorite")
    public SingleArticleRecord unfavoriteArticle(User me, @PathVariable String slug) {
        ArticleVO article = articleService.unfavoriteArticle(me, slug);
        return new SingleArticleRecord(article);
    }

    @PostMapping("/api/articles/{slug}/comments")
    public SingleCommentRecord createComment(
            User me, @PathVariable String slug, @RequestBody CreateCommentRequest request) {
        CommentVO comment = articleService.createComment(me, slug, request);
        return new SingleCommentRecord(comment);
    }
}
