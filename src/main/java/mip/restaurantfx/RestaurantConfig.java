package mip.restaurantfx;
public class RestaurantConfig{
    private String numeRestaurant;
    private double tva;

    public RestaurantConfig() {}

    public String getNumeRestaurant() {
        return numeRestaurant;
    }
    public void setNumeRestaurant(String numeRestaurant) {
        this.numeRestaurant = numeRestaurant;
    }
    public double getTva() {
        return tva;
    }
    public void setTva(double tva) {
        this.tva = tva;
    }
}