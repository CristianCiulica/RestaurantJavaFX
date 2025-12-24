package mip.restaurantfx;

import jakarta.persistence.*;

@Entity
public class Masa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private int numarMasa;

    private int locuri;

    private boolean esteOcupata;

    public Masa() {}

    public Masa(int numarMasa, int locuri) {
        this.numarMasa = numarMasa;
        this.locuri = locuri;
        this.esteOcupata = false;
    }

    public Long getId() { return id; }
    public int getNumarMasa() { return numarMasa; }
    public boolean isEsteOcupata() { return esteOcupata; }
    public void setEsteOcupata(boolean esteOcupata) { this.esteOcupata = esteOcupata; }

    @Override
    public String toString() { return "Masa " + numarMasa; }
}