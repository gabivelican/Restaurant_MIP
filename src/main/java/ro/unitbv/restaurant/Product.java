package ro.unitbv.restaurant.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import javafx.beans.property.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product_type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Food.class, name = "FOOD"),
        @JsonSubTypes.Type(value = Drink.class, name = "DRINK")
})
public abstract class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id; // <--- MODIFICAT: Integer (obiect), nu int

    @Column(name = "name")
    protected String name;

    @Column(name = "price")
    protected double price;

    @Transient
    private StringProperty nameProperty;
    @Transient
    private DoubleProperty priceProperty;

    public Product() {}

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    // --- METODELE NOI CARE LIPSEAU ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    // ---------------------------------

    // Getteri și Setteri adaptați pentru JavaFX
    public String getName() { return nameProperty().get(); }
    public void setName(String name) {
        this.name = name;
        this.nameProperty().set(name);
    }

    public double getPrice() { return priceProperty().get(); }
    public void setPrice(double price) {
        this.price = price;
        this.priceProperty().set(price);
    }

    public StringProperty nameProperty() {
        if (nameProperty == null) nameProperty = new SimpleStringProperty(this, "name", name);
        return nameProperty;
    }

    public DoubleProperty priceProperty() {
        if (priceProperty == null) priceProperty = new SimpleDoubleProperty(this, "price", price);
        return priceProperty;
    }

    public abstract String getDetails();
}