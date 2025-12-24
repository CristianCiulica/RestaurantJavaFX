package mip.restaurantfx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MealDealDiscount implements DiscountRule {

    @Override
    public void aplicaDiscount(Comanda comanda) {
        if (comanda == null || comanda.getItems() == null || comanda.getItems().isEmpty()) {
            return;
        }

        int pizzaCount = 0;
        List<Double> desertUnitPrices = new ArrayList<>();

        for (ComandaItem item : comanda.getItems()) {
            Produs p = item.getProdus();
            if (p == null) continue;

            if (isPizza(p)) {
                pizzaCount += item.getCantitate();
            }

            if (isDesert(p)) {
                for (int i = 0; i < item.getCantitate(); i++) {
                    desertUnitPrices.add(item.getPretUnitar());
                }
            }
        }

        if (pizzaCount <= 0 || desertUnitPrices.isEmpty()) {
            return;
        }

        desertUnitPrices.sort(Comparator.naturalOrder());
        int discountsToApply = Math.min(pizzaCount, desertUnitPrices.size());

        double discountTotal = 0.0;
        for (int i = 0; i < discountsToApply; i++) {
            discountTotal += desertUnitPrices.get(i) * 0.25;
        }

        if (discountTotal <= 0.0) return;

        comanda.addDiscountLine(new DetaliuComanda("Meal Deal (desert -25%)", -discountTotal));
    }

    private boolean isPizza(Produs p) {
        String n = p.getNume();
        return n != null && n.toLowerCase().contains("pizza");
    }

    private boolean isDesert(Produs p) {
        String n = p.getNume();
        if (n == null) return false;
        String x = n.toLowerCase();
        return x.contains("tiramisu") || x.contains("cheesecake") || x.contains("panna cotta") || x.contains("salata de fructe");
    }
}
