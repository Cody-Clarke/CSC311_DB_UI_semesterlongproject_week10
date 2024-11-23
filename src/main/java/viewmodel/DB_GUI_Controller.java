package viewmodel;

import com.azure.storage.blob.BlobClient;
import dao.DbConnectivityClass;
import dao.StorageUploader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Person;
import service.MyLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.ResourceBundle;

public class DB_GUI_Controller implements Initializable {

    @FXML
    TextField first_name, last_name, department, major, email, imageURL;
    @FXML
    ImageView img_view;
    @FXML
    MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();
    @FXML
    private Button btnDelete;
    @FXML
    private Button addBtn;
    @FXML
    private Button btnEdit;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private MenuItem editItem;
    @FXML
    private MenuItem deleteItem;
    @FXML
    private MenuItem ClearItem;
    @FXML
    private MenuItem CopyItem;
    @FXML
    private ComboBox<Major> majorComboBox;


    final String firstName_regex = "[A-Za-z]{2,25}";
    final String lastName_regex = "[A-Za-z]{2,25}";
    final String email_regex = "(\\w+)@(Farmingdale).(edu)";


    public boolean checkFirstName() {
        String name = first_name.getText();
        boolean isValid = name.matches(firstName_regex);
        if (!isValid) {
//            msg +=  "Invalid first name\n";
        }
        return isValid;
    }
    public boolean checkLastName() {
        String lastName = last_name.getText();
        boolean isValid = lastName.matches(lastName_regex);
        if (!isValid) {
//            msg +=  "Invalid last name\n";
        }
        return isValid;
    }
    public boolean checkEmail() {
        String email1 = email.getText();
        boolean isValid = email1.matches(email_regex);
        if (!isValid) {
           // msg +=  "Invalid email\n";
        }
        return isValid;
    }







    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);
            // Disable "Edit" "Delete" and "Add" buttons initially
            btnEdit.setDisable(true);
            btnDelete.setDisable(true);
            addBtn.setDisable(true);

            // Add listener to update button states based on TableView selection
            tv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                // Enable "Edit" and "Delete" buttons if a row is selected
                boolean isSelected = newValue != null;

                btnEdit.setDisable(!isSelected);
                btnDelete.setDisable(!isSelected);

                // Update menu items (if applicable, for example)
                updateMenuState(isSelected);
            });

            // Add listener to form fields to enable/disable the "Add" button
            first_name.textProperty().addListener((observable, oldValue, newValue) -> updateAddButtonState());
            last_name.textProperty().addListener((observable, oldValue, newValue) -> updateAddButtonState());
            department.textProperty().addListener((observable, oldValue, newValue) -> updateAddButtonState());
            majorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateAddButtonState());
            email.textProperty().addListener((observable, oldValue, newValue) -> updateAddButtonState());
            imageURL.textProperty().addListener((observable, oldValue, newValue) -> updateAddButtonState());

            majorComboBox.setItems(FXCollections.observableArrayList(Major.values()));
            majorComboBox.getSelectionModel().selectFirst();





        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private void updateAddButtonState() {
        // Enable Add button only if all fields are valid
        boolean isValidForm = checkFirstName() && checkLastName() && checkEmail() &&
                !department.getText().isEmpty() && !majorComboBox.getValue().toString().isEmpty()&&
                !imageURL.getText().isEmpty();
        addBtn.setDisable(!isValidForm);  // Disable or enable based on validity

    }

    private void updateMenuState(boolean isSelected) {
        // Disable the menu items if no record is selected
        editItem.setDisable(!isSelected);
        deleteItem.setDisable(!isSelected);
        ClearItem.setDisable(!isSelected);
        CopyItem.setDisable(!isSelected);
    }



    @FXML
    protected void addNewRecord() {

        if(checkFirstName() && checkLastName() && checkEmail() &&
                !department.getText().isEmpty() && !majorComboBox.getValue().toString().isEmpty() &&
                !imageURL.getText().isEmpty()) {

            Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                    majorComboBox.getValue().toString(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
            clearForm();
        }


    }

    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
//        major.setText("");
        email.setText("");
        imageURL.setText("");
    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void editRecord() {

        if(checkFirstName() && checkLastName() && checkEmail() &&
                !department.getText().isEmpty() && !majorComboBox.getValue().toString().isEmpty() &&
                !imageURL.getText().isEmpty()) {

            Person p = tv.getSelectionModel().getSelectedItem();
            int index = data.indexOf(p);
            Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), department.getText(),
                    majorComboBox.getValue().toString(), email.getText(), imageURL.getText());
            cnUtil.editUser(p.getId(), p2);
            data.remove(p);
            data.add(index, p2);
            tv.getSelectionModel().select(index);
        }



    }

    @FXML
    protected void deleteRecord() {

            Person p = tv.getSelectionModel().getSelectedItem();
            int index = data.indexOf(p);
            cnUtil.deleteRecord(p);
            data.remove(index);
            tv.getSelectionModel().select(index);

    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
            Task<Void> uploadTask = createUploadTask(file, progressBar);
            progressBar.progressProperty().bind(uploadTask.progressProperty());
            new Thread(uploadTask).start();
        }
    }

    private Task<Void> createUploadTask(File file, ProgressBar progressBar) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                StorageUploader store = new StorageUploader();
                BlobClient blobClient = store.getContainerClient().getBlobClient(file.getName());
                long fileSize = Files.size(file.toPath());
                long uploadedBytes = 0;

                try (FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream blobOutputStream = blobClient.getBlockBlobClient().getBlobOutputStream()) {

                    byte[] buffer = new byte[1024 * 1024]; // 1 MB buffer size
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        blobOutputStream.write(buffer, 0, bytesRead);
                        uploadedBytes += bytesRead;

                        // Calculate and update progress as a percentage
                        int progress = (int) ((double) uploadedBytes / fileSize * 100);
                        updateProgress(progress, 100);
                    }
                }

                return null;
            }
        };
    }

    @FXML
    protected void addRecord() {
        showSomeone();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setText(p.getDepartment());
        majorComboBox.setValue(Major.valueOf((p.getMajor())));
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
    }

    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }

    private static enum Major {Business, CSC, CPIS}

    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }

}