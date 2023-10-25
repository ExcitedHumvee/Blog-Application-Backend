package com.stanydesa.blog.domain.article;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Set<Comment> findByArticleOrderByCreatedAtDesc(Article article);
}
