package mip.restaurantfx.service;

/**
 * DTO pentru filtrele din modulul Guest.
 * View-ul colecteaza inputul (TextField/CheckBox), iar Service-ul aplica regulile (Streams API).
 */
public record ProductFilterCriteria(
        String searchText,
        boolean vegOnly,
        Double minPrice,
        Double maxPrice
) {
}
