package viewmodel;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.UserSession;

import java.util.prefs.Preferences;


public class LoginController {
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameTextField;

    @FXML
    private GridPane rootpane;
    public void initialize() {
        rootpane.setBackground(new Background(new BackgroundImage(new Image(getClass().getResource("/images/fscLogin.jpeg").toExternalForm()),BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, true, Side.BOTTOM, 0, true),
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true))));


        rootpane.setOpacity(0);
        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(3), rootpane);
        fadeOut2.setFromValue(0);
        fadeOut2.setToValue(1);
        fadeOut2.play();
    }
    private static BackgroundImage createImage(String url) {
        return new BackgroundImage(
                new Image(url),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, true, Side.BOTTOM, 0, true),
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true));
    }

    @FXML
    public void login(ActionEvent actionEvent) {
        String enteredUsername = usernameTextField.getText();
        String enteredPassword = passwordField.getText();

        // Validate the credentials
        if (validateCredentials(enteredUsername, enteredPassword)) {
            // Proceed to next screen
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/view/db_interface_gui.fxml"));
                    Scene scene = new Scene(root, 925 , 650);
                    scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
                    Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    window.setScene(scene);
                    window.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

        } else {
            // Invalid credentials
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password. Please try again.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private boolean validateCredentials(String username, String password) {
        UserSession userSession = UserSession.loadUserSession();

        // Check if the session exists and credentials match
        return userSession != null && username.equals(userSession.getUserName()) && password.equals(userSession.getPassword());
    }
    
    public void signUp(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/signUp.fxml"));
            Scene scene = new Scene(root, 1100, 725);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
