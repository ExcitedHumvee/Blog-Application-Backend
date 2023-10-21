package com.stanydesa.blog.domain.article;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleTagId implements Serializable {
    private Integer articleId;
    private Integer tagId;
}
