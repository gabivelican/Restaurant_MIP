package ro.unitbv.restaurant.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import ro.unitbv.restaurant.model.Order;
import java.util.List;

public class OrderRepository {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");

    // --- FIX FINAL: Ștergere "Braț la Braț" (Safe Delete) ---
    // Aceasta metoda sterge comenzile una cate una, ca sa stearga si produsele din ele
    public void deleteOrdersByUserId(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Găsim toate comenzile acestui user
            List<Order> orders = em.createQuery("SELECT o FROM Order o WHERE o.user.id = :uid", Order.class)
                    .setParameter("uid", userId)
                    .getResultList();

            // 2. Le ștergem una câte una
            // Asta activează "Cascade", deci Hibernate șterge întâi produsele din comandă, apoi comanda
            for (Order o : orders) {
                em.remove(o);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            // Aruncăm eroarea ca să apară în alertă dacă tot nu merge
            throw new RuntimeException("Eroare la ștergerea comenzilor: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    // --------------------------------------------------------

    public Order findOpenOrderByTable(Integer tableId) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> found = em.createQuery(
                            "SELECT o FROM Order o WHERE o.table.id = :tid AND o.status = 'OPEN'", Order.class)
                    .setParameter("tid", tableId)
                    .getResultList();

            if (found.isEmpty()) {
                return null;
            } else {
                return found.get(0);
            }
        } finally {
            em.close();
        }
    }

    public List<Order> getAllOrders() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT o FROM Order o ORDER BY o.date DESC", Order.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void save(Order order) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (order.getId() == null) {
                em.persist(order);
            } else {
                em.merge(order);
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
}