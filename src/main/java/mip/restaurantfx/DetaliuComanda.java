package mip.restaurantfx;

import jakarta.persistence.*;

@Entity
@Table(name = "detalii_comanda")
public class DetaliuComanda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "comanda_id")
    private Comanda comanda;

    @Column(nullable = false)
    private String descriere;

    @Column(nullable = false)
    private double valoare;

    public DetaliuComanda() {}

    public DetaliuComanda(String descriere, double valoare) {
        this.descriere = descriere;
        this.valoare = valoare;
    }

    public Long getId() { return id; }

    public Comanda getComanda() { return comanda; }

    public void setComanda(Comanda comanda) { this.comanda = comanda; }

    public String getDescriere() { return descriere; }

    public double getValoare() { return valoare; }

    @Override
    public String toString() {
        return descriere + ": " + String.format("%.2f", valoare);
    }
}