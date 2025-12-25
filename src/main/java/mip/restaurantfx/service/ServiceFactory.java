package mip.restaurantfx.service;

import mip.restaurantfx.*;

/**
 * Composition root simplu: creeaza si partajeaza repo-urile/serviciile.
 * Fara framework DI, dar suficient ca sa nu instantiem repo-uri in View-uri.
 */
public class ServiceFactory {

    private final UserRepository userRepository = new UserRepository();
    private final ProdusRepository produsRepository = new ProdusRepository();
    private final ComandaRepository comandaRepository = new ComandaRepository();
    private final MasaRepository masaRepository = new MasaRepository();

    private final ClientMenuService clientMenuService = new ClientMenuService(produsRepository);
    private final ProductImageService productImageService = new ProductImageService();
    private final OrderService orderService = new OrderService(comandaRepository, masaRepository);
    private final AdminService adminService = new AdminService(userRepository, produsRepository, comandaRepository);

    public UserRepository users() {
        return userRepository;
    }

    public ProdusRepository produse() {
        return produsRepository;
    }

    public ComandaRepository comenzi() {
        return comandaRepository;
    }

    public MasaRepository mese() {
        return masaRepository;
    }

    public ClientMenuService clientMenu() {
        return clientMenuService;
    }

    public ProductImageService productImages() {
        return productImageService;
    }

    public OrderService orders() {
        return orderService;
    }

    public AdminService admin() {
        return adminService;
    }
}
