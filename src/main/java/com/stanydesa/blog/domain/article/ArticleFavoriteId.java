package com.stanydesa.blog.domain.article;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleFavoriteId implements Serializable {
    private UUID userId;
    private Integer articleId;
}
