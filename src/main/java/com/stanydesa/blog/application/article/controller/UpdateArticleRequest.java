package com.stanydesa.blog.application.article.controller;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("article")
public record UpdateArticleRequest(String title, String description, String body) {
    public UpdateArticleRequest {
        if (title == null) title = "";
        if (description == null) description = "";
        if (body == null) body = "";
    }
}
