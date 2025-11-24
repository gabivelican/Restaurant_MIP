package ro.unitbv.restaurant;

import java.util.LinkedHashMap;
import java.util.Map;

public class Order {

    private static final double VAT_RATE = 0.09;

    private final Map<Product, Integer> items = new LinkedHashMap<>();

    public void addProduct(Product product, int quantity) {
        if (quantity <= 0) return;
        items.merge(product, quantity, Integer::sum);
    }

    public double calculateTotalWithoutVAT() {
        return items.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                .sum();
    }

    public double calculateTotalWithVAT() {
        double base = calculateTotalWithoutVAT();
        return base + base * VAT_RATE;
    }

    public double calculateTotalWithDiscount(DiscountRule rule) {
        double totalWithVAT = calculateTotalWithVAT();
        return rule.apply(totalWithVAT, items);
    }
}
