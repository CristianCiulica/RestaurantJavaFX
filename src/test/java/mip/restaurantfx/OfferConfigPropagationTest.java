package mip.restaurantfx;

import mip.restaurantfx.service.OfferConfigService;
import mip.restaurantfx.service.OrderService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OfferConfigPropagationTest {

    @Test
    void disablingHappyHourRemovesDiscountLine() {
        OfferConfigService cfg = new OfferConfigService();
        OrderService service = new OrderService(null, null, cfg);

        Comanda c = new Comanda(new Masa(1, 2));
        c.adaugaProdus(new Bautura("Bere", 10.0, 500, true), 2);

        service.recalculateTotal(c);
        assertTrue(c.getDiscountLines().stream().anyMatch(d -> d.getDescriere().toLowerCase().contains("happy hour")));

        cfg.setEnabled(OfferConfigService.OfferKey.HAPPY_HOUR, false);
        service.recalculateTotal(c);
        assertFalse(c.getDiscountLines().stream().anyMatch(d -> d.getDescriere().toLowerCase().contains("happy hour")));
    }
}

