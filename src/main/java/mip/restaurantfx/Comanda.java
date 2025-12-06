package mip.restaurantfx;

import java.util.HashMap;
import java.util.Map;

public class Comanda {
    private Map<Produs, Integer> produseComandate = new HashMap<>();
    private double tva;
    public Comanda(double tva) {
        this.tva = tva;
    }
    void adaugaProdus(Produs p, int cantitate) {
        if (!(p instanceof Mancare) && !(p instanceof Bautura)) {
            System.out.println("Se pot adauga doar produse de tip Mancare sau Bautura");
            return;
        }
        if (p == null) {
            System.out.println("Produsul este null!");
            return;
        }
        produseComandate.put(p, cantitate);
    }

    void aplicaDiscountRule(DiscountRule rule) {
        if (rule != null) {
            rule.aplicaDiscount(this);
        }
    }

    void afiseazaComanda() {
        if (produseComandate.isEmpty()) {
            System.out.println("Comanda este goala!");
            return;
        }
        for (Produs produsCurent : produseComandate.keySet()) {
            int cantitate = produseComandate.get(produsCurent);
            double pretTotal = produsCurent.getPret() * cantitate;
            double pretCuTVA = pretTotal * (1 + tva);
            System.out.printf("> %s - Cantitate: %d - Pret: %.2f RON (cu TVA: %.2f RON)%n",
                    produsCurent.getNume(), cantitate, pretTotal, pretCuTVA);
        }
    }
    void calculeazaTotal() {
        if (produseComandate.isEmpty()) {
            System.out.println("Comanda este goala!");
            return;
        }
        double subtotal = 0;
        for (Produs produsCurent : produseComandate.keySet()) {
            subtotal += produsCurent.getPret() * produseComandate.get(produsCurent);
        }
        double valoareTVA = subtotal * tva;
        double sumaTotala = subtotal + valoareTVA;

        System.out.printf("Subtotal (fara TVA): %.2f RON%n", subtotal);
        System.out.printf("TVA (%.0f%%): %.2f RON%n", tva * 100, valoareTVA);
        System.out.printf("TOTAL DE PLATA: %.2f RON%n", sumaTotala);
    }

    public Map<Produs, Integer> getProduseComandate() {
        return produseComandate;
    }
}