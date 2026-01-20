package ro.unitbv.restaurant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ro.unitbv.restaurant.model.Product;
import ro.unitbv.restaurant.model.User;
import ro.unitbv.restaurant.model.UserRole;
import ro.unitbv.restaurant.repository.ProductRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RestaurantApp extends Application {

    private ProductRepository repo = new ProductRepository();
    private ObservableList<Product> productsList = FXCollections.observableArrayList();
    private ListView<Product> listView = new ListView<>();

    private ObjectMapper objectMapper = new ObjectMapper();
    private final String JSON_FILE = "meniu.json";

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // 1. Seed data (Populam baza de date daca e goala)
        DataSeeder seeder = new DataSeeder();
        seeder.seed();

        // 2. Afisam ecranul de Login
        showLogin();

        primaryStage.setTitle("La Andrei - Restaurant");
        primaryStage.show();
    }

    private void showLogin() {
        LoginView loginView = new LoginView();
        // Aici transmitem ce sa faca aplicatia dupa login (onLoginSuccess) sau guest (enterAsGuest)
        Scene loginScene = loginView.createScene(primaryStage, this::onLoginSuccess, this::enterAsGuest);
        primaryStage.setScene(loginScene);
    }

    private void onLoginSuccess(User user) {
        UserRole role = user.getRole();
        if (role == UserRole.CLIENT) {
            enterAsGuest();
        } else if (role == UserRole.STAFF) {
            showStaffView(user);
        } else if (role == UserRole.ADMIN) {
            showAdminView(user);
        } else {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Rol necunoscut");
            a.setHeaderText(null);
            a.setContentText("Rol necunoscut: " + role);
            a.showAndWait();
        }
    }

    private void enterAsGuest() {
        GuestView guestView = new GuestView();
        Scene guestScene = guestView.createScene(primaryStage, this::showLogin);
        primaryStage.setScene(guestScene);
    }

    private void showStaffView(User user) {
        StaffView staffView = new StaffView(user);
        Scene scene = staffView.createScene(primaryStage, this::showLogin);
        primaryStage.setTitle("La Andrei - Mod Ospatar (Staff)");
        primaryStage.setScene(scene);
    }

    private void showAdminView(User user) {
        AdminView adminView = new AdminView();
        Scene adminScene = adminView.createScene(primaryStage, this::showLogin);
        primaryStage.setScene(adminScene);
    }

    // --- METODELE VECHI (Păstrate pentru Etapa 3 - Admin) ---

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        javafx.scene.control.Menu fileMenu = new javafx.scene.control.Menu("File");

        MenuItem exportItem = new MenuItem("Export JSON");
        exportItem.setOnAction(e -> exportToJson());

        MenuItem importItem = new MenuItem("Import JSON");
        importItem.setOnAction(e -> importFromJson());

        fileMenu.getItems().addAll(exportItem, importItem);
        menuBar.getMenus().add(fileMenu);
        return menuBar;
    }

    private GridPane createDetailsForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextField priceField = new TextField();
        Label detailsLabel = new Label("Selecteaza un produs...");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price (RON):"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(detailsLabel, 0, 2, 2, 1);

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != null) {
                nameField.textProperty().unbindBidirectional(oldVal.nameProperty());
                priceField.textProperty().unbindBidirectional(oldVal.priceProperty());
            }

            if (newVal != null) {
                nameField.textProperty().bindBidirectional(newVal.nameProperty());
                priceField.textProperty().bindBidirectional(newVal.priceProperty(), new javafx.util.StringConverter<Number>() {
                    @Override
                    public String toString(Number object) {
                        return object == null ? "" : object.toString();
                    }
                    @Override
                    public Number fromString(String string) {
                        try {
                            return Double.parseDouble(string);
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                });
                detailsLabel.setText(newVal.getDetails());
            } else {
                nameField.setText("");
                priceField.setText("");
                detailsLabel.setText("");
            }
        });

        return grid;
    }

    private void loadDataFromDB() {
        productsList.clear();
        try {
            List<Product> dbProducts = repo.getAllProducts();
            productsList.addAll(dbProducts);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Eroare DB", "Nu s-a putut conecta la baza de date. Verifica parola!");
        }
    }

    private void exportToJson() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(JSON_FILE), productsList);
            showAlert("Succes", "Export realizat in " + JSON_FILE);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Eroare", "Nu s-a putut exporta.");
        }
    }

    private void importFromJson() {
        try {
            File file = new File(JSON_FILE);
            if (!file.exists()) {
                showAlert("Eroare", "Fisierul " + JSON_FILE + " nu exista!");
                return;
            }

            List<Product> importedProducts = objectMapper.readValue(file, new TypeReference<List<Product>>(){});
            repo.deleteAll();
            for (Product p : importedProducts) {
                repo.addProduct(p);
            }
            loadDataFromDB();
            showAlert("Succes", "Importat " + importedProducts.size() + " produse.");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Eroare", "JSON invalid.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}