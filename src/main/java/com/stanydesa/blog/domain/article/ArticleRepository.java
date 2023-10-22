package com.stanydesa.blog.domain.article;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    boolean existsByTitle(String title);

    Optional<Article> findBySlug(String slug);
}
