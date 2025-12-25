package mip.restaurantfx;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "comenzi")
public class Comanda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_creare", nullable = false)
    private LocalDateTime dataCreare;

    @Enumerated(EnumType.STRING)
    private StatusComanda status;

    public enum StatusComanda {
        DESCHISA,
        PLATITA,
        ANULATA
    }

    @ManyToOne
    @JoinColumn(name = "masa_id")
    private Masa masa;

    @ManyToOne
    @JoinColumn(name = "ospatar_id")
    private User ospatar;

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ComandaItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<DetaliuComanda> discountLines = new LinkedHashSet<>();

    private double total;

    public Comanda() {
        this.dataCreare = LocalDateTime.now();
        this.status = StatusComanda.DESCHISA;
        this.total = 0.0;
    }

    public Comanda(Masa masa) {
        this();
        this.masa = masa;
    }


    public void adaugaProdus(Produs p, int cantitate) {
        if (p == null || cantitate <= 0) return;
        for (ComandaItem item : items) {
            if (item.getProdus() != null && item.getProdus().getId() != null && item.getProdus().getId().equals(p.getId())) {
                item.setCantitate(item.getCantitate() + cantitate);
                this.calculeazaTotal();
                return;
            }
        }

        ComandaItem newItem = new ComandaItem(this, p, cantitate);
        items.add(newItem);

        this.calculeazaTotal();
    }

    public void stergeProdus(ComandaItem item) {
        items.remove(item);
        item.setComanda(null);
        this.calculeazaTotal();
    }

    public void clearDiscountLines() {
        for (DetaliuComanda d : discountLines) {
            d.setComanda(null);
        }
        discountLines.clear();
    }

    public void addDiscountLine(DetaliuComanda detaliu) {
        if (detaliu == null) return;
        detaliu.setComanda(this);
        discountLines.add(detaliu);
        this.calculeazaTotal();
    }

    public void calculeazaTotal() {
        double sum = 0;
        for (ComandaItem item : items) {
            sum += item.getSubtotal();
        }
        for (DetaliuComanda d : discountLines) {
            sum += d.getValoare();
        }
        this.total = sum;
    }

    public Long getId() { return id; }
    public LocalDateTime getDataCreare() { return dataCreare; }
    public StatusComanda getStatus() { return status; }
    public void setStatus(StatusComanda status) { this.status = status; }
    public Masa getMasa() { return masa; }
    public void setMasa(Masa masa) { this.masa = masa; }
    public List<ComandaItem> getItems() { return items; }
    public Set<DetaliuComanda> getDiscountLines() { return discountLines; }
    public double getTotal() { return total; }
    public User getOspatar() { return ospatar; }
    public void setOspatar(User ospatar) { this.ospatar = ospatar; }
}