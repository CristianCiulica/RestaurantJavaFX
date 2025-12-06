module mip.restaurantfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens mip.restaurantfx;

    exports mip.restaurantfx;
}