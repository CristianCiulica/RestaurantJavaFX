package mip.restaurantfx;

import jakarta.persistence.*;

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
    @Override
    public String toString() { return nume + " (" + rol + ")"; }
}