package ru.effective_mobile.short_url.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.effective_mobile.short_url.dto.ShortenUrlRequest;
import ru.effective_mobile.short_url.exceptions.AliasNotFoundException;
import ru.effective_mobile.short_url.services.ShortUrlService;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    @PostMapping("/api/v1/shorten")
    public ResponseEntity<String> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        log.info("Received shorten URL request for original URL: {}", request.getOriginalUrl());
        String alias = shortUrlService.generateAlias(request);
        log.info("URL shortened successfully. Alias: {}", alias);
        return ResponseEntity.status(HttpStatus.CREATED).body(alias);
    }

    @GetMapping("/{alias}")
    public void redirectToOriginalUrl(@PathVariable String alias, HttpServletResponse response) throws IOException {
        log.info("Received redirect request for alias: {}", alias);
        String originalUrl = shortUrlService.getFullUrlByAlias(alias);
        log.info("Redirecting alias {} to original URL: {}", alias, originalUrl);
        response.sendRedirect(originalUrl);
    }
}
