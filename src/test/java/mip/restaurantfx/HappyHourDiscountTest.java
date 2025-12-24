package mip.restaurantfx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HappyHourDiscountTest {

    @Test
    void happyHour_everySecondDrink_halfOff_addsNegativeDiscountLine() {
        Comanda c = new Comanda(new Masa(1, 4));

        Bautura b1 = new Bautura("Bere", 10.0, 500, true);
        b1.setId(1L);
        Bautura b2 = new Bautura("Vin", 20.0, 150, true);
        b2.setId(2L);

        c.adaugaProdus(b1, 1);
        c.adaugaProdus(b2, 1);
        c.calculeazaTotal();
        assertEquals(30.0, c.getTotal(), 0.0001);

        c.clearDiscountLines();
        new HappyHourDiscount().aplicaDiscount(c);
        c.calculeazaTotal();

        assertEquals(1, c.getDiscountLines().size());
        // sort desc => [20, 10], discount applies to 2nd => 10 * 0.5 = 5
        assertEquals(-5.0, c.getDiscountLines().get(0).getValoare(), 0.0001);
        assertEquals(25.0, c.getTotal(), 0.0001);
    }

    @Test
    void happyHour_singleDrink_noDiscount() {
        Comanda c = new Comanda(new Masa(1, 4));
        Bautura b1 = new Bautura("Bere", 10.0, 500, true);
        b1.setId(1L);
        c.adaugaProdus(b1, 1);

        c.clearDiscountLines();
        new HappyHourDiscount().aplicaDiscount(c);

        assertTrue(c.getDiscountLines().isEmpty());
    }
}

