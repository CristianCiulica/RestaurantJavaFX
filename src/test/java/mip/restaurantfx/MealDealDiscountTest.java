package mip.restaurantfx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MealDealDiscountTest {

    @Test
    void mealDeal_appliesOnCheapestDesertPerPizza() {
        Comanda c = new Comanda(new Masa(1, 4));

        Mancare pizza = new Mancare("Pizza Margherita", 40.0, 450, true);
        pizza.setId(1L);
        Mancare desert1 = new Mancare("Cheesecake Vanilie", 30.0, 180, false);
        desert1.setId(2L);
        Mancare desert2 = new Mancare("Tiramisu Special", 100.0, 250, false);
        desert2.setId(3L);

        c.adaugaProdus(pizza, 2);
        c.adaugaProdus(desert1, 1);
        c.adaugaProdus(desert2, 1);
        c.calculeazaTotal();
        assertEquals(210.0, c.getTotal(), 0.0001);

        c.clearDiscountLines();
        new MealDealDiscount().aplicaDiscount(c);
        c.calculeazaTotal();

        // pizzaCount=2, deserts=2 => 2 discounts: cheapest 30 and next 100 => 7.5 + 25 = 32.5
        assertEquals(1, c.getDiscountLines().size());
        DetaliuComanda line = c.getDiscountLines().stream().findFirst().orElseThrow();
        assertEquals(-32.5, line.getValoare(), 0.0001);
        assertEquals(177.5, c.getTotal(), 0.0001);
    }
}
