package mip.restaurantfx;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tip_produs", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tip_json")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Mancare.class, name = "mancare"),
        @JsonSubTypes.Type(value = Bautura.class, name = "bautura"),
        @JsonSubTypes.Type(value = Pizza.class, name = "pizza")
})
public abstract class Produs implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nume;

    @Column(nullable = false)
    private double pret;

    public Produs() {}

    public Produs(String nume, double pret) {
        this.nume = nume;
        this.pret = pret;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public double getPret() { return pret; }
    public void setPret(double pret) { this.pret = pret; }

    public abstract String getDetalii();

    @Override
    public String toString() {
        return nume + " - " + String.format("%.2f", pret) + " RON";
    }
}