package mip.restaurantfx;

import jakarta.persistence.EntityManager;
import java.util.List;

public class ProdusRepository {
    private EntityManager getEntityManager() {
        return PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    public List<Produs> getAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produs p", Produs.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Produs> getAllActive() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produs p WHERE p.activ = true ORDER BY p.nume", Produs.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void salveazaProdus(Produs p) {
        EntityManager em = getEntityManager();
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

    public boolean deactivateById(Long id) {
        if (id == null) return false;
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Produs managed = em.find(Produs.class, id);
            if (managed == null) {
                em.getTransaction().rollback();
                return false;
            }
            managed.setActiv(false);
            em.merge(managed);
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public void resetDatabase() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM pizza_toppinguri").executeUpdate();
            em.createNativeQuery("DELETE FROM produs").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE produs_id_seq RESTART WITH 1").executeUpdate();

            em.getTransaction().commit();
            System.out.println("Baza de date a fost resetata complet!");
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }


}