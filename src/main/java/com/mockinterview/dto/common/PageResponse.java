package com.mockinterview.dto.common;

import lombok.*;

import java.util.List;
// Paginated Response
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> content;
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Boolean last;
    private Boolean first;
}