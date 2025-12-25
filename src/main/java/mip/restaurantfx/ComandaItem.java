package mip.restaurantfx;

import jakarta.persistence.*;

@Entity
@Table(name = "comanda_items")
public class ComandaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    @ManyToOne
    @JoinColumn(name = "produs_id", nullable = false)
    private Produs produs;

    private int cantitate;
    @Column(name = "pret_unitar")
    private double pretUnitar;

    public ComandaItem() {}

    public ComandaItem(Comanda comanda, Produs produs, int cantitate) {
        this.comanda = comanda;
        this.produs = produs;
        this.cantitate = cantitate;
        this.pretUnitar = produs.getPret();
    }

    public double getSubtotal() {
        return this.pretUnitar * this.cantitate;
    }

    public Long getId() { return id; }
    public Produs getProdus() { return produs; }
    public int getCantitate() { return cantitate; }
    public void setCantitate(int cantitate) { this.cantitate = cantitate; }
    public double getPretUnitar() { return pretUnitar; }
    public Comanda getComanda() { return comanda; }
    public void setComanda(Comanda comanda) { this.comanda = comanda; }
}