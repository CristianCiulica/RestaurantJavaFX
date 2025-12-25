package mip.restaurantfx;

public final class WindowState {

    private WindowState() {}

    private static volatile Boolean preferFullScreen = null;

    public static void rememberFullScreen(boolean fullScreen) {
        preferFullScreen = fullScreen;
    }

    public static boolean preferFullScreenByDefault(boolean fallback) {
        Boolean v = preferFullScreen;
        return v != null ? v : fallback;
    }

    public static void remember(boolean maximized) {
        rememberFullScreen(maximized);
    }
}
