package ro.unitbv.restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Pizza extends Product {

    private final String dough;
    private final String sauce;
    private final List<String> toppings;

    private Pizza(Builder builder) {
        super(builder.name, builder.price, builder.vegetarian);
        this.dough = builder.dough;
        this.sauce = builder.sauce;
        this.toppings = List.copyOf(builder.toppings);
    }

    public String getDough() { return dough; }
    public String getSauce() { return sauce; }
    public List<String> getToppings() { return Collections.unmodifiableList(toppings); }

    @Override
    public String toString() {
        return getName() + " - " + getPrice() + " RON - Dough: " + dough +
                ", Sauce: " + sauce + ", Toppings: " + toppings;
    }

    public static class Builder {

        private final String name;
        private final double price;
        private final String dough;
        private final String sauce;

        List<String> toppings = new ArrayList<>();
        boolean vegetarian = true;

        public Builder(String name, double price, String dough, String sauce) {
            this.name = name;
            this.price = price;
            this.dough = dough;
            this.sauce = sauce;
        }

        public Builder addTopping(String topping) {
            toppings.add(topping);

            String t = topping.toLowerCase();
            if (t.contains("bacon") || t.contains("salam") || t.contains("ham") || t.contains("meat")) {
                vegetarian = false;
            }

            return this;
        }

        public Pizza build() {
            return new Pizza(this);
        }
    }
}
