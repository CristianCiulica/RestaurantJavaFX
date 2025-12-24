package mip.restaurantfx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HappyHourDiscount implements DiscountRule {

    @Override
    public void aplicaDiscount(Comanda comanda) {
        if (comanda == null || comanda.getItems() == null || comanda.getItems().isEmpty()) {
            return;
        }

        List<Double> bauturiPretUnit = new ArrayList<>();
        for (ComandaItem item : comanda.getItems()) {
            if (item.getProdus() instanceof Bautura) {
                for (int i = 0; i < item.getCantitate(); i++) {
                    bauturiPretUnit.add(item.getPretUnitar());
                }
            }
        }

        if (bauturiPretUnit.size() < 2) {
            return;
        }

        bauturiPretUnit.sort(Comparator.reverseOrder());

        double discountTotal = 0.0;
        for (int i = 1; i < bauturiPretUnit.size(); i += 2) {
            discountTotal += bauturiPretUnit.get(i) * 0.5;
        }

        if (discountTotal <= 0.0) {
            return;
        }

        comanda.addDiscountLine(new DetaliuComanda("Happy Hour (a 2-a bautura -50%)", -discountTotal));
    }
}