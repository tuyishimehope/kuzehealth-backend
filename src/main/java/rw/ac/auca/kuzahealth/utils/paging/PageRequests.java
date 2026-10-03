package rw.ac.auca.kuzahealth.utils.paging;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageRequests {

    public static final int MAX_SIZE = 100;

    private PageRequests() {
    }

    /**
     * @param sort "field" or "field,asc|desc"; falls back to newest first
     */
    public static Pageable of(int page, int size, String sort) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_SIZE);
        return PageRequest.of(safePage, safeSize, parseSort(sort));
    }

    private static Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        if (!property.matches("[A-Za-z][A-Za-z0-9]*")) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        boolean descending = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim());
        return Sort.by(descending ? Sort.Direction.DESC : Sort.Direction.ASC, property);
    }
}
