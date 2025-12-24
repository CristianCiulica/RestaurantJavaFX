package mip.restaurantfx;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
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

    public long count() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}