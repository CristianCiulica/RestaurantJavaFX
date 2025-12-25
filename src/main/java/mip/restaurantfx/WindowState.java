package mip.restaurantfx;

/**
 * Retine starea dorita a ferestrei intre ecrane.
 *
 * Scop: dupa Login/Logout sa nu se piarda maximizarea/fullscreen.
 */
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

    /**
     * Compatibilitate cu codul vechi: daca nu avem un state explicit, tratam maximized ca fullScreen.
     */
    public static void remember(boolean maximized) {
        rememberFullScreen(maximized);
    }
}
