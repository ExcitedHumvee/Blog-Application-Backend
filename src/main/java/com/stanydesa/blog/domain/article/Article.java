package com.stanydesa.blog.domain.article;

import com.stanydesa.blog.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {//TODO analyze the mapping between Tables
    @Id
    @Column(name = "article_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(length = 50, nullable = false)
    private String description;

    @Column(length = 50, unique = true, nullable = false)
    private String title;

    @Column(length = 50, unique = true, nullable = false)
    private String slug;

    @Column(length = 1_000, nullable = false)
    private String content = "";

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArticleFavorite> favoriteUsers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArticleTag> includeTags = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    @Builder
    private Article(Integer id, User author, String description, String title, String content) {
        this.id = id;
        this.author = author;
        this.description = description;
        this.title = title;
        this.slug = createSlugBy(title);
        this.content = content;
        this.favoriteUsers = new HashSet<>();
        this.includeTags = new HashSet<>();
        this.createdAt = LocalDateTime.now();
    }

    public List<Tag> getTags() {
        return this.includeTags.stream().map(ArticleTag::getTag).toList();
    }

    public String[] getTagNames() {
        return this.getTags().stream().map(Tag::getName).sorted().toArray(String[]::new);
    }

    public int numberOfLikes() {
        return this.favoriteUsers.size();
    }

    public void updateTitle(String title) {
        if (title == null || title.isBlank()) {
            return;
        }

        this.title = title;
        this.slug = createSlugBy(title);
    }

    public void updateDescription(String description) {
        if (description == null || description.isBlank()) {
            return;
        }

        this.description = description;
    }

    public void updateContent(String content) {
        if (content == null || content.isBlank()) {
            return;
        }

        this.content = content;
    }

    private String createSlugBy(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be null or blank");
        }

        return title.toLowerCase().replaceAll("\\s+", "-");//replacing all spaces with -
    }

    public boolean isNotWritten(User user) {
        return !this.author.equals(user);
    }

    public void addTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("tag must not be null");
        }

        ArticleTag articleTag = new ArticleTag(this, tag);

        if (this.includeTags.stream().anyMatch(articleTag::equals)) {
            return;
        }

        this.includeTags.add(articleTag);
    }
}
