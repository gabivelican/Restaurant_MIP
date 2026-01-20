package ro.unitbv.restaurant;

import ro.unitbv.restaurant.model.Product;
import java.util.Map;

@FunctionalInterface
public interface DiscountRule {
    double apply(double totalWithVAT, Map<Product, Integer> items);
}
