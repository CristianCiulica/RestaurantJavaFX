package mip.restaurantfx;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class UserRepository {

    private EntityManager getEntityManager() {
        return PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    public Optional<User> login(String username, String password) {
        EntityManager em = getEntityManager();
        try {
            // Cautam un user care are username-ul SI parola specificata
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :user AND u.password = :pass", User.class);
            query.setParameter("user", username);
            query.setParameter("pass", password);

            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty(); // User sau parola gresite
        } finally {
            em.close();
        }
    }

    public void save(User user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Optional<User> findByUsername(String username) {
        EntityManager em = getEntityManager();
        try {
            return Optional.of(
                    em.createQuery("SELECT u FROM User u WHERE u.username = :user", User.class)
                            .setParameter("user", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Sterge userul cu username-ul dat (daca exista). Returneaza true daca s-a sters.
     */
    public boolean deleteByUsername(String username) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            var userOpt = findByUsername(username);
            if (userOpt.isEmpty()) {
                em.getTransaction().rollback();
                return false;
            }

            // trebuie re-atasat la EM curent
            User managed = em.find(User.class, userOpt.get().getId());
            if (managed == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(managed);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Returneaza toti userii cu rolul cerut (ex: STAFF) sortati alfabetic dupa nume.
     */
    public List<User> findByRole(User.Role role) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.rol = :rol ORDER BY u.nume", User.class)
                    .setParameter("rol", role)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<User> getAllStaff() {
        return findByRole(User.Role.STAFF);
    }

    public long count() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}