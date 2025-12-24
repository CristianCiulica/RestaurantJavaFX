package mip.restaurantfx;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Mancare")
public class Mancare extends Produs {
    private int gramaj;
    private boolean vegetarian;

    public Mancare() {}

    public Mancare(String nume, double pret, int gramaj, boolean vegetarian) {
        // categoria poate fi setata ulterior; pentru moment nu o fortam
        super(nume, pret);
        this.gramaj = gramaj;
        this.vegetarian = vegetarian;
    }

    public int getGramaj() { return gramaj; }

    public boolean isVegetarian() { return vegetarian; }

    @Override
    public String getDetalii() {
        return "Gramaj: " + gramaj + "g" + (vegetarian ? " (Vegetarian)" : "");
    }
}