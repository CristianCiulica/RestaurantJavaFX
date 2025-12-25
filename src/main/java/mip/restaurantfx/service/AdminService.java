package mip.restaurantfx.service;

import mip.restaurantfx.*;

import java.util.List;

/**
 * Use-case/service pentru modulul Manager. Scoate SQL/persistenta din UI.
 */
public class AdminService {

    private final UserRepository userRepo;
    private final ProdusRepository produsRepo;
    private final ComandaRepository comandaRepo;

    public AdminService(UserRepository userRepo, ProdusRepository produsRepo, ComandaRepository comandaRepo) {
        this.userRepo = userRepo;
        this.produsRepo = produsRepo;
        this.comandaRepo = comandaRepo;
    }

    public List<User> getAllStaff() {
        return userRepo.getAllStaff();
    }

    public void addStaff(String username, String password, String nume) {
        userRepo.save(new User(username.trim(), password, nume.trim(), User.Role.STAFF));
    }

    public boolean deleteStaffByUsername(String username) {
        return userRepo.deleteByUsername(username);
    }

    public List<Produs> getAllProducts() {
        return produsRepo.getAll();
    }

    public void saveProduct(Produs p) {
        produsRepo.salveazaProdus(p);
    }

    public List<Comanda> getGlobalHistory() {
        return comandaRepo.getIstoricGlobal();
    }

    public void deleteAllHistory() {
        comandaRepo.deleteAll();
    }
}

