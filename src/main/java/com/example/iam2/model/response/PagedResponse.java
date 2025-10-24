package com.example.iam2.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> data;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;

    public PagedResponse(List<T> data, int page, int size, long totalItems) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / size);
    }
}
