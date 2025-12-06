module mip.restaurantfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    opens mip.restaurantfx to javafx.fxml, com.fasterxml.jackson.databind;
    exports mip.restaurantfx;
}
