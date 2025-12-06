package mip.restaurantfx;

public class Bautura extends Produs {
    private int volum;
    private boolean alcoolica;

    public Bautura(String nume, double pret, int volum) {
        super(nume, pret);
        this.volum = volum;
        this.alcoolica = false;
    }
    public Bautura(String nume, double pret, int volum, boolean alcoolica) {
        super(nume, pret);
        this.volum = volum;
        this.alcoolica = alcoolica;
    }
    public void afisareProdus() {
        System.out.println("> " + getNume() + " - " + getPret() + " RON - Volum: " + volum + "ml" + (alcoolica ? " (alcoolică)" : ""));
    }
    public boolean isAlcoolica() {
        return alcoolica;
    }
    public int getVolum() {
        return volum;
    }
}