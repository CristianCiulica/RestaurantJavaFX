package mip.restaurantfx;
public class Mancare extends Produs {
    private int gramaj;
    private boolean vegetarian;
    public Mancare(String nume, double Pret, int Gramaj, boolean vegetarian) {
        super(nume, Pret);
        this.gramaj = Gramaj;
        this.vegetarian = vegetarian;
    }

    public Mancare(String nume, double Pret, int Gramaj) {
        this(nume, Pret, Gramaj, false);
    }

    @Override
    public void afisareProdus() {
        String veggie = vegetarian ? " (Vegetarian)" : "";
        System.out.println("> " + getNume() + " - " + getPret() + " RON - Gramaj: " + gramaj + "g" + veggie);
    }
    public int getGramaj() {
        return gramaj;
    }
    public boolean isVegetarian() {
        return vegetarian;
    }
}