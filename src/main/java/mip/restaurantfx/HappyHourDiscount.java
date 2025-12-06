package mip.restaurantfx;
class HappyHourDiscount implements DiscountRule {
    private static final double discountBauturiAlcoolice = 0.20;
    private static final int startHour = 17;
    private static final int endHour = 19;
    private int oraCurenta;

    public HappyHourDiscount(int oraCurenta) {
        this.oraCurenta = oraCurenta;
    }
    @Override
    public void aplicaDiscount(Comanda comanda) {
        if (oraCurenta >= startHour && oraCurenta < endHour) {
            int produseReduse = 0;
            for (Produs produsCurent : comanda.getProduseComandate().keySet()) {
                if (produsCurent instanceof Bautura) {
                    Bautura bauturaCurenta = (Bautura) produsCurent;
                    if (bauturaCurenta.isAlcoolica()) {
                        double pretInitial = bauturaCurenta.getPret();
                        double pretCuDiscount = pretInitial * (1 - discountBauturiAlcoolice);
                        bauturaCurenta.setPret(pretCuDiscount);
                        produseReduse++;
                    }
                }
            }
            System.out.println("Happy Hour activ. (17:00 - 19:00)");
            System.out.printf("Discount 20%% aplicat pentru %d bauturi alcoolice.%n", produseReduse);
        } else {
            System.out.println("Nu este in intervalul de Happy Hour (17:00 - 19:00).");
            System.out.println("Nu s-a aplicat niciun discount.");
        }
    }

}