package mip.restaurantfx.service;

import mip.restaurantfx.*;

import java.util.List;

public class AdminService {

    private final UserRepository userRepo;
    private final ProdusRepository produsRepo;
    private final ComandaRepository comandaRepo;
    private final OfferConfigService offerConfig;

    public AdminService(UserRepository userRepo, ProdusRepository produsRepo, ComandaRepository comandaRepo, OfferConfigService offerConfig) {
        this.userRepo = userRepo;
        this.produsRepo = produsRepo;
        this.comandaRepo = comandaRepo;
        this.offerConfig = offerConfig;
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

    public OfferConfigService offers() {
        return offerConfig;
    }

    public boolean isOfferEnabled(OfferConfigService.OfferKey key) {
        return offerConfig != null && offerConfig.isEnabled(key);
    }

    public void setOfferEnabled(OfferConfigService.OfferKey key, boolean enabled) {
        if (offerConfig == null) return;
        offerConfig.setEnabled(key, enabled);
    }

    public boolean deleteProductById(Long id) {
        return produsRepo.deactivateById(id);
    }
}
