package mip.restaurantfx;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import javafx.beans.property.*;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tip_json"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Mancare.class, name = "mancare"),
        @JsonSubTypes.Type(value = Bautura.class, name = "bautura")
})
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tip_produs", discriminatorType = DiscriminatorType.STRING)
public abstract class Produs implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nume")
    private String numePersist;

    @Column(name = "pret")
    private double pretPersist;

    @Transient
    @JsonIgnore
    private StringProperty numeProperty;
    @Transient
    @JsonIgnore
    private DoubleProperty pretProperty;

    public Produs() {}

    public Produs(String nume, double pret) {
        this.numePersist = nume;
        this.pretPersist = pret;
        this.numeProperty = new SimpleStringProperty(nume);
        this.pretProperty = new SimpleDoubleProperty(pret);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNume() {
        return numeProperty != null ? numeProperty.get() : numePersist;
    }

    public void setNume(String nume) {
        this.numePersist = nume;
        if (this.numeProperty != null) {
            this.numeProperty.set(nume);
        } else {
            this.numeProperty = new SimpleStringProperty(nume);
        }
    }

    public StringProperty numeProperty() {
        if (numeProperty == null) numeProperty = new SimpleStringProperty(numePersist);
        return numeProperty;
    }

    public double getPret() {
        return pretProperty != null ? pretProperty.get() : pretPersist;
    }

    public void setPret(double pret) {
        this.pretPersist = pret;
        if (this.pretProperty != null) {
            this.pretProperty.set(pret);
        } else {
            this.pretProperty = new SimpleDoubleProperty(pret);
        }
    }

    public DoubleProperty pretProperty() {
        if (pretProperty == null) pretProperty = new SimpleDoubleProperty(pretPersist);
        return pretProperty;
    }

    public abstract void afisareProdus();

    @Override
    public String toString() {
        return getNume() + " (" + getPret() + " RON)";
    }
}