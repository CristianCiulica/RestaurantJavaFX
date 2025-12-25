package mip.restaurantfx.service;

public final class AppContext {
    private static final ServiceFactory INSTANCE = new ServiceFactory();

    private AppContext() {}

    public static ServiceFactory services() {
        return INSTANCE;
    }
}

