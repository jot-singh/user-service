package com.user.service.dto.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for paginated responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponseDto<T> {
    
    @NotNull(message = "Content list is required")
    private List<T> content;
    
    @NotNull(message = "Page information is required")
    private PageInfo pageInfo;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        
        @Min(value = 0, message = "Page number cannot be negative")
        private int pageNumber;
        
        @Min(value = 1, message = "Page size must be at least 1")
        private int pageSize;
        
        @Min(value = 0, message = "Total elements cannot be negative")
        private long totalElements;
        
        @Min(value = 0, message = "Total pages cannot be negative")
        private int totalPages;
        
        private boolean hasNext;
        private boolean hasPrevious;
        private boolean isFirst;
        private boolean isLast;
    }
}
