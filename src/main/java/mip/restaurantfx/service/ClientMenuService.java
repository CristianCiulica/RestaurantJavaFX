package mip.restaurantfx.service;

import mip.restaurantfx.Mancare;
import mip.restaurantfx.Produs;
import mip.restaurantfx.ProdusRepository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;


public class ClientMenuService {

    private final ProdusRepository produsRepository;

    public ClientMenuService(ProdusRepository produsRepository) {
        this.produsRepository = produsRepository;
    }

    public List<Produs> getAllProducts() {
        return produsRepository.getAllActive();
    }

    public List<Produs> filterProducts(ProductFilterCriteria criteria) {
        List<Produs> toate = produsRepository.getAllActive();

        String cautare = Optional.ofNullable(criteria.searchText()).orElse("")
                .trim()
                .toLowerCase(Locale.ROOT);

        boolean doarVeg = criteria.vegOnly();

        Double min = criteria.minPrice();
        Double max = criteria.maxPrice();
        if (min != null && max != null && min > max) {
            double tmp = min;
            min = max;
            max = tmp;
        }
        final Double fMin = min;
        final Double fMax = max;

        return toate.stream()
                .filter(p -> {
                    if (cautare.isEmpty()) return true;
                    String nume = p.getNume() == null ? "" : p.getNume().toLowerCase(Locale.ROOT);
                    return nume.contains(cautare);
                })
                .filter(p -> {
                    if (!doarVeg) return true;
                    return (p instanceof Mancare) && ((Mancare) p).isVegetarian();
                })
                .filter(p -> {
                    if (fMin != null && p.getPret() < fMin) return false;
                    if (fMax != null && p.getPret() > fMax) return false;
                    return true;
                })
                .collect(Collectors.toList());
    }
}
