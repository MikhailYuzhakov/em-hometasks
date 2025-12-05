package ru.effective_mobile.short_url.services;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.effective_mobile.short_url.dto.ShortenUrlRequest;
import ru.effective_mobile.short_url.entity.ShortenedUrl;
import ru.effective_mobile.short_url.exceptions.AliasNotFoundException;
import ru.effective_mobile.short_url.repository.ShortenedUrlRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlService {
    private final ShortenedUrlRepository repository;

    public String getFullUrlByAlies(String url) {
        ShortenedUrl shortenedUrl = repository.findByAlias(url)
            .orElseThrow(() -> new AliasNotFoundException(url + " not found."));

        if (shortenedUrl.getExpiresAt() != null && shortenedUrl.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new AliasNotFoundException(url + " has expired.");
        }
        return shortenedUrl.getOriginalUrl();
    }

    public String generateAlias(ShortenUrlRequest request) {
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        String providedAlias = request.getAlias();
        String finalAlias;

        if (providedAlias == null || providedAlias.isBlank()) {
            String generatedAlias;
            do {
                generatedAlias = UUID.randomUUID().toString().substring(0, 8);
            } while (repository.findByAlias(generatedAlias).isPresent());
            finalAlias = generatedAlias;
        } else {
            finalAlias = providedAlias;
        }
        
        shortenedUrl.setAlias(finalAlias);
        shortenedUrl.setOriginalUrl(request.getOriginalUrl());
        shortenedUrl.setCreatedAt(OffsetDateTime.now());
        shortenedUrl.setExpiresAt(request.getExpiresAt());
        repository.save(shortenedUrl);
        return shortenedUrl.getAlias();
    }
    
    @Scheduled(fixedRate = 1800000) // каждые 30 минут (1800000 миллисекунд)
    public void cleanupExpiredUrls() {
        log.info("Starting cleanup of expired URLs.");
        repository.deleteByExpiresAtBefore(OffsetDateTime.now());
        log.info("Cleanup of expired URLs finished.");
    }
}
