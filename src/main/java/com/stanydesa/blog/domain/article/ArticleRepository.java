package com.stanydesa.blog.domain.article;

import com.stanydesa.blog.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    boolean existsByTitle(String title);

    Optional<Article> findBySlug(String slug);

    @Query(//Java Persistence Query Language
            """
                    SELECT a FROM Article a
                    WHERE (:tag IS NULL OR :tag IN (SELECT t.tag.name FROM a.includeTags t))
                    AND (:author IS NULL OR a.author.username = :author)
                    AND (:favorited IS NULL OR :favorited IN (SELECT fu.user.username FROM a.favoriteUsers fu))
                    ORDER BY a.createdAt DESC
                    """)
    Page<Article> findByFacets(
            @Param("tag") String tag,
            @Param("author") String author,
            @Param("favorited") String favorited,
            Pageable pageable);

    Page<Article> findByAuthorInOrderByCreatedAtDesc(Collection<User> authors, Pageable pageable);//TODO find sql equivalent
}
