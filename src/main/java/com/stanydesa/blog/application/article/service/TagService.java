package com.stanydesa.blog.application.article.service;

import com.stanydesa.blog.domain.article.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.stanydesa.blog.domain.article.Tag;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<String> getTags() {
        return tagRepository.findAll().stream().map(Tag::getName).toList();
    }
}
