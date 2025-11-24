package ro.unitbv.restaurant;

public final class Food extends Product {

    private final int weight;

    public Food(String name, double price, int weight, boolean vegetarian) {
        super(name, price, vegetarian);
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return super.toString() + " - Weight: " + weight + "g";
    }
}
