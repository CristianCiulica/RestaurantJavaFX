package mip.restaurantfx;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.OptionalDouble;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

public class Meniu {
    Map<String, List<Produs>> categoriiProduse;
    public Meniu() {
        categoriiProduse = new LinkedHashMap<>();
    }

    public void adaugaProdus(String categorie, Produs p) {
        categoriiProduse.computeIfAbsent(categorie, k -> new ArrayList<>()).add(p);
    }

    public void afiseazaMeniu() {
        System.out.println("--- Meniul Restaurantului \"La Andrei\" ---");
        if (categoriiProduse.isEmpty()) {
            System.out.println("Meniul este gol!");
            return;
        }
        for (Map.Entry<String, List<Produs>> categorie : categoriiProduse.entrySet()) {
            System.out.println("\n--- " + categorie.getKey() + " ---");
            List<Produs> produse = new ArrayList<>(categorie.getValue());
            produse.sort(Comparator.comparing(Produs::getNume, String.CASE_INSENSITIVE_ORDER));
            for (Produs p : produse) {
                p.afisareProdus();
            }
        }
        System.out.println("--------------------------------------------");
    }

    public List<Produs> getProduseDinCategorie(String categorie) {
        return categoriiProduse.getOrDefault(categorie, new ArrayList<>());
    }

    public Optional<Produs> cautaProdus(String nume) {
        for (List<Produs> listaProduse : categoriiProduse.values()) {
            for (Produs p : listaProduse) {
                if (p.getNume().equalsIgnoreCase(nume)) {
                    return Optional.of(p);
                }
            }
        }
        return Optional.empty();
    }

    public void exportaMeniuInFisier(String numeFisier) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            mapper.writeValue(new File(numeFisier), this.categoriiProduse);
            System.out.println("Succes: Meniul a fost exportat in fisierul '" + numeFisier + "'.");
        } catch (IOException e) {
            System.out.println("Eroare la scrierea fisierului de export: " + e.getMessage());
        }
    }
    private List<Produs> getAllProduse() {
        List<Produs> toateProdusele = new ArrayList<>();
        for (List<Produs> lista : categoriiProduse.values()) {
            toateProdusele.addAll(lista);
        }
        return toateProdusele;
    }

    public List<Mancare> getPreparateVegetarieneSortate() {
        return getAllProduse().stream()
                .filter(p -> p instanceof Mancare)
                .map(p -> (Mancare) p)
                .filter(mancare -> mancare.isVegetarian())
                .sorted(Comparator.comparing(produs -> produs.getNume()))
                .collect(Collectors.toList());
    }

    public OptionalDouble getPretMediuCategorie(String categorie) {
        List<Produs> produse = categoriiProduse.get(categorie);
        if (produse == null || produse.isEmpty()) {
            return OptionalDouble.empty();
        }

        return produse.stream()
                .mapToDouble(produs -> produs.getPret())
                .average();
    }

    public List<Produs> getProdusePestePret(double pretLimita) {
        return getAllProduse().stream()
                .filter(p -> p.getPret() > pretLimita)
                .collect(Collectors.toList());
    }
}