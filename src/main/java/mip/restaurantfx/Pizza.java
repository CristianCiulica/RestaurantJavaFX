package mip.restaurantfx;
import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;

@Entity
@DiscriminatorValue("Pizza")
public class Pizza extends Mancare {
    private String blat;
    private String sos;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> toppinguri;

    private static final double PRET_BAZA = 25.0;
    private static final double PRET_TOPPING = 3.5;

    public Pizza() {
        super();
        this.toppinguri = new ArrayList<>();
    }

    private Pizza(PizzaBuilder builder) {
        super(builder.buildNume(),builder.buildPret(),500,!builder.toppinguri.contains("Salam"));
        this.blat = builder.blat;
        this.sos = builder.sos;
        this.toppinguri = builder.toppinguri;
    }

    @Override
    public void afisareProdus() {
        System.out.println("> " + getNume() + " - " + getPret() + " RON - Gramaj: " + getGramaj() + "g" + (isVegetarian() ? " (Vegetarian)" : ""));
        System.out.println("    [Blat: " + blat + ", Sos: " + sos + ", Toppinguri: " + toppinguri + "]");
    }

    public String getBlat() { return blat; }
    public void setBlat(String blat) { this.blat = blat; }

    public String getSos() { return sos; }
    public void setSos(String sos) { this.sos = sos; }

    public List<String> getToppinguri() { return toppinguri; }
    public void setToppinguri(List<String> toppinguri) { this.toppinguri = toppinguri; }

    public static class PizzaBuilder {
        private final String blat;
        private final String sos;
        private List<String> toppinguri = new ArrayList<>();

        public PizzaBuilder(String blat, String sos) {
            this.blat = blat;
            this.sos = sos;
        }

        public PizzaBuilder withTopping(String topping) {
            this.toppinguri.add(topping);
            return this;
        }

        public PizzaBuilder withExtraMozzarella() {
            return withTopping("Extra Mozzarella");
        }

        public PizzaBuilder withCiuperci() {
            return withTopping("Ciuperci");
        }

        public PizzaBuilder withSalam() {
            return withTopping("Salam");
        }

        public Pizza build() {
            return new Pizza(this);
        }

        private String buildNume() {
            String nume = "Pizza Custom (Blat " + blat + ", Sos " + sos;
            if (toppinguri.isEmpty()) {
                nume += ")";
            } else {
                nume += ", " + toppinguri.size() + " toppinguri)";
            }
            return nume;
        }

        private double buildPret() {
            return PRET_BAZA + (toppinguri.size() * PRET_TOPPING);
        }
    }
}