package ru.effective_mobile.short_url.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.effective_mobile.short_url.entity.ShortenedUrl;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface ShortenedUrlRepository extends JpaRepository<ShortenedUrl, Long> {
    Optional<ShortenedUrl> findByAlias(String alias);
    void deleteByExpiresAtBefore(OffsetDateTime dateTime);
}
