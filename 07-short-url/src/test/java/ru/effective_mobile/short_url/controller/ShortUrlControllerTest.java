package ru.effective_mobile.short_url.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.effective_mobile.short_url.dto.ShortenUrlRequest;
import ru.effective_mobile.short_url.exceptions.AliasAlreadyExistsException;
import ru.effective_mobile.short_url.exceptions.AliasNotFoundException;
import ru.effective_mobile.short_url.services.ShortUrlService;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ShortUrlController.class, excludeAutoConfiguration = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
class ShortUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShortUrlService shortUrlService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    private ShortenUrlRequest request;

    @BeforeEach
    void setUp() {
        request = new ShortenUrlRequest();
        request.setOriginalUrl("http://example.com");
        request.setExpiresAt(OffsetDateTime.now().plusHours(1)); // Исправлено на plusHours(1)
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shortenUrl_withCustomAlias_returnsCreatedStatusAndAlias() throws Exception {
        request.setAlias("customalias");
        when(shortUrlService.generateAlias(any(ShortenUrlRequest.class))).thenReturn("customalias");

        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("customalias"));
    }

    @Test
    void shortenUrl_withoutCustomAlias_returnsCreatedStatusAndGeneratedAlias() throws Exception {
        request.setAlias(null); // Исправлено на null
        when(shortUrlService.generateAlias(any(ShortenUrlRequest.class))).thenReturn("generatedalias");

        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("generatedalias"));
    }

    @Test
    void shortenUrl_invalidRequest_returnsBadRequest() throws Exception {
        request.setOriginalUrl(""); // Invalid URL

        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shortenUrl_withExistingCustomAlias_returnsConflictStatus() throws Exception {
        request.setAlias("existingalias");
        when(shortUrlService.generateAlias(any(ShortenUrlRequest.class)))
                .thenThrow(new AliasAlreadyExistsException("Alias 'existingalias' already exists."));

        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void redirectToOriginalUrl_existingAlias_redirects() throws Exception {
        when(shortUrlService.getFullUrlByAlias("testalias")).thenReturn("http://example.com");

        mockMvc.perform(get("/testalias"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://example.com"));
    }

    @Test
    void redirectToOriginalUrl_nonExistingAlias_returnsNotFound() throws Exception {
        when(shortUrlService.getFullUrlByAlias("nonexistent")).thenThrow(new AliasNotFoundException("nonexistent not found."));

        mockMvc.perform(get("/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void redirectToOriginalUrl_expiredAlias_returnsNotFound() throws Exception {
        when(shortUrlService.getFullUrlByAlias("expiredalias")).thenThrow(new AliasNotFoundException("expiredalias has expired."));

        mockMvc.perform(get("/expiredalias"))
                .andExpect(status().isNotFound());
    }
}
