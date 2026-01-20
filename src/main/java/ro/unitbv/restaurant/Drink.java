package ro.unitbv.restaurant.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DRINK")
public class Drink extends Product {

    private int volume;

    public Drink() {}

    public Drink(String name, double price, int volume) {
        super(name, price);
        this.volume = volume;
    }

    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }

    @Override
    public String getDetails() {
        return "Volume: " + volume + "ml";
    }
}