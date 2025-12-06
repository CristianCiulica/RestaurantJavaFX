package mip.restaurantfx;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.Objects;

public abstract class Produs {
    // Folosim Properties pentru a permite Data Binding cu interfata grafica
    private StringProperty nume;
    private DoubleProperty pret;

    public Produs() {
        this.nume = new SimpleStringProperty("");
        this.pret = new SimpleDoubleProperty(0.0);
    }

    public Produs(String nume, double pret) {
        this.nume = new SimpleStringProperty(nume);
        this.pret = new SimpleDoubleProperty(pret);
    }

    // --- Getteri si Setteri pentru JavaFX Properties ---

    // 1. Pentru NUME
    public String getNume() {
        return nume.get();
    }
    public void setNume(String nume) {
        this.nume.set(nume);
    }
    public StringProperty numeProperty() { // Asta cauta JavaFX pentru binding
        return nume;
    }

    // 2. Pentru PRET
    public double getPret() {
        return pret.get();
    }
    public void setPret(double pret) {
        this.pret.set(pret);
    }
    public DoubleProperty pretProperty() {
        return pret;
    }

    public abstract void afisareProdus();

    @Override
    public String toString() {
        return getNume() + " (" + getPret() + " RON)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produs)) return false;
        Produs produs = (Produs) o;
        return getNume().equals(produs.getNume());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNume());
    }
}