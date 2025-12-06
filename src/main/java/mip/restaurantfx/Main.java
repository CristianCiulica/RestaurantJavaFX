package mip.restaurantfx;
import java.util.Scanner;
import java.util.OptionalDouble;
import java.util.List;
import java.util.Optional;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ObjectMapper mapper = new ObjectMapper();
        RestaurantConfig config = new RestaurantConfig();

        config.setNumeRestaurant("La Andrei (Safe Mode)");
        config.setTva(0.09);
        try {
            File configFile = new File("config.json");
            if (!configFile.exists()) {
                throw new java.io.FileNotFoundException("Fisierul 'config.json' nu a fost gasit.");
            }
            config = mapper.readValue(configFile, RestaurantConfig.class);
            System.out.println("Configurare incarcata cu succes!");
        } catch (java.io.FileNotFoundException e) {
            System.out.println("ATENTIE: " + e.getMessage());
            System.out.println("Sistemul va porni cu valorile implicite.");
        } catch (IOException e) {
            System.out.println("EROARE CRITICA: Fisierul de configurare este corupt sau ilizibil.");
            System.out.println("Detalii tehnice: " + e.getMessage());
            System.out.println("Sistemul va porni cu valorile implicite.");
        } catch (Exception e) {
            System.out.println("Eroare neasteptata: " + e.getMessage());
        }

        System.out.println("\n--- Bine ati venit " + config.getNumeRestaurant() + " ------------");
        Meniu meniu = new Meniu();

        meniu.adaugaProdus("Fel Principal", new Mancare("Pizza Margherita", 45.0, 450, true));
        meniu.adaugaProdus("Fel Principal", new Mancare("Supa Crema Ciuperci", 22.0, 300, true));
        meniu.adaugaProdus("Desert", new Mancare("Salata de Fructe", 18.0, 200, true));

        meniu.adaugaProdus("Fel Principal", new Mancare("Paste Carbonara", 52.5, 400, false));
        meniu.adaugaProdus("Desert", new Mancare("Tiramisu Special", 120.0, 250, false));

        meniu.adaugaProdus("Bauturi Racoritoare", new Bautura("Limonada", 15.0, 400, false));
        meniu.adaugaProdus("Bauturi Racoritoare", new Bautura("Apa Plata", 8.0, 500, false));
        meniu.adaugaProdus("Bauturi Alcoolice", new Bautura("Bere", 12.0, 500, true));
        meniu.adaugaProdus("Bauturi Alcoolice", new Bautura("Vin Rosu", 28.0, 150, true));

        Pizza pizzaCustom = new Pizza.PizzaBuilder("Pufos", "Rosii")
                .withExtraMozzarella()
                .withCiuperci()
                .withSalam()
                .build();
        meniu.adaugaProdus("Pizza Custom", pizzaCustom);

        meniu.afiseazaMeniu();

        System.out.println("\n--- [ADMIN] Statistici si Interogari ---");
        List<Mancare> vegetariene = meniu.getPreparateVegetarieneSortate();
        System.out.println("Preparate vegetariene (Sortate alfabetic):");
        if (vegetariene.isEmpty()) {
            System.out.println("Nu exista preparate vegetariene.");
        } else {
            for (Mancare m : vegetariene) {
                m.afisareProdus();
            }
        }

        OptionalDouble pretMediu = meniu.getPretMediuCategorie("Desert");
        if(pretMediu.isPresent()) {
            System.out.printf("Pret mediu Desert: %.2f RON%n", pretMediu.getAsDouble());
        }

        boolean existaScumpe = !meniu.getProdusePestePret(100.0).isEmpty();
        System.out.println("Exista produse peste 100 RON? " + (existaScumpe ? "DA" : "NU"));

        System.out.println("\n[ADMIN] Doriti exportul meniului in format JSON? (da/nu)");
        if (scanner.hasNext()) {
            String raspuns = scanner.next();
            if (raspuns.equalsIgnoreCase("da")) {
                meniu.exportaMeniuInFisier("meniu_export.json");
            }
        }

        System.out.println("\n--- Creare Comanda Noua ---");
        Comanda comanda = new Comanda(config.getTva());

        adaugaProdusInComanda(meniu, comanda, "Pizza Margherita", 2);
        adaugaProdusInComanda(meniu, comanda, "Limonada", 3);
        adaugaProdusInComanda(meniu, comanda, "Vin Rosu", 1);
        adaugaProdusInComanda(meniu, comanda, pizzaCustom.getNume(), 1);
        adaugaProdusInComanda(meniu, comanda, "Burger", 1);

        System.out.println("\n--- Nota de plata initiala ---");
        comanda.calculeazaTotal();

        System.out.print("\nIntroduceti ora curenta pentru verificare oferte (0-23): ");
        int oraCurenta = scanner.nextInt();

        DiscountRule regulaHappyHour = new HappyHourDiscount(oraCurenta);
        comanda.aplicaDiscountRule(regulaHappyHour);

        System.out.println("\n--- Nota de plata FINAL ---");
        comanda.calculeazaTotal();
    }

    public static void adaugaProdusInComanda(Meniu meniu, Comanda comanda, String numeProdus, int cantitate) {
        Optional<Produs> produsGasit = meniu.cautaProdus(numeProdus);

        if (produsGasit.isPresent()) {
            comanda.adaugaProdus(produsGasit.get(), cantitate);
        } else {
            System.out.println("INFO: Produsul '" + numeProdus + "' nu a fost gasit in meniu.");
        }
    }
}