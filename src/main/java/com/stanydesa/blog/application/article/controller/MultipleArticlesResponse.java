package com.stanydesa.blog.application.article.controller;

import com.stanydesa.blog.domain.article.ArticleVO;

import java.util.List;

public record MultipleArticlesResponse(ArticleVO[] articles, int articlesCount) {
    public MultipleArticlesResponse(List<ArticleVO> articles) {
        this(articles.toArray(ArticleVO[]::new), articles.size());
    }
}
