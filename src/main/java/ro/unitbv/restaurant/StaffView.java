package ro.unitbv.restaurant;

import ro.unitbv.restaurant.model.*;
import ro.unitbv.restaurant.repository.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StaffView {

    private final TableRepository tableRepo = new TableRepository();
    private final ProductRepository productRepo = new ProductRepository();
    private final OrderRepository orderRepo = new OrderRepository();

    private final User currentUser;
    private BorderPane root;
    private VBox tablesPanel;
    private VBox orderPanel;

    private RestaurantTable selectedTable;
    private Order currentOrder;
    private ObservableList<OrderItem> orderItemsList = FXCollections.observableArrayList();
    private Label lblTotal;

    public StaffView(User user) {
        this.currentUser = user;
    }

    public Scene createScene(Stage stage, Runnable onLogout) {
        root = new BorderPane();

        Button btnLogout = new Button("Logout (" + currentUser.getUsername() + ")");
        btnLogout.setOnAction(e -> onLogout.run());

        Button btnBackToTables = new Button("Înapoi la Mese");
        btnBackToTables.setOnAction(e -> showTablesView());

        HBox top = new HBox(10, btnBackToTables, new Region(), btnLogout);
        HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);
        top.setPadding(new Insets(10));
        top.setStyle("-fx-background-color: #ddd;");
        root.setTop(top);

        initTablesPanel();
        initOrderPanel();
        showTablesView();

        return new Scene(root, 1000, 700);
    }

    private void showTablesView() {
        refreshTables();
        root.setCenter(tablesPanel);
    }

    private void initTablesPanel() {
        tablesPanel = new VBox(20);
        tablesPanel.setPadding(new Insets(20));
    }

    private void refreshTables() {
        tablesPanel.getChildren().clear();
        tablesPanel.getChildren().add(new Label("Harta Meselor - Selectează o masă:"));

        TilePane tiles = new TilePane();
        tiles.setHgap(20);
        tiles.setVgap(20);

        List<RestaurantTable> tables = tableRepo.getAllTables();

        for (RestaurantTable t : tables) {
            String status = t.isOccupied() ? "OCUPAT" : "LIBER";
            Button btn = new Button(t.getName() + "\n(" + t.getSeats() + " locuri)\n" + status);
            btn.setPrefSize(140, 100);

            if (t.isOccupied()) {
                btn.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-font-weight: bold;");
            } else {
                btn.setStyle("-fx-background-color: #ccffcc; -fx-border-color: green;");
            }

            btn.setOnAction(e -> openTableOrder(t));
            tiles.getChildren().add(btn);
        }
        tablesPanel.getChildren().add(tiles);
    }

    // --- LOGICA DE DESCHIDERE MASA (IMPORTANTA) ---
    private void openTableOrder(RestaurantTable table) {
        this.selectedTable = table;
        this.orderItemsList.clear();

        if (table.isOccupied()) {
            // Daca e ocupata, cautam comanda existenta in DB
            Order existingOrder = orderRepo.findOpenOrderByTable(table.getId());

            if (existingOrder != null) {
                // Am gasit-o! O incarcam.
                this.currentOrder = existingOrder;
                // Populam lista vizuala cu produsele din baza de date
                this.orderItemsList.setAll(existingOrder.getItems());
            } else {
                // E ocupata teoretic, dar nu are comanda OPEN (caz de eroare/fallback) -> facem una noua
                createNewOrder(table);
            }
        } else {
            // E libera -> facem comanda noua
            createNewOrder(table);
        }

        root.setCenter(orderPanel);
        updateTotal();
    }

    private void createNewOrder(RestaurantTable table) {
        currentOrder = new Order();
        currentOrder.setTable(table);
        currentOrder.setUser(currentUser);
        currentOrder.setDate(LocalDateTime.now());
        currentOrder.setStatus("OPEN");
        currentOrder.setItems(new ArrayList<>());
    }
    // ----------------------------------------------

    private void initOrderPanel() {
        ListView<Product> menuList = new ListView<>();
        try {
            menuList.getItems().addAll(productRepo.getAllProducts());
        } catch (Exception e) { e.printStackTrace(); }

        menuList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName() + " - " + item.getPrice() + " RON");
            }
        });

        Button btnAdd = new Button("Adaugă în Coș ->");
        btnAdd.setOnAction(e -> {
            Product p = menuList.getSelectionModel().getSelectedItem();
            if (p != null) addItemToOrder(p);
        });

        VBox left = new VBox(10, new Label("MENIU DISPONIBIL"), menuList, btnAdd);
        left.setPadding(new Insets(10));

        ListView<String> cartList = new ListView<>();
        lblTotal = new Label("Total: 0 RON");
        lblTotal.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: blue;");

        Button btnFinish = new Button("Finalizează Comanda & Eliberează Masa");
        btnFinish.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        btnFinish.setOnAction(e -> finishOrder());

        VBox right = new VBox(10, new Label("COMANDA CURENTĂ"), cartList, lblTotal, btnFinish);
        right.setPadding(new Insets(10));
        HBox.setHgrow(right, Priority.ALWAYS);

        orderItemsList.addListener((javafx.beans.Observable o) -> {
            cartList.getItems().clear();
            for (OrderItem item : orderItemsList) {
                cartList.getItems().add(item.getProduct().getName() + " x " + item.getQuantity() + " buc");
            }
            updateTotal();
        });

        SplitPane split = new SplitPane(left, right);
        split.setDividerPositions(0.4);
        orderPanel = new VBox(split);
        VBox.setVgrow(split, Priority.ALWAYS);
    }

    // --- LOGICA DE ADAUGARE SI SALVARE INCREMENTALA ---
    private void addItemToOrder(Product p) {
        // 1. Marcam masa ocupata daca nu e deja
        if (!selectedTable.isOccupied()) {
            selectedTable.setOccupied(true);
            tableRepo.save(selectedTable);
        }

        // 2. Adaugam produsul in lista
        boolean found = false;
        for (OrderItem item : orderItemsList) {
            if (item.getProduct().getId().equals(p.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            OrderItem newItem = new OrderItem();
            newItem.setProduct(p);
            newItem.setQuantity(1);
            newItem.setOrder(currentOrder);
            orderItemsList.add(newItem);
            currentOrder.getItems().add(newItem);
        }

        // 3. IMPORTANT: Actualizam lista si salvam in DB acum!
        // Fortam refresh la lista observabila
        ObservableList<OrderItem> temp = FXCollections.observableArrayList(orderItemsList);
        orderItemsList.setAll(temp);

        // Salvam comanda ca sa nu se piarda daca dam "Inapoi"
        orderRepo.save(currentOrder);
    }
    // --------------------------------------------------

    private void updateTotal() {
        double subtotal = 0;
        for (OrderItem item : orderItemsList) {
            subtotal += item.getProduct().getPrice() * item.getQuantity();
        }

        double discount = 0;
        long drinkCount = orderItemsList.stream()
                .filter(i -> i.getProduct() instanceof Drink)
                .mapToInt(OrderItem::getQuantity).sum();

        if (drinkCount >= 2) {
            discount += 5.0; // Happy Hour simplificat
        }

        List<Product> pizzas = new ArrayList<>();
        for (OrderItem item : orderItemsList) {
            if (item.getProduct() instanceof Food && item.getProduct().getName().toLowerCase().contains("pizza")) {
                for(int i=0; i<item.getQuantity(); i++) pizzas.add(item.getProduct());
            }
        }
        if (pizzas.size() >= 4) {
            pizzas.sort(Comparator.comparingDouble(Product::getPrice));
            discount += pizzas.get(0).getPrice(); // Cea mai ieftina gratis
        }

        double finalTotal = subtotal - discount;
        lblTotal.setText(String.format("Subtotal: %.2f RON\nReducere: -%.2f RON\nTOTAL FINAL: %.2f RON", subtotal, discount, finalTotal));
        currentOrder.setTotal(finalTotal);
    }

    private void finishOrder() {
        if (orderItemsList.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Coșul este gol!").show();
            return;
        }
        try {
            // Eliberam masa
            selectedTable.setOccupied(false);
            tableRepo.save(selectedTable);

            // Inchidem comanda
            currentOrder.setStatus("CLOSED");
            currentOrder.setItems(new ArrayList<>(orderItemsList));
            orderRepo.save(currentOrder);

            new Alert(Alert.AlertType.INFORMATION, "Comanda salvată cu succes!").show();
            showTablesView();

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Eroare la salvare: " + ex.getMessage()).show();
        }
    }
}