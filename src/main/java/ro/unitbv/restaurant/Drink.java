package ro.unitbv.restaurant;

public final class Drink extends Product {

    private final int volume;
    private final boolean alcoholic;

    public Drink(String name, double price, int volume, boolean alcoholic, boolean vegetarian) {
        super(name, price, vegetarian);
        this.volume = volume;
        this.alcoholic = alcoholic;
    }

    public int getVolume() {
        return volume;
    }

    public boolean isAlcoholic() {
        return alcoholic;
    }

    @Override
    public String toString() {
        return super.toString() + " - Volume: " + volume + "ml";
    }
}
