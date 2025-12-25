package mip.restaurantfx;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String nume;
    @Enumerated(EnumType.STRING)
    private Role rol;

    @OneToMany(mappedBy = "ospatar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comanda> comenzi = new ArrayList<>();

    public enum Role {
        CLIENT, STAFF, ADMIN
    }
    public User() {}

    public User(String username, String password, String nume, Role rol) {
        this.username = username;
        this.password = password;
        this.nume = nume;
        this.rol = rol;
    }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public Role getRol() { return rol; }
    public String getNume() { return nume; }

    public List<Comanda> getComenzi() { return comenzi; }
    @Override
    public String toString() { return nume + " (" + rol + ")"; }
}