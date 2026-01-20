package ro.unitbv.restaurant;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ro.unitbv.restaurant.model.User;
import ro.unitbv.restaurant.repository.UserRepository;

import java.util.Optional;
import java.util.function.Consumer;

public class LoginView {

    private final UserRepository userRepo = new UserRepository();

    public Scene createScene(Stage stage, Consumer<User> onLoginSuccess, Runnable onGuest) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label lblUser = new Label("Username:");
        TextField tfUser = new TextField();
        Label lblPass = new Label("Password:");
        PasswordField pfPass = new PasswordField();

        Button btnLogin = new Button("Login");
        Button btnGuest = new Button("Intră ca Client (Guest)");

        grid.add(lblUser, 0, 0);
        grid.add(tfUser, 1, 0);
        grid.add(lblPass, 0, 1);
        grid.add(pfPass, 1, 1);
        grid.add(btnLogin, 0, 2);
        grid.add(btnGuest, 1, 2);

        // Action handlers
        btnLogin.setOnAction(e -> {
            String username = tfUser.getText().trim();
            String password = pfPass.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Login", "Completează username și parola.");
                return;
            }
            Optional<User> opt = userRepo.findByUsername(username);
            if (opt.isPresent()) {
                User user = opt.get();
                if (password.equals(user.getPassword())) {
                    onLoginSuccess.accept(user);
                } else {
                    showAlert("Login eșuat", "Parolă incorectă.");
                }
            } else {
                showAlert("Login eșuat", "Utilizator inexistent.");
            }
        });

        btnGuest.setOnAction(e -> onGuest.run());

        VBox root = new VBox(grid);
        Scene scene = new Scene(root, 600, 400);
        return scene;
    }

    private void showAlert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}

