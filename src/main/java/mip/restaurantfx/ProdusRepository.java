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
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void stergeToateProdusele() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Produs").executeUpdate();
            em.getTransaction().commit();
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