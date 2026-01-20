package ro.unitbv.restaurant.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FOOD")
public class Food extends Product {

    private int weight;
    private boolean vegetarian; // <--- CAMP NOU

    public Food() {}

    // Constructor actualizat
    public Food(String name, double price, int weight, boolean vegetarian) {
        super(name, price);
        this.weight = weight;
        this.vegetarian = vegetarian;
    }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    // <--- METODA CARE ÎȚI LIPSEA
    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    @Override
    public String getDetails() {
        return "Weight: " + weight + "g" + (vegetarian ? " (Veg)" : "");
    }
}