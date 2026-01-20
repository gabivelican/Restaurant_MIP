package ro.unitbv.restaurant.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import ro.unitbv.restaurant.model.RestaurantTable;

import java.util.List;

public class TableRepository {

    private final EntityManagerFactory emf;

    public TableRepository() {
        this.emf = Persistence.createEntityManagerFactory("restaurantPU");
    }

    public List<RestaurantTable> getAllTables() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT t FROM RestaurantTable t", RestaurantTable.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public RestaurantTable save(RestaurantTable table) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            if (table.getId() == null) {
                em.persist(table);
            } else {
                table = em.merge(table);
            }
            tx.commit();
            return table;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void close() {
        emf.close();
    }
}

