package mip.restaurantfx.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stare globala pentru ofertele hardcodate.
 *
 * Important: e doar configuratie (care oferte sunt active), NU contine logica de business.
 * Logica efectiva ramane in DiscountRule-urile existente.
 */
public class OfferConfigService {

    public enum OfferKey {
        HAPPY_HOUR,
        MEAL_DEAL,
        PARTY_PACK
    }

    private final Map<OfferKey, Boolean> enabled = new ConcurrentHashMap<>();

    public OfferConfigService() {
        // default: toate active
        for (OfferKey k : OfferKey.values()) {
            enabled.put(k, true);
        }
    }

    public boolean isEnabled(OfferKey key) {
        if (key == null) return false;
        return enabled.getOrDefault(key, false);
    }

    public void setEnabled(OfferKey key, boolean value) {
        if (key == null) return;
        enabled.put(key, value);
    }
}

