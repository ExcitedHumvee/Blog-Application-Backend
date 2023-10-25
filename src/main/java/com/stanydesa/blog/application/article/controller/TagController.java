package com.stanydesa.blog.application.article.controller;

import com.stanydesa.blog.application.article.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping("/api/tags")
    public ListOfTagsRecord getTags() {
        List<String> tags = tagService.getTags();
        return new ListOfTagsRecord(tags);
    }
}
