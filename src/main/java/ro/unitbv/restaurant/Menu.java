package ro.unitbv.restaurant;

import java.util.*;
import java.util.stream.Collectors;

public class Menu {

    private final Map<Category, List<Product>> productsByCategory =
            new EnumMap<>(Category.class);

    public void addProduct(Product product, Category category) {
        productsByCategory
                .computeIfAbsent(category, c -> new ArrayList<>())
                .add(product);
    }

    public void printMenu(String restaurantName) {
        System.out.println("---- MENU: " + restaurantName + " ----");
        for (var entry : productsByCategory.entrySet()) {
            System.out.println("[" + entry.getKey() + "]");
            for (Product p : entry.getValue()) {
                System.out.println("  -> " + p);
            }
            System.out.println();
        }
    }

    public void printMenuIteration1(String restaurantName) {
        System.out.println("---  Meniul Restaurantului \"" + restaurantName + "\"  ---");

        productsByCategory.values().stream()
                .flatMap(List::stream)
                .forEach(product -> {
                    if (product instanceof Food f) {
                        System.out.println("> " + product.getName() + " - "
                                + product.getPrice() + " RON - Gramaj: " + f.getWeight() + "g");
                    } else if (product instanceof Drink d) {
                        System.out.println("> " + product.getName() + " - "
                                + product.getPrice() + " RON - Volum: " + d.getVolume() + "ml");
                    }
                });

        System.out.println("---------------------------------------------------------");
    }


    public List<Product> getVegetarianSorted() {
        return productsByCategory.values().stream()
                .flatMap(List::stream)
                .filter(Product::isVegetarian)
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.toList());
    }

    public double getAveragePrice(Category category) {
        return productsByCategory.getOrDefault(category, List.of())
                .stream()
                .mapToDouble(Product::getPrice)
                .average()
                .orElse(0.0);
    }

    public boolean existsProductMoreExpensiveThan(double price) {
        return productsByCategory.values().stream()
                .flatMap(List::stream)
                .anyMatch(p -> p.getPrice() > price);
    }

    public Optional<Product> findProductByName(String name) {
        return productsByCategory.values().stream()
                .flatMap(List::stream)
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public Map<Category, List<Product>> getProducts() {
        return productsByCategory;
    }

}
