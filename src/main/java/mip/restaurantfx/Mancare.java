package mip.restaurantfx;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Mancare")
public class Mancare extends Produs {
    private int gramaj;
    private boolean vegetarian;

    public Mancare() {}

    public Mancare(String nume, double Pret, int Gramaj, boolean vegetarian) {
        super(nume, Pret);
        this.gramaj = Gramaj;
        this.vegetarian = vegetarian;
    }

    public int getGramaj() { return gramaj; }
    public void setGramaj(int gramaj) { this.gramaj = gramaj; }

    public boolean isVegetarian() { return vegetarian; }
    public void setVegetarian(boolean vegetarian) { this.vegetarian = vegetarian; }

    @Override
    public void afisareProdus() {
        String veggie = vegetarian ? " (Vegetarian)" : "";
        System.out.println("> " + getNume() + " - " + getPret() + " RON - Gramaj: " + gramaj + "g" + veggie);
    }
}