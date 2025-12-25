package mip.restaurantfx;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class ComandaRepository {

    private EntityManager getEntityManager() {
        return PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    public void save(Comanda comanda) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (comanda.getId() == null) {
                em.persist(comanda);
            } else {
                em.merge(comanda);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public Comanda getComandaActiva(Long masaId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT c FROM Comanda c " +
                    "LEFT JOIN FETCH c.items i " +
                    "LEFT JOIN FETCH i.produs " +
                    "WHERE c.masa.id = :masaId " +
                    "AND c.status = 'DESCHISA'";

            return em.createQuery(jpql, Comanda.class)
                    .setParameter("masaId", masaId)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Comanda> getIstoricOspatar(Long ospatarId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT DISTINCT c FROM Comanda c " +
                    "LEFT JOIN FETCH c.items i " +
                    "LEFT JOIN FETCH i.produs " +
                    "LEFT JOIN FETCH c.discountLines d " +
                    "WHERE c.ospatar.id = :ospatarId AND c.status = 'PLATITA' " +
                    "ORDER BY c.dataCreare DESC";

            return em.createQuery(jpql, Comanda.class)
                    .setParameter("ospatarId", ospatarId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Comanda> getIstoricGlobal() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT DISTINCT c FROM Comanda c " +
                    "LEFT JOIN FETCH c.items i " +
                    "LEFT JOIN FETCH i.produs " +
                    "LEFT JOIN FETCH c.discountLines d " +
                    "LEFT JOIN FETCH c.ospatar o " +
                    "LEFT JOIN FETCH c.masa m " +
                    "WHERE c.status = 'PLATITA' " +
                    "ORDER BY c.dataCreare DESC";

            return em.createQuery(jpql, Comanda.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void deleteAll() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<Comanda> all = em.createQuery("SELECT c FROM Comanda c", Comanda.class).getResultList();
            for (Comanda c : all) {
                em.remove(c);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}