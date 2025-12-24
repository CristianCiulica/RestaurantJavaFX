package mip.restaurantfx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PartyPackDiscountTest {

    @Test
    void partyPack_oneFreePizzaPer4CheapestFree() {
        Comanda c = new Comanda(new Masa(1, 4));

        Mancare p1 = new Mancare("Pizza A", 40.0, 450, true); p1.setId(1L);
        Mancare p2 = new Mancare("Pizza B", 60.0, 450, true); p2.setId(2L);

        c.adaugaProdus(p1, 3);
        c.adaugaProdus(p2, 1);
        c.calculeazaTotal();
        assertEquals(180.0, c.getTotal(), 0.0001);

        c.clearDiscountLines();
        new PartyPackDiscount().aplicaDiscount(c);
        c.calculeazaTotal();

        // 4 pizzas => 1 free, cheapest unit is 40
        assertEquals(1, c.getDiscountLines().size());
        DetaliuComanda line = c.getDiscountLines().stream().findFirst().orElseThrow();
        assertEquals(-40.0, line.getValoare(), 0.0001);
        assertEquals(140.0, c.getTotal(), 0.0001);
    }
}
