package mip.restaurantfx.service;

/**
 * Acces global minimal (anti-pattern in general), dar folosit aici ca refactor minim:
 * View-urile nu mai creeaza repo-uri, ci iau servicii dintr-un singur loc.
 */
public final class AppContext {
    private static final ServiceFactory INSTANCE = new ServiceFactory();

    private AppContext() {}

    public static ServiceFactory services() {
        return INSTANCE;
    }
}

