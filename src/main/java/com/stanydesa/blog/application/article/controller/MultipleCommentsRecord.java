package com.stanydesa.blog.application.article.controller;

import com.stanydesa.blog.domain.article.CommentVO;

import java.util.List;

public record MultipleCommentsRecord(CommentVO[] comments) {
    public MultipleCommentsRecord(List<CommentVO> comments) {
        this(comments.toArray(CommentVO[]::new));
    }
}
