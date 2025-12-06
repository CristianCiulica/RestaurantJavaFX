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
    public void setVolum(int volum) { this.volum = volum; }

    public boolean isAlcoolica() { return alcoolica; }
    public void setAlcoolica(boolean alcoolica) { this.alcoolica = alcoolica; }

    @Override
    public void afisareProdus() {
        System.out.println("> " + getNume() + " - " + getPret() + " RON - Volum: " + volum + "ml" + (alcoolica ? " (alcoolică)" : ""));
    }
}