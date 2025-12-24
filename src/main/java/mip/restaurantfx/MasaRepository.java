package mip.restaurantfx;

import jakarta.persistence.EntityManager;
import java.util.List;

public class MasaRepository {

    private EntityManager getEntityManager() {
        return PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    public void save(Masa masa) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (masa.getId() == null) {
                em.persist(masa);
            } else {
                em.merge(masa);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Masa> getAllMese() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT m FROM Masa m ORDER BY m.numarMasa", Masa.class).getResultList();
        } finally {
            em.close();
        }
    }

    public long count() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(m) FROM Masa m", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}