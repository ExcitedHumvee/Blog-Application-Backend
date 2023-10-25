package com.stanydesa.blog.application.article.service;

import com.stanydesa.blog.application.article.controller.CreateArticleRequest;
import com.stanydesa.blog.application.article.controller.CreateCommentRequest;
import com.stanydesa.blog.application.article.controller.UpdateArticleRequest;
import com.stanydesa.blog.domain.article.*;
import com.stanydesa.blog.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ArticleVO createArticle(User me, CreateArticleRequest request) {
        if (articleRepository.existsByTitle(request.title())) {
            throw new IllegalArgumentException("Title `%s` is already in use.".formatted(request.title()));
        }

        Article article = Article.builder()
                .author(me)
                .title(request.title())
                .description(request.description())
                .content(request.body())
                .build();

        for (Tag invalidTag : request.tags()) {
            Optional<Tag> optionalTag = tagRepository.findByName(invalidTag.getName());
            Tag validTag = optionalTag.orElseGet(() -> tagRepository.save(invalidTag));
            validTag.addTo(article);
        }

        article = articleRepository.save(article);
        return new ArticleVO(me, article);
    }

    @Transactional
    public ArticleVO updateArticle(User me, String slug, UpdateArticleRequest request) {
        Article article = findBySlug(slug);

        if (article.isNotWritten(me)) {
            throw new IllegalArgumentException("You can't edit articles written by others.");
        }

        article.updateTitle(request.title());
        article.updateDescription(request.description());
        article.updateContent(request.body());

        return new ArticleVO(me, article);
    }

    private Article findBySlug(String slug) {
        return articleRepository
                .findBySlug(slug)
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));
    }

    @Transactional
    public void deleteArticle(User me, String slug) {
        Article article = findBySlug(slug);

        if (article.isNotWritten(me)) {
            throw new IllegalArgumentException("You can't delete articles written by others.");
        }

        articleRepository.delete(article);
    }

    @Transactional(readOnly = true)
    public ArticleVO getSingleArticle(User me, String slug) {
        Article article = findBySlug(slug);
        return new ArticleVO(me, article);
    }

    @Transactional(readOnly = true)
    public List<ArticleVO> getArticles(User me, ArticleFacets facets) {
        String tag = facets.tag();
        String author = facets.author();
        String favorited = facets.favorited();
        Pageable pageable = facets.getPageable();

        Page<Article> byFacets = articleRepository.findByFacets(tag, author, favorited, pageable);
//        List<ArticleVO> articleVOList = new ArrayList<>();
//        List<Article> articles = byFacets.getContent();
//        for (Article article : articles) {
//            articleVOList.add(new ArticleVO(me, article));
//        }
//        return articleVOList;
        return byFacets.getContent().stream()
                .map(article -> new ArticleVO(me, article))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArticleVO> getFeedArticles(User me, ArticleFacets facets) {
        List<User> followings = me.followUsers();
        Pageable pageable = facets.getPageable();

        return articleRepository
                .findByAuthorInOrderByCreatedAtDesc(followings, pageable)
                .map(article -> new ArticleVO(me, article))
                .getContent();
    }

    @Transactional
    public ArticleVO favoriteArticle(User me, String slug) {
        Article article = findBySlug(slug);
        return me.favorite(article);
    }

    @Transactional
    public ArticleVO unfavoriteArticle(User me, String slug) {
        Article article = findBySlug(slug);
        return me.unfavorite(article);
    }

    @Transactional
    public CommentVO createComment(User me, String slug, CreateCommentRequest request) {
        Comment comment = Comment.builder()
                .article(findBySlug(slug))
                .author(me)
                .content(request.body())
                .build();

        commentRepository.save(comment);

        return CommentVO.myComment(comment);
    }
}
