package mip.restaurantfx.service;

public record ProductFilterCriteria(
        String searchText,
        boolean vegOnly,
        Double minPrice,
        Double maxPrice
) {
}
