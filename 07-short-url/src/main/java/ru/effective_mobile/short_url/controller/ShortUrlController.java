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
        String alias = shortUrlService.generateAlias(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(alias);
    }

    @GetMapping("/{alias}")
    public void redirectToOriginalUrl(@PathVariable String alias, HttpServletResponse response) throws IOException {
        try {
            String originalUrl = shortUrlService.getFullUrlByAlies(alias);
            response.sendRedirect(originalUrl);
        } catch (AliasNotFoundException e) {
            log.warn("Alias not found or expired: {}", alias);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
}
