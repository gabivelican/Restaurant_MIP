package ro.unitbv.restaurant;

// IMPORTURI MODEL
import ro.unitbv.restaurant.model.Product;
import ro.unitbv.restaurant.model.Food;
import ro.unitbv.restaurant.model.Drink;
import ro.unitbv.restaurant.model.User;
import ro.unitbv.restaurant.model.UserRole;
import ro.unitbv.restaurant.model.Order;

// IMPORTURI REPOSITORY
import ro.unitbv.restaurant.repository.ProductRepository;
import ro.unitbv.restaurant.repository.UserRepository;
import ro.unitbv.restaurant.repository.OrderRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AdminView {

    private final ProductRepository productRepo = new ProductRepository();
    private final UserRepository userRepo = new UserRepository();
    private final OrderRepository orderRepo = new OrderRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Scene createScene(Stage stage, Runnable onLogout) {
        TabPane tabPane = new TabPane();

        // TAB 1: Meniu
        Tab tabMenu = new Tab("Gestiune Meniu");
        tabMenu.setContent(createMenuTabContent());
        tabMenu.setClosable(false);

        // TAB 2: Staff
        Tab tabStaff = new Tab("Gestiune Staff");
        tabStaff.setContent(createStaffTabContent());
        tabStaff.setClosable(false);

        // TAB 3: Oferte
        Tab tabOffers = new Tab("Oferte");
        tabOffers.setContent(createOffersTabContent());
        tabOffers.setClosable(false);

        // TAB 4: Istoric
        Tab tabHistory = new Tab("Istoric Comenzi");
        tabHistory.setContent(createHistoryTabContent());
        tabHistory.setClosable(false);

        tabPane.getTabs().addAll(tabMenu, tabStaff, tabOffers, tabHistory);

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> onLogout.run());
        HBox topBar = new HBox(10, new Label("Admin Panel"), btnLogout);
        topBar.setPadding(new Insets(8));
        topBar.setStyle("-fx-background-color: #ddd;");

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabPane);

        return new Scene(root, 1000, 700);
    }

    // --- TAB 1: Menu management ---
    private BorderPane createMenuTabContent() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));

        TableView<Product> table = new TableView<>();

        TableColumn<Product, String> colName = new TableColumn<>("Nume");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(300);

        TableColumn<Product, Double> colPrice = new TableColumn<>("Preț");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setPrefWidth(100);

        TableColumn<Product, String> colType = new TableColumn<>("Tip");
        colType.setCellValueFactory(cell -> {
            Product p = cell.getValue();
            String t = p instanceof Food ? "Mancare" : (p instanceof Drink ? "Bautura" : "Altele");
            return javafx.beans.property.SimpleStringProperty.stringExpression(javafx.beans.binding.Bindings.createStringBinding(() -> t));
        });
        colType.setPrefWidth(120);

        table.getColumns().addAll(colName, colPrice, colType);

        ObservableList<Product> products = FXCollections.observableArrayList();
        table.setItems(products);

        refreshProducts(products);

        // Form add product
        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(8));

        TextField tfName = new TextField();
        TextField tfPrice = new TextField();
        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll("Mancare", "Bautura");
        cbType.setValue("Mancare");
        TextField tfSpecific = new TextField();
        tfSpecific.setPromptText("Gramaj sau Volum");
        CheckBox cbVegetarian = new CheckBox("Vegetarian?");

        Button btnAdd = new Button("Adaugă produs");
        btnAdd.setOnAction(e -> {
            String name = tfName.getText().trim();
            String priceText = tfPrice.getText().trim();
            String type = cbType.getValue();
            String specText = tfSpecific.getText().trim();

            if (name.isEmpty() || priceText.isEmpty()) {
                showAlert("Eroare", "Completează nume și preț");
                return;
            }
            double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException ex) {
                showAlert("Eroare", "Preț invalid");
                return;
            }

            if (type.equals("Mancare")) {
                int weight = 0;
                try { weight = Integer.parseInt(specText); } catch (Exception ignored) {}
                Food f = new Food(name, price, weight, cbVegetarian.isSelected());
                productRepo.addProduct(f);
            } else {
                int vol = 0;
                try { vol = Integer.parseInt(specText); } catch (Exception ignored) {}
                Drink d = new Drink(name, price, vol);
                productRepo.addProduct(d);
            }
            tfName.clear(); tfPrice.clear(); tfSpecific.clear(); cbVegetarian.setSelected(false);
            refreshProducts(products);
        });

        form.add(new Label("Nume"), 0, 0);
        form.add(tfName, 1, 0);
        form.add(new Label("Preț"), 0, 1);
        form.add(tfPrice, 1, 1);
        form.add(new Label("Tip"), 0, 2);
        form.add(cbType, 1, 2);
        form.add(new Label("Gramaj/Volum"), 0, 3);
        form.add(tfSpecific, 1, 3);
        form.add(cbVegetarian, 2, 3);
        form.add(btnAdd, 1, 4);

        pane.setTop(form);
        pane.setCenter(table);

        // Buttons
        Button btnImport = new Button("Import JSON");
        Button btnExport = new Button("Export JSON");
        Button btnDelete = new Button("Șterge Produs Selectat");

        btnImport.setOnAction(e -> {
            File file = new File("meniu.json");
            if (!file.exists()) {
                showAlert("Eroare", "Fisier meniu.json nu exista.");
                return;
            }
            try {
                List<Product> imported = objectMapper.readValue(file, new TypeReference<List<Product>>(){});
                productRepo.deleteAll();
                for (Product p : imported) productRepo.addProduct(p);
                refreshProducts(products);
                showAlert("Succes", "Import realizat.");
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Eroare", "Import JSON esuat.");
            }
        });

        btnExport.setOnAction(e -> {
            try {
                List<Product> all = productRepo.getAllProducts();
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("meniu.json"), all);
                showAlert("Succes", "Export realizat in meniu.json");
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Eroare", "Export JSON esuat.");
            }
        });

        btnDelete.setOnAction(e -> {
            Product sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert("Info", "Selectează un produs.");
                return;
            }
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Sigur stergi?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> res = conf.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.YES) {
                try {
                    productRepo.deleteProduct(sel);
                } catch (Exception ex) {
                    products.remove(sel);
                }
                refreshProducts(products);
            }
        });

        HBox bottom = new HBox(10, btnImport, btnExport, btnDelete);
        bottom.setPadding(new Insets(8));
        pane.setBottom(bottom);

        return pane;
    }

    private void refreshProducts(ObservableList<Product> products) {
        products.clear();
        try {
            List<Product> all = productRepo.getAllProducts();
            products.addAll(all);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // --- TAB 2: Staff management ---
    private BorderPane createStaffTabContent() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));

        TableView<User> table = new TableView<>();
        TableColumn<User, String> colUsername = new TableColumn<>("Username");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUsername.setPrefWidth(200);

        TableColumn<User, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getRole().name()));
        colRole.setPrefWidth(120);

        table.getColumns().addAll(colUsername, colRole);

        ObservableList<User> users = FXCollections.observableArrayList();
        table.setItems(users);
        refreshStaff(users);

        GridPane form = new GridPane();
        form.setHgap(8); form.setVgap(8); form.setPadding(new Insets(8));

        TextField tfUsername = new TextField();
        PasswordField pfPassword = new PasswordField();
        Button btnHire = new Button("Angajează Ospătar");

        btnHire.setOnAction(e -> {
            String username = tfUsername.getText().trim();
            String pass = pfPassword.getText();
            if (username.isEmpty() || pass.isEmpty()) { showAlert("Eroare", "Date incomplete."); return; }

            try {
                User u = new User(username, pass, UserRole.STAFF);
                userRepo.save(u);
                tfUsername.clear(); pfPassword.clear();
                refreshStaff(users);
            } catch (Exception ex) {
                showAlert("Eroare", "Userul exista deja!");
            }
        });

        Button btnFire = new Button("Concediază");
        // --- LOGICA DE ȘTERGERE ÎN CASCADĂ REPARATĂ ---
        btnFire.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                        "Ești sigur că vrei să concediezi ospătarul " + sel.getUsername() + "?\n\nATENȚIE: Se vor șterge și toate comenzile lui din istoric!",
                        ButtonType.YES, ButtonType.NO);

                Optional<ButtonType> res = conf.showAndWait();

                if (res.isPresent() && res.get() == ButtonType.YES) {
                    try {
                        // 1. Ștergem comenzile întâi
                        orderRepo.deleteOrdersByUserId(sel.getId());

                        // 2. Ștergem userul
                        userRepo.delete(sel);

                        refreshStaff(users);
                        showAlert("Succes", "Angajatul și comenzile sale au fost șterse.");

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showAlert("Eroare", "Nu s-a putut șterge angajatul: " + ex.getMessage());
                    }
                }
            } else {
                showAlert("Info", "Selectează un user pentru concediere.");
            }
        });
        // ----------------------------------------------

        form.add(new Label("Username"), 0, 0); form.add(tfUsername, 1, 0);
        form.add(new Label("Password"), 0, 1); form.add(pfPassword, 1, 1);
        form.add(btnHire, 1, 2); form.add(btnFire, 2, 2);

        pane.setTop(form);
        pane.setCenter(table);
        return pane;
    }

    private void refreshStaff(ObservableList<User> users) {
        users.clear();
        try {
            users.addAll(userRepo.findByRole(UserRole.STAFF));
        } catch (Exception ex) {}
    }

    // --- TAB 3: Offers ---
    private VBox createOffersTabContent() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        CheckBox cbHappy = new CheckBox("Happy Hour (băuturi)");
        CheckBox cbMeal = new CheckBox("Meal Deal (desert redus)");
        CheckBox cbParty = new CheckBox("Party Pack (4 pizza -> 1 gratuit)");
        box.getChildren().addAll(new Label("Oferte Active:"), cbHappy, cbMeal, cbParty);
        return box;
    }

    // --- TAB 4: ISTORIC ---
    private VBox createHistoryTabContent() {
        TableView<Order> table = new TableView<>();

        TableColumn<Order, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().getId())));

        TableColumn<Order, String> colDate = new TableColumn<>("Data");
        colDate.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDate().toString()));

        TableColumn<Order, String> colWaiter = new TableColumn<>("Ospătar");
        colWaiter.setCellValueFactory(cell -> {
            if (cell.getValue().getUser() != null) {
                return new javafx.beans.property.SimpleStringProperty(cell.getValue().getUser().getUsername());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        TableColumn<Order, String> colTable = new TableColumn<>("Masa");
        colTable.setCellValueFactory(cell -> {
            if (cell.getValue().getTable() != null) {
                return new javafx.beans.property.SimpleStringProperty(cell.getValue().getTable().getName());
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        TableColumn<Order, String> colTotal = new TableColumn<>("Total (RON)");
        colTotal.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.format("%.2f", cell.getValue().getTotal())));

        TableColumn<Order, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));

        table.getColumns().addAll(colId, colDate, colWaiter, colTable, colTotal, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnRefresh = new Button("Reîmprospătează Istoricul");
        btnRefresh.setOnAction(e -> {
            table.getItems().clear();
            table.getItems().addAll(orderRepo.getAllOrders());
        });

        btnRefresh.fire();

        VBox layout = new VBox(10, btnRefresh, table);
        layout.setPadding(new Insets(10));
        return layout;
    }

    private void showAlert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}