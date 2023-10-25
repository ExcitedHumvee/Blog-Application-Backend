package com.stanydesa.blog.application.article.controller;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("comment")
public record CreateCommentRequest(String body) {}
