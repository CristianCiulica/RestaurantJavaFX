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

    /**
     * Path către imaginea produsului (de preferat în resources), ex: "/mip/restaurantfx/images/pizza_margherita.jpg".
     * Poate fi null/blank -> se folosește o imagine default.
     */
    private String imagePath;

    public Produs() {}

    public Produs(String nume, double pret) {
        this(nume, pret, null);
    }

    public Produs(String nume, double pret, String categorie) {
        this(nume, pret, categorie, null);
    }

    public Produs(String nume, double pret, String categorie, String imagePath) {
        this.nume = nume;
        this.pret = pret;
        this.categorie = categorie;
        this.imagePath = imagePath;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNume() { return nume; }

    public void setNume(String nume) { this.nume = nume; }

    public double getPret() { return pret; }

    public void setPret(double pret) { this.pret = pret; }

    public String getCategorie() { return categorie; }

    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getImagePath() { return imagePath; }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public abstract String getDetalii();

    @Override
    public String toString() {
        return nume;
    }
}