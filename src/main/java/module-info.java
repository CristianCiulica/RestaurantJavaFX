module mip.restaurantfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires java.sql;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    exports mip.restaurantfx;

    opens mip.restaurantfx to javafx.graphics, javafx.fxml, org.hibernate.orm.core, com.fasterxml.jackson.databind;
}