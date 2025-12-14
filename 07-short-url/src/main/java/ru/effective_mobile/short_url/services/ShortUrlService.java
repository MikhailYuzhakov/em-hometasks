package ru.effective_mobile.short_url.services;

import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.effective_mobile.short_url.dto.ShortenUrlRequest;
import ru.effective_mobile.short_url.entity.ShortenedUrl;
import ru.effective_mobile.short_url.exceptions.AliasNotFoundException;
import ru.effective_mobile.short_url.repository.ShortenedUrlRepository;
import ru.effective_mobile.short_url.exceptions.AliasAlreadyExistsException;
import ru.effective_mobile.short_url.utils.Base62Util;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private static final int GENERATED_ALIAS_LENGTH = 8;

    private final ShortenedUrlRepository repository;

    @Value("${shorturl.cleanup.interval:1800000}")
    private long cleanupInterval;

    public String getFullUrlByAlias(String url) {
        log.debug("Attempting to retrieve full URL for alias: {}", url);
        ShortenedUrl shortenedUrl = repository.findByAlias(url)
            .orElseThrow(() -> {
                log.warn("Alias not found: {}", url);
                return new AliasNotFoundException(url + " not found.");
            });

        if (shortenedUrl.getExpiresAt() != null && shortenedUrl.getExpiresAt().isBefore(OffsetDateTime.now())) {
            log.warn("Alias {} has expired. Expiration date: {}", url, shortenedUrl.getExpiresAt());
            throw new AliasNotFoundException(url + " has expired.");
        }
        log.debug("Successfully retrieved original URL for alias {}: {}", url, shortenedUrl.getOriginalUrl());
        return shortenedUrl.getOriginalUrl();
    }

    public String generateAlias(ShortenUrlRequest request) {
        log.debug("Attempting to generate alias for original URL: {}", request.getOriginalUrl());
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        String providedAlias = request.getAlias();
        String finalAlias;

        if (providedAlias == null || providedAlias.isBlank()) {
            log.info("No custom alias provided. Generating unique alias.");
            String generatedAlias;
            do {
                generatedAlias = Base62Util.generateUniqueBase62(GENERATED_ALIAS_LENGTH);
                log.trace("Generated candidate alias: {}", generatedAlias);
            } while (repository.findByAlias(generatedAlias).isPresent());
            finalAlias = generatedAlias;
            log.info("Generated unique alias: {}", finalAlias);
        } else {
            log.info("Custom alias provided: {}", providedAlias);
            if (repository.findByAlias(providedAlias).isPresent()) {
                log.warn("Custom alias {} already exists.", providedAlias);
                throw new AliasAlreadyExistsException("Alias '" + providedAlias + "' already exists.");
            }
            finalAlias = providedAlias;
        }
        
        shortenedUrl.setAlias(finalAlias);
        shortenedUrl.setOriginalUrl(request.getOriginalUrl());
        shortenedUrl.setExpiresAt(request.getExpiresAt());
        repository.save(shortenedUrl);
        log.info("Shortened URL created: original={}, alias={}, expiresAt={}",
                 request.getOriginalUrl(), finalAlias, request.getExpiresAt());
        return shortenedUrl.getAlias();
    }
    
    @Scheduled(fixedRateString = "${shorturl.cleanup.interval}")
    public void cleanupExpiredUrls() {
        log.info("Starting cleanup of expired URLs.");
        repository.deleteByExpiresAtBefore(OffsetDateTime.now());
        log.info("Cleanup of expired URLs finished.");
    }
}
