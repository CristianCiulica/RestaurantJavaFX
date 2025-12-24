package mip.restaurantfx;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClientAdvancedFilterTest {

    @Test
    void vegetarianAndTypeAndPriceRangeShouldWorkTogether() {
        Produs m1 = new Mancare("Salata", 18.0, 200, true);
        Produs m2 = new Mancare("Burger", 19.0, 300, false);
        Produs b1 = new Bautura("Apa", 8.0, 500, false);

        List<Produs> all = List.of(m1, m2, b1);

        // combinat: doar vegetariene + doar mâncare + preț <= 20
        boolean doarVeg = true;
        String tip = "Mâncare";
        double min = 0;
        double max = 20;

        List<Produs> filtered = all.stream()
                .filter(p -> {
                    return switch (tip) {
                        case "Mâncare" -> p instanceof Mancare;
                        case "Băutură" -> p instanceof Bautura;
                        default -> true;
                    };
                })
                .filter(p -> !doarVeg || (p instanceof Mancare && ((Mancare) p).isVegetarian()))
                .filter(p -> p.getPret() >= min && p.getPret() <= max)
                .toList();

        assertEquals(1, filtered.size());
        assertEquals("Salata", filtered.get(0).getNume());
    }

    @Test
    void typeDrinkShouldReturnOnlyDrinks() {
        Produs m1 = new Mancare("Salata", 18.0, 200, true);
        Produs b1 = new Bautura("Apa", 8.0, 500, false);
        Produs b2 = new Bautura("Bere", 12.0, 500, true);

        List<Produs> all = List.of(m1, b1, b2);
        String tip = "Băutură";

        List<Produs> filtered = all.stream()
                .filter(p -> {
                    return switch (tip) {
                        case "Mâncare" -> p instanceof Mancare;
                        case "Băutură" -> p instanceof Bautura;
                        default -> true;
                    };
                })
                .toList();

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(p -> p instanceof Bautura));
    }
}
