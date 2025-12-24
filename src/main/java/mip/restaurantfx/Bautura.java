package mip.restaurantfx;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Bautura")
public class Bautura extends Produs {
    private int volum;
    private boolean alcoolica;

    public Bautura() {}

    public Bautura(String nume, double pret, int volum, boolean alcoolica) {
        super(nume, pret);
        this.volum = volum;
        this.alcoolica = alcoolica;
    }

    public int getVolum() { return volum; }

    public boolean isAlcoolica() { return alcoolica; }

    @Override
    public String getDetalii() {
        return "Volum: " + volum + "ml" + (alcoolica ? " (Alcool)" : "");
    }
}