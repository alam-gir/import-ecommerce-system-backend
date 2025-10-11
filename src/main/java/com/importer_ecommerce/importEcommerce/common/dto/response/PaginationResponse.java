package com.importer_ecommerce.importEcommerce.common.dto.response;

/**
 * Pagination information for paginated responses
 */
public record PaginationResponse(
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {
    
    /**
     * Create pagination response from Spring Data Page
     */
    public static PaginationResponse from(org.springframework.data.domain.Page<?> page) {
        return new PaginationResponse(
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext(),
            page.hasPrevious()
        );
    }
    
    /**
     * Create pagination response with custom values
     */
    public static PaginationResponse of(int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PaginationResponse(
            page,
            size,
            totalElements,
            totalPages,
            page < totalPages - 1,
            page > 0
        );
    }
}

