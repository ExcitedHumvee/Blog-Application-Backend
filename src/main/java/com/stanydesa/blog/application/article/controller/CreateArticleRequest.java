package com.stanydesa.blog.application.article.controller;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.stanydesa.blog.domain.article.Tag;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@JsonRootName("article")
public record CreateArticleRequest(String title, String description, String body, List<String> tagList) {
    public Set<Tag> tags() {
        return tagList.stream().map(Tag::new).collect(toSet());
    }
}
