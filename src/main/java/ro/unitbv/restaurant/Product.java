package ro.unitbv.restaurant;

public sealed abstract class Product permits Food, Drink, Pizza {

    private final String name;
    private final double price;
    private final boolean vegetarian;

    protected Product(String name, double price, boolean vegetarian) {
        this.name = name;
        this.price = price;
        this.vegetarian = vegetarian;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    @Override
    public String toString() {
        return name + " - " + price + " RON";
    }
}
