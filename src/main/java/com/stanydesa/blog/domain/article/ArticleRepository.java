package com.stanydesa.blog.domain.article;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    boolean existsByTitle(String title);
}
