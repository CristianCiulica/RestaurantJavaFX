package mip.restaurantfx.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OfferConfigService {

    public enum OfferKey {
        HAPPY_HOUR,
        MEAL_DEAL,
        PARTY_PACK
    }

    private final Map<OfferKey, Boolean> enabled = new ConcurrentHashMap<>();

    public OfferConfigService() {
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

