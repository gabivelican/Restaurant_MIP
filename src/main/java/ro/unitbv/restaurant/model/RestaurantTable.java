package ro.unitbv.restaurant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_table")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private boolean occupied;

    @Column(nullable = false)
    private int seats;

    @Column(nullable = false)
    private String name;

    // No-arg constructor for JPA
    public RestaurantTable() {
    }

    public RestaurantTable(String name, int seats, boolean occupied) {
        this.name = name;
        this.seats = seats;
        this.occupied = occupied;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

