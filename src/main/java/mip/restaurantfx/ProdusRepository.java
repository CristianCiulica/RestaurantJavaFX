package mip.restaurantfx;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ProdusRepository {
    private EntityManagerFactory entityManagerFactory;

    public ProdusRepository() {
        entityManagerFactory = Persistence.createEntityManagerFactory("restaurantPU");
    }

    public List<Produs> getAll() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produs p", Produs.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void salveazaProdus(Produs p) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            if (p.getId() == null) {
                em.persist(p);
            } else {
                em.merge(p);
            }
            em.getTransaction().commit();
            System.out.println("Successfully saved: " + p.getNume() + " (ID: " + p.getId() + ")");
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("ERROR saving product: " + p.getNume() + " - " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void stergeToateProdusele() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM pizza_toppinguri").executeUpdate();
            em.createQuery("DELETE FROM Produs").executeUpdate();

            em.getTransaction().commit();
            System.out.println("All products deleted successfully.");
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("Error deleting products: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void addProdus(Produs p) {
        salveazaProdus(p);
    }

    public void inchide() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}