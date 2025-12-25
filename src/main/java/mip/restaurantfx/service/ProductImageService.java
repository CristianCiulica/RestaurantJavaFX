package mip.restaurantfx.service;

import javafx.scene.image.Image;
import mip.restaurantfx.Produs;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Responsabil doar de mapping nume produs -> resource image.
 * Nu tine de UI layout, doar de infrastructura resurselor.
 */
public class ProductImageService {

    private static final String IMAGES_DIR = "/mip/restaurantfx/images/";
    private static final String[] IMAGE_EXTS = new String[]{".png", ".jpg", ".jpeg"};
    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]+");

    public Image loadImageForProduct(Produs p) {
        if (p == null || p.getNume() == null || p.getNume().isBlank()) return null;

        String slug = slugify(p.getNume());
        for (String ext : IMAGE_EXTS) {
            String path = IMAGES_DIR + slug + ext;
            var url = ProductImageService.class.getResource(path);
            if (url != null) {
                return new Image(url.toExternalForm(), true);
            }
        }

        // No placeholder requested.
        return null;
    }

    static String slugify(String input) {
        String s = input.trim().toLowerCase(Locale.ROOT);
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("\\p{M}", "");
        s = NON_ALNUM.matcher(s).replaceAll("_");
        s = s.replaceAll("^_+|_+$", "");
        return s;
    }
}

