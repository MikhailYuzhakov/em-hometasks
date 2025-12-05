package ru.effective_mobile.short_url.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.effective_mobile.short_url.dto.ShortenUrlRequest;
import ru.effective_mobile.short_url.entity.ShortenedUrl;
import ru.effective_mobile.short_url.exceptions.AliasNotFoundException;
import ru.effective_mobile.short_url.repository.ShortenedUrlRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @Mock
    private ShortenedUrlRepository repository;

    @InjectMocks
    private ShortUrlService shortUrlService;

    private ShortenUrlRequest request;
    private ShortenedUrl shortenedUrl;

    @BeforeEach
    void setUp() {
        request = new ShortenUrlRequest();
        request.setOriginalUrl("http://example.com");

        shortenedUrl = new ShortenedUrl();
        shortenedUrl.setId(1L);
        shortenedUrl.setOriginalUrl("http://example.com");
        shortenedUrl.setAlias("testalias");
        shortenedUrl.setCreatedAt(OffsetDateTime.now());
    }

    @Test
    void generateAlias_withCustomAlias_returnsCustomAlias() {
        request.setAlias("mycustomlink");
        when(repository.save(any(ShortenedUrl.class))).thenReturn(shortenedUrl);

        String result = shortUrlService.generateAlias(request);

        assertEquals("mycustomlink", result);
        verify(repository, times(1)).save(any(ShortenedUrl.class));
    }

    @Test
    void generateAlias_withoutCustomAlias_returnsGeneratedAlias() {
        request.setAlias(null);
        when(repository.findByAlias(anyString()))
                .thenReturn(Optional.empty()) // for the first generated alias
                .thenReturn(Optional.of(new ShortenedUrl())); // to ensure loop breaks if alias is taken
        when(repository.save(any(ShortenedUrl.class))).thenReturn(shortenedUrl);

        String result = shortUrlService.generateAlias(request);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(repository, atLeastOnce()).findByAlias(anyString());
        verify(repository, times(1)).save(any(ShortenedUrl.class));
    }

    @Test
    void getFullUrlByAlias_existingAlias_returnsOriginalUrl() {
        when(repository.findByAlias("testalias")).thenReturn(Optional.of(shortenedUrl));

        String result = shortUrlService.getFullUrlByAlies("testalias");

        assertEquals("http://example.com", result);
    }

    @Test
    void getFullUrlByAlias_expiredAlias_throwsAliasNotFoundException() {
        shortenedUrl.setExpiresAt(OffsetDateTime.now().minusDays(1)); // Set expired date
        when(repository.findByAlias("testalias")).thenReturn(Optional.of(shortenedUrl));

        assertThrows(AliasNotFoundException.class, () -> shortUrlService.getFullUrlByAlies("testalias"));
    }

    @Test
    void getFullUrlByAlias_nonExistingAlias_throwsAliasNotFoundException() {
        when(repository.findByAlias("nonexistent")).thenReturn(Optional.empty());

        assertThrows(AliasNotFoundException.class, () -> shortUrlService.getFullUrlByAlies("nonexistent"));
    }

    @Test
    void cleanupExpiredUrls_deletesExpiredUrls() {
        OffsetDateTime now = OffsetDateTime.now();
        shortUrlService.cleanupExpiredUrls();
        verify(repository, times(1)).deleteByExpiresAtBefore(any(OffsetDateTime.class));
    }
}
