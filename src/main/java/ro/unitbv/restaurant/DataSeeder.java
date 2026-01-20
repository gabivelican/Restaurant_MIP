package ro.unitbv.restaurant;

import ro.unitbv.restaurant.model.User;
import ro.unitbv.restaurant.model.UserRole;
import ro.unitbv.restaurant.model.RestaurantTable;
import ro.unitbv.restaurant.repository.UserRepository;
import ro.unitbv.restaurant.repository.TableRepository;

import java.util.List;
import java.util.Optional;

public class DataSeeder {

    private final UserRepository userRepo;
    private final TableRepository tableRepo;

    public DataSeeder() {
        this.userRepo = new UserRepository();
        this.tableRepo = new TableRepository();
    }

    public void seed() {
        try {
            // Seed users if admin missing
            Optional<User> adminOpt = userRepo.findByUsername("admin");
            if (adminOpt.isEmpty()) {
                User admin = new User("admin", "admin", UserRole.ADMIN);
                userRepo.save(admin);
            }

            Optional<User> staffOpt = userRepo.findByUsername("staff");
            if (staffOpt.isEmpty()) {
                User staff = new User("staff", "1234", UserRole.STAFF);
                userRepo.save(staff);
            }

            // Seed tables if none
            List<RestaurantTable> tables = tableRepo.getAllTables();
            if (tables == null || tables.isEmpty()) {
                for (int i = 1; i <= 5; i++) {
                    RestaurantTable t = new RestaurantTable("Masa " + i, 4, false);
                    tableRepo.save(t);
                }
            }
        } finally {
            // keep repos open for app lifetime; do not close here
        }
    }
}

