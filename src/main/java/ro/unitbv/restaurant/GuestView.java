package ro.unitbv.restaurant;

import ro.unitbv.restaurant.model.Product;
import ro.unitbv.restaurant.model.Food;
import ro.unitbv.restaurant.model.Drink;
import ro.unitbv.restaurant.repository.ProductRepository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GuestView {

    private final ProductRepository productRepo = new ProductRepository();
    private final ObservableList<Product> masterList = FXCollections.observableArrayList();
    private final ObservableList<Product> filteredList = FXCollections.observableArrayList();

    public Scene createScene(Stage stage, Runnable onBack) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // 1. Lista din Stanga
        ListView<Product> listView = new ListView<>(filteredList);
        listView.setPrefWidth(300);
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " - " + item.getPrice() + " RON");
            }
        });
        root.setLeft(listView);

        // 2. Zona de Detalii (Centru)
        Label details = new Label("Selectează un produs pentru detalii");
        details.setWrapText(true);
        root.setCenter(details);

        // Listener pentru selecție
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                details.setText("Selectează un produs pentru detalii");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("NUME: ").append(newV.getName()).append("\n");
                sb.append("PRET: ").append(newV.getPrice()).append(" RON\n");

                // Verificam tipul ca sa stim ce detalii afisam
                if (newV instanceof Food f) {
                    sb.append("GRAMAJ: ").append(f.getWeight()).append(" g\n");
                    sb.append("VEGETARIAN: ").append(f.isVegetarian() ? "Da" : "Nu");
                } else if (newV instanceof Drink d) {
                    sb.append("VOLUM: ").append(d.getVolume()).append(" ml\n");
                }

                details.setText(sb.toString());
            }
        });

        // 3. Zona de Filtre (Sus)
        GridPane filters = new GridPane();
        filters.setHgap(10);
        filters.setVgap(8);
        filters.setPadding(new Insets(8));

        CheckBox cbVegetarian = new CheckBox("Vegetarian");
        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll("Toate", "Mancare", "Bautura");
        cbType.setValue("Toate");

        TextField tfMinPrice = new TextField();
        tfMinPrice.setPromptText("Min");
        TextField tfMaxPrice = new TextField();
        tfMaxPrice.setPromptText("Max");

        Button btnApply = new Button("Aplică Filtre");

        TextField tfSearch = new TextField();
        tfSearch.setPromptText("Caută...");

        filters.add(new Label("Caută:"), 0, 0);
        filters.add(tfSearch, 1, 0, 3, 1);
        filters.add(cbVegetarian, 0, 1);
        filters.add(new Label("Tip:"), 1, 1);
        filters.add(cbType, 2, 1);
        filters.add(new Label("Preț:"), 0, 2);
        HBox priceBox = new HBox(5, tfMinPrice, new Label("-"), tfMaxPrice, btnApply);
        filters.add(priceBox, 1, 2, 3, 1);

        root.setTop(filters);

        // Bottom: back button
        Button btnBack = new Button("Înapoi");
        btnBack.setOnAction(e -> onBack.run());
        HBox bottom = new HBox(btnBack);
        bottom.setPadding(new Insets(8));
        root.setBottom(bottom);

        // Load products from repository
        loadProductsFromRepo();

        // initial filtered list
        filteredList.setAll(masterList);

        // Filtering function
        Runnable applyFilters = () -> {
            String search = tfSearch.getText() == null ? "" : tfSearch.getText().trim().toLowerCase(Locale.ROOT);
            boolean veg = cbVegetarian.isSelected();
            String type = cbType.getValue();
            Double min = parseDoubleOrNull(tfMinPrice.getText());
            Double max = parseDoubleOrNull(tfMaxPrice.getText());

            Predicate<Product> predicate = p -> true;
            if (!search.isEmpty()) {
                predicate = predicate.and(p -> p.getName().toLowerCase(Locale.ROOT).contains(search));
            }
            if (veg) {
                predicate = predicate.and(p -> (p instanceof Food) && ((Food) p).isVegetarian());
            }
            if (type != null && !type.equals("Toate")) {
                if (type.equals("Mancare")) {
                    predicate = predicate.and(p -> p instanceof Food);
                } else if (type.equals("Bautura")) {
                    predicate = predicate.and(p -> p instanceof Drink);
                }
            }
            if (min != null) {
                predicate = predicate.and(p -> p.getPrice() >= min);
            }
            if (max != null) {
                predicate = predicate.and(p -> p.getPrice() <= max);
            }

            List<Product> result = masterList.stream().filter(predicate).collect(Collectors.toList());
            filteredList.setAll(result);
        };

        btnApply.setOnAction(e -> applyFilters.run());

        // real-time search
        tfSearch.addEventHandler(KeyEvent.KEY_RELEASED, e -> applyFilters.run());

        Scene scene = new Scene(root, 900, 600);
        return scene;
    }

    private void loadProductsFromRepo() {
        masterList.clear();
        try {
            List<Product> products = productRepo.getAllProducts();
            masterList.addAll(products);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Double parseDoubleOrNull(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return Double.parseDouble(s.trim()); } catch (NumberFormatException e) { return null; }
    }
}