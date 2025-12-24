package mip.restaurantfx;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class ComandaRepository {

    private EntityManager getEntityManager() {
        // Asigură-te că PersistenceManager e configurat corect în proiectul tău
        return PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    // Salvare sau Update (Merge)
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

    /**
     * Caută comanda activă (DESCHISA) pentru o anumită masă.
     * Folosește JOIN FETCH pentru a aduce și produsele într-un singur query.
     */
    public Comanda getComandaActiva(Long masaId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT c FROM Comanda c " +
                    "LEFT JOIN FETCH c.items i " +   // Aduce lista de items
                    "LEFT JOIN FETCH i.produs " +    // Aduce detaliile produselor (nume, pret)
                    "WHERE c.masa.id = :masaId " +
                    "AND c.status = 'DESCHISA'";

            return em.createQuery(jpql, Comanda.class)
                    .setParameter("masaId", masaId)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null; // Masa este liberă
        } finally {
            em.close();
        }
    }

    /**
     * Obține istoricul comenzilor pentru un anumit ospătar.
     * Include detalii despre produse și liniile de discount.
     */
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

    /**
     * Obține istoricul global al comenzilor plătite.
     * Include detalii despre produse, linii de discount, ospătar și masă.
     */
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
}