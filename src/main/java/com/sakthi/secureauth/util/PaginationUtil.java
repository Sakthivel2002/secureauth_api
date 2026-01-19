package com.sakthi.secureauth.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PaginationUtil {

    private static final int MAX_PAGE_SIZE = 50;

    public static PageRequest createPageRequest(int page, int size) {

        int pageSize = Math.min(size, MAX_PAGE_SIZE);

        return PageRequest.of(
                Math.max(page, 0),
                pageSize,
                Sort.by("createdAt").descending()
        );
    }
}
