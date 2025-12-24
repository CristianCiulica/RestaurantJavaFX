package mip.restaurantfx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Party Pack (Iteratia 7):
 * - La 4 Pizza comandate, una e gratis (cea mai ieftina pizza).
 * Reducerea apare pe bon ca linie separata (valoare negativa).
 */
public class PartyPackDiscount implements DiscountRule {

    @Override
    public void aplicaDiscount(Comanda comanda) {
        if (comanda == null || comanda.getItems() == null || comanda.getItems().isEmpty()) {
            return;
        }

        int pizzaCount = 0;
        List<Double> pizzaUnitPrices = new ArrayList<>();

        for (ComandaItem item : comanda.getItems()) {
            Produs p = item.getProdus();
            if (p == null) continue;
            if (!isPizza(p)) continue;

            pizzaCount += item.getCantitate();
            for (int i = 0; i < item.getCantitate(); i++) {
                pizzaUnitPrices.add(item.getPretUnitar());
            }
        }

        if (pizzaCount < 4) {
            return;
        }

        int freePizzas = pizzaCount / 4;
        pizzaUnitPrices.sort(Comparator.naturalOrder());

        double discountTotal = 0.0;
        for (int i = 0; i < freePizzas && i < pizzaUnitPrices.size(); i++) {
            discountTotal += pizzaUnitPrices.get(i);
        }

        if (discountTotal <= 0.0) return;

        comanda.addDiscountLine(new DetaliuComanda("Party Pack (1 pizza gratis la 4)", -discountTotal));
    }

    private boolean isPizza(Produs p) {
        String n = p.getNume();
        return n != null && n.toLowerCase().contains("pizza");
    }
}

