package com.stanydesa.blog.domain.article;

import com.stanydesa.blog.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Builder
@Table(name = "article_favorite")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleFavorite {
    @EmbeddedId
    private ArticleFavoriteId id;

    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    @CreatedDate
    @Builder.Default
    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ArticleFavorite(User user, Article article) {
        this.id = new ArticleFavoriteId(user.getId(), article.getId());
        this.user = user;
        this.article = article;
    }

    @Override
    public boolean equals(Object o) {
//        // Check if the compared object is the same instance as this one
//        if (this == o) {
//            return true;
//        }
//
//        // Check if the compared object is null or of a different class
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//
//        // Cast the compared object to ArticleFavorite
//        ArticleFavorite other = (ArticleFavorite) o;
//
//        // Compare the individual fields for equality
//        return Objects.equals(this.id, other.id) &&
//                Objects.equals(this.user, other.user) &&
//                Objects.equals(this.article, other.article);

        return o instanceof ArticleFavorite other// new Java 16, automatically other=o
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.user, other.user)
                && Objects.equals(this.article, other.article);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.user, this.article);
    }
}
