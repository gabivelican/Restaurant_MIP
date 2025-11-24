package ro.unitbv.restaurant;

import java.util.Map;

@FunctionalInterface
public interface DiscountRule {
    double apply(double totalWithVAT, Map<Product, Integer> items);
}
