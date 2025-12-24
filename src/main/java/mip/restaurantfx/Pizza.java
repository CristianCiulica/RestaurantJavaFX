package mip.restaurantfx;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("Pizza")
public class Pizza extends Mancare {
    private String blat;
    private String sos;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> toppinguri = new ArrayList<>();

    public Pizza() {}

    private Pizza(Builder builder) {
        super("Pizza Custom", builder.pret, 500, false);
        this.blat = builder.blat;
        this.sos = builder.sos;
        this.toppinguri = builder.toppinguri;
    }

    public List<String> getToppinguri() { return toppinguri; }

    public String getBlat() { return blat; }

    public String getSos() { return sos; }

    @Override
    public String getDetalii() {
        return "Blat: " + blat + ", Sos: " + sos + ", Toppinguri: " + toppinguri;
    }

    public static class Builder {
        private final String blat;
        private final String sos;
        private final List<String> toppinguri = new ArrayList<>();
        private double pret = 25.0;

        public Builder(String blat, String sos) {
            this.blat = blat;
            this.sos = sos;
        }

        public Builder addTopping(String t) {
            this.toppinguri.add(t);
            this.pret += 3.5;
            return this;
        }

        public Pizza build() {
            return new Pizza(this);
        }
    }
}