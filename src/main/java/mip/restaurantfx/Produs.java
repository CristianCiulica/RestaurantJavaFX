package mip.restaurantfx;

import jakarta.persistence.*;

@Entity
@Table(name = "produse")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tip_produs", discriminatorType = DiscriminatorType.STRING)
public abstract class Produs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nume;
    private double pret;
    private String categorie;

    @Column(nullable = false)
    @org.hibernate.annotations.ColumnDefault("true")
    private boolean activ = true;

    public Produs() {}

    public Produs(String nume, double pret) {
        this(nume, pret, null);
    }

    public Produs(String nume, double pret, String categorie) {
        this.nume = nume;
        this.pret = pret;
        this.categorie = categorie;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNume() { return nume; }

    public void setNume(String nume) { this.nume = nume; }

    public double getPret() { return pret; }

    public void setPret(double pret) { this.pret = pret; }

    public String getCategorie() { return categorie; }

    public void setCategorie(String categorie) { this.categorie = categorie; }

    public boolean isActiv() {
        return activ;
    }

    public void setActiv(boolean activ) {
        this.activ = activ;
    }

    public abstract String getDetalii();

    @Override
    public String toString() {
        return nume;
    }
}