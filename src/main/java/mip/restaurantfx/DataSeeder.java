package mip.restaurantfx;

public class DataSeeder {
    public static void seed() {
        UserRepository userRepo = new UserRepository();
        MasaRepository masaRepo = new MasaRepository();
        ProdusRepository produsRepo = new ProdusRepository(); // Avem nevoie si de asta

        System.out.println("--- Verificare si populare baza de date ---");

        // 1. SEED UTILIZATORI (Daca nu exista)
        if (userRepo.count() == 0) {
            System.out.println("Seeding: Creare utilizatori initiali...");
            userRepo.save(new User("admin", "admin", "Seful Andrei", User.Role.ADMIN));
            userRepo.save(new User("staff", "1234", "Ospatar George", User.Role.STAFF));
            // Guest nu se salveaza in DB, e doar logic
        }

        // 2. SEED MESE (Daca nu exista)
        if (masaRepo.count() == 0) {
            System.out.println("Seeding: Creare mese restaurant...");
            for (int i = 1; i <= 10; i++) {
                int locuri = (i <= 4) ? 4 : 2;
                masaRepo.save(new Masa(i, locuri));
            }
        }

        // 3. SEED MENIU (Preluat din vechiul Main.java)
        // Verificam daca meniul e gol inainte sa adaugam, ca sa nu le dublam
        if (produsRepo.getAll().isEmpty()) {
            System.out.println("Seeding: Populare meniu initial...");

            // Fel Principal
            produsRepo.salveazaProdus(new Mancare("Pizza Margherita", 45.0, 450, true));
            produsRepo.salveazaProdus(new Mancare("Supa Crema Ciuperci", 22.0, 300, true));
            produsRepo.salveazaProdus(new Mancare("Risotto cu Hribii de munte", 48.0, 320, true));
            produsRepo.salveazaProdus(new Mancare("Paste Carbonara", 52.5, 400, false));
            produsRepo.salveazaProdus(new Mancare("Burger Gourmet Black Angus", 62.0, 380, false));

            // Desert
            produsRepo.salveazaProdus(new Mancare("Salata de Fructe", 18.0, 200, true));
            produsRepo.salveazaProdus(new Mancare("Panna Cotta cu fructe de padure", 28.0, 150, true));
            produsRepo.salveazaProdus(new Mancare("Tiramisu Special", 120.0, 250, false));
            produsRepo.salveazaProdus(new Mancare("Cheesecake Vanilie", 32.0, 180, false));

            // Bauturi
            produsRepo.salveazaProdus(new Bautura("Limonada", 15.0, 400, false));
            produsRepo.salveazaProdus(new Bautura("Apa Plata", 8.0, 500, false));
            produsRepo.salveazaProdus(new Bautura("Fresh Portocale", 19.0, 450, false));
            produsRepo.salveazaProdus(new Bautura("Ceai Verde cu miere", 14.0, 350, false));
            produsRepo.salveazaProdus(new Bautura("Bere", 12.0, 500, true));
            produsRepo.salveazaProdus(new Bautura("Vin Rosu", 28.0, 150, true));
            produsRepo.salveazaProdus(new Bautura("Aperol Spritz", 35.0, 250, true));
            produsRepo.salveazaProdus(new Bautura("Gin Tonic", 38.0, 300, true));

            // Pizza Custom (Builder Pattern)
            Pizza pizzaCustom = new Pizza.Builder("Pufos", "Rosii")
                    .addTopping("Mozzarella")
                    .build();
            // Atentie: Numele trebuie sa fie unic sau tratat diferit, aici il lasam generic
            pizzaCustom.setNume("Pizza Custom Mozzarella");
            produsRepo.salveazaProdus(pizzaCustom);

            Pizza pizzaVeggie = new Pizza.Builder("Subtire", "Busuioc")
                    .addTopping("Ciuperci")
                    .addTopping("Extra Mozzarella")
                    .build();
            pizzaVeggie.setNume("Pizza Custom Veggie");
            produsRepo.salveazaProdus(pizzaVeggie);
        }
    }
}