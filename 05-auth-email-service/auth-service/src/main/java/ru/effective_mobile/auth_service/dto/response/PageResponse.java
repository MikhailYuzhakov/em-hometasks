package ru.effective_mobile.auth_service.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Класс-обертка для реализации пагинации.
 * @param <T> список объектов для пагинации.
 */
@Getter
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;

    public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;

        this.totalPages = (pageSize > 0) ? (int) Math.ceil((double) totalElements / pageSize) : 0;
        this.isFirst = (pageNumber == 0);
        this.isLast = (pageNumber >= totalPages - 1);
    }
}
