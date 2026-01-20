package ro.unitbv.restaurant.repository;

import ro.unitbv.restaurant.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ProductRepository {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");

    public void addProduct(Product p) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Product> getAllProducts() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void deleteAll() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Product").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteProduct(Product p) {
        if (p == null || p.getId() == null) return;
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Product managed = em.find(Product.class, p.getId());
            if (managed != null) {
                em.remove(managed);
            }
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }
}