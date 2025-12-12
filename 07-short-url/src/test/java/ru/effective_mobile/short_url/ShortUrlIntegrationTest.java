package ru.effective_mobile.short_url;

import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach; // Добавлено
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.effective_mobile.short_url.dto.ShortenUrlRequest;
import ru.effective_mobile.short_url.repository.ShortenedUrlRepository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShortUrlIntegrationTest {

    @LocalServerPort
    private int port;

    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // Flyway настройки обычно подтягиваются автоматически, но можно оставить явно
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration"); // убрал слэш после classpath
    }

    private String createUrl(String uri) {
        return "http://localhost:" + port + uri;
    }

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ShortenedUrlRepository repository;

    @PostConstruct
    void configTest() {
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(@NotNull HttpURLConnection connection, @NotNull String httpMethod) throws IOException {
                super.prepareConnection(connection, httpMethod);
                connection.setInstanceFollowRedirects(false);
            }
        });

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(@NotNull ClientHttpResponse response) throws IOException {
                return false;
            }
        });
    }

    // ВАЖНО: Очищаем базу после каждого теста, чтобы избежать конфликтов (например, duplicate key)
    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertTrue(postgres.isRunning());
    }

    @Test
    void shortenUrlAndRedirect_success() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setOriginalUrl("http://test.com/long/url");
        request.setAlias("testalias");

        ResponseEntity<@NotNull String> shortenResponse = restTemplate.postForEntity(createUrl("/api/v1/shorten"), request, String.class);
        assertEquals(HttpStatus.CREATED, shortenResponse.getStatusCode());

        // Проверка редиректа
        ResponseEntity<@NotNull Void> redirectResponse = restTemplate.getForEntity(createUrl("/testalias"), Void.class);
        assertEquals(HttpStatus.FOUND, redirectResponse.getStatusCode());
        // Проверка на null перед вызовом toString()
        assertNotNull(redirectResponse.getHeaders().getLocation());
        assertEquals("http://test.com/long/url", redirectResponse.getHeaders().getLocation().toString());
    }

    @Test
    void shortenUrl_duplicateAlias_returnsConflict() {
        ShortenUrlRequest request1 = new ShortenUrlRequest();
        request1.setOriginalUrl("http://test.com/url1");
        request1.setAlias("duplicatealias");

        restTemplate.postForEntity(createUrl("/api/v1/shorten"), request1, String.class);

        ShortenUrlRequest request2 = new ShortenUrlRequest();
        request2.setOriginalUrl("http://test.com/url2");
        request2.setAlias("duplicatealias"); // Тот же алиас

        ResponseEntity<@NotNull String> shortenResponse = restTemplate.postForEntity(createUrl("/api/v1/shorten"), request2, String.class);
        assertEquals(HttpStatus.CONFLICT, shortenResponse.getStatusCode());
    }

    @Test
    void redirectToOriginalUrl_expiredAlias_returnsNotFound() throws InterruptedException {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setOriginalUrl("http://test.com/expired");
        request.setAlias("expiredlink");

        // Ставим маленькое время жизни (например, 1 сек),
        request.setExpiresAt(OffsetDateTime.now().plusSeconds(1));

        ResponseEntity<@NotNull String> createResponse = restTemplate.postForEntity(createUrl("/api/v1/shorten"), request, String.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // Ждем, пока ссылка протухнет (1 сек + запас)
        Thread.sleep(1500);

        ResponseEntity<@NotNull Void> redirectResponse = restTemplate.getForEntity(createUrl("/expiredlink"), Void.class);
        assertEquals(HttpStatus.NOT_FOUND, redirectResponse.getStatusCode());
    }
}