package mip.restaurantfx.service;

import mip.restaurantfx.*;

public class ServiceFactory {

    private final UserRepository userRepository = new UserRepository();
    private final ProdusRepository produsRepository = new ProdusRepository();
    private final ComandaRepository comandaRepository = new ComandaRepository();
    private final MasaRepository masaRepository = new MasaRepository();

    private final ClientMenuService clientMenuService = new ClientMenuService(produsRepository);
    private final ProductImageService productImageService = new ProductImageService();
    private final OfferConfigService offerConfigService = new OfferConfigService();

    private final OrderService orderService = new OrderService(comandaRepository, masaRepository, offerConfigService);
    private final AdminService adminService = new AdminService(userRepository, produsRepository, comandaRepository, offerConfigService);

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

    public OfferConfigService offers() {
        return offerConfigService;
    }

    public OrderService orders() {
        return orderService;
    }

    public AdminService admin() {
        return adminService;
    }
}
