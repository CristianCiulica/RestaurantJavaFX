package mip.restaurantfx.service;

import mip.restaurantfx.DataSeeder;

public final class AppInitializer {

    private AppInitializer() {}

    private static volatile boolean initialized = false;

    public static void init() {
        if (initialized) return;
        synchronized (AppInitializer.class) {
            if (initialized) return;
            DataSeeder.seed();
            initialized = true;
        }
    }
}

