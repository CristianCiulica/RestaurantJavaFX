package mip.restaurantfx.service;

import mip.restaurantfx.*;

/**
 * Use-case/service pentru plasarea comenzii (Staff).
 * Contine logica de comanda + aplicare oferte + persistenta, fara JavaFX.
 */
public class OrderService {

    private final ComandaRepository comandaRepo;
    private final MasaRepository masaRepo;
    private final OfferConfigService offerConfig;

    public OrderService(ComandaRepository comandaRepo, MasaRepository masaRepo, OfferConfigService offerConfig) {
        this.comandaRepo = comandaRepo;
        this.masaRepo = masaRepo;
        this.offerConfig = offerConfig;
    }

    public Comanda loadOrCreateActiveOrder(Masa masa) {
        if (masa == null) throw new IllegalArgumentException("masa is null");

        Comanda existenta = comandaRepo.getComandaActiva(masa.getId());
        if (existenta != null) return existenta;

        Comanda noua = new Comanda(masa);
        if (!masa.isEsteOcupata()) {
            masa.setEsteOcupata(true);
            masaRepo.save(masa);
        }
        return noua;
    }

    public void addProduct(Comanda comanda, Produs produs, int qty) {
        if (comanda == null || produs == null) return;
        if (qty <= 0) return;
        comanda.adaugaProdus(produs, qty);
        recalculateTotal(comanda);
    }

    public void changeQuantity(Comanda comanda, ComandaItem item, int delta) {
        if (comanda == null || item == null || delta == 0) return;
        int newQty = item.getCantitate() + delta;
        if (newQty <= 0) {
            comanda.stergeProdus(item);
        } else {
            item.setCantitate(newQty);
        }
        recalculateTotal(comanda);
    }

    public void removeItem(Comanda comanda, ComandaItem item) {
        if (comanda == null || item == null) return;
        comanda.stergeProdus(item);
        recalculateTotal(comanda);
    }

    public void recalculateTotal(Comanda comanda) {
        if (comanda == null) return;

        comanda.clearDiscountLines();
        comanda.calculeazaTotal();

        // Oferte hardcodate, dar activarea e controlata din Admin.
        if (offerConfig == null || offerConfig.isEnabled(OfferConfigService.OfferKey.HAPPY_HOUR)) {
            new HappyHourDiscount().aplicaDiscount(comanda);
        }
        if (offerConfig == null || offerConfig.isEnabled(OfferConfigService.OfferKey.MEAL_DEAL)) {
            new MealDealDiscount().aplicaDiscount(comanda);
        }
        if (offerConfig == null || offerConfig.isEnabled(OfferConfigService.OfferKey.PARTY_PACK)) {
            new PartyPackDiscount().aplicaDiscount(comanda);
        }

        comanda.calculeazaTotal();
    }

    public void saveOrder(Comanda comanda, User ospatar, Masa masa, boolean keepOccupied) {
        if (comanda == null || masa == null) return;

        recalculateTotal(comanda);

        if (ospatar != null) {
            comanda.setOspatar(ospatar);
        }

        masa.setEsteOcupata(keepOccupied);
        masaRepo.save(masa);

        if (!keepOccupied) {
            comanda.setStatus(Comanda.StatusComanda.PLATITA);
        }

        comandaRepo.save(comanda);
    }
}
