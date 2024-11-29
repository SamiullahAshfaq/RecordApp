package com.example.searchApp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private List<String[]> records = new ArrayList<>();
    private int currentIndex = -1;

    // UI Components
    private TextField fullNameField, idField, homeProvinceField;
    private DatePicker dobPicker;
    private ToggleGroup genderGroup;
    private RadioButton maleRadio, femaleRadio;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Record Manager");

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // Input Fields
        fullNameField = new TextField();
        fullNameField.setPromptText("Enter full name");
        idField = new TextField();
        idField.setPromptText("Enter ID");
        homeProvinceField = new TextField();
        homeProvinceField.setPromptText("Enter home province");

        // DatePicker with calendar-only input
        dobPicker = new DatePicker();
        dobPicker.setEditable(false); // Disables manual text input
        dobPicker.setPromptText("Select date of birth");

        // Gender (Radio Buttons)
        genderGroup = new ToggleGroup();
        maleRadio = new RadioButton("Male");
        femaleRadio = new RadioButton("Female");
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("ID:"), 0, 1);
        grid.add(idField, 1, 1);
        grid.add(new Label("Gender:"), 0, 2);
        grid.add(maleRadio, 1, 2);
        grid.add(femaleRadio, 2, 2);
        grid.add(new Label("Home Province:"), 0, 3);
        grid.add(homeProvinceField, 1, 3);
        grid.add(new Label("DOB:"), 0, 4);
        grid.add(dobPicker, 1, 4);

        // Buttons
        Button newButton = createStyledButton("Add");
        Button deleteButton = createStyledButton("Delete");
        Button resetButton = createStyledButton("Reset");
        Button findPrevButton = createStyledButton("Find Prev");
        Button findNextButton = createStyledButton("Find Next");
        Button findButton = createStyledButton("Find");
        Button closeButton = createStyledButton("Close");

        grid.add(newButton, 3, 0);
        grid.add(deleteButton, 3, 1);
        grid.add(resetButton, 3, 2); // Changed from Restore to Reset
        grid.add(findPrevButton, 3, 3);
        grid.add(findNextButton, 3, 4);
        grid.add(findButton, 3, 5);
        grid.add(closeButton, 3, 6);

        // Event Listeners
        newButton.setOnAction(e -> addRecord());
        deleteButton.setOnAction(e -> deleteRecord());
        resetButton.setOnAction(e -> resetFields()); // Updated to Reset
        findPrevButton.setOnAction(e -> findPrevious());
        findNextButton.setOnAction(e -> findNext());
        findButton.setOnAction(e -> findByName());
        closeButton.setOnAction(e -> primaryStage.close());

        // Load Records on Startup
        loadRecords();

        // Scene Setup
        Scene scene = new Scene(grid, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/com/example/searchApp/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #5DADE2; -fx-text-fill: white; -fx-font-size: 14;");
        button.setPrefWidth(100);
        return button;
    }

    private void addRecord() {
        String name = fullNameField.getText().trim();
        String id = idField.getText().trim();
        String homeProvince = homeProvinceField.getText().trim();
        String gender = maleRadio.isSelected() ? "Male" : femaleRadio.isSelected() ? "Female" : "";
        String dob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : "";

        if (name.isEmpty() || id.isEmpty() || homeProvince.isEmpty() || gender.isEmpty() || dob.isEmpty()) {
            showAlert("Validation Error", "All fields must be filled out!", Alert.AlertType.ERROR);
            return;
        }

        String[] record = {name, id, gender, homeProvince, dob};
        records.add(record);
        saveRecords();
        resetFields();
        showAlert("Success", "Record added successfully!", Alert.AlertType.INFORMATION);
    }

    private void deleteRecord() {
        if (currentIndex >= 0 && currentIndex < records.size()) {
            records.remove(currentIndex);
            currentIndex = Math.min(currentIndex, records.size() - 1);
            saveRecords();
            if (!records.isEmpty()) {
                displayRecord(currentIndex);
            } else {
                resetFields();
            }
            showAlert("Success", "Record deleted successfully!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "No record selected to delete!", Alert.AlertType.ERROR);
        }
    }

    private void resetFields() {
        fullNameField.clear();
        idField.clear();
        homeProvinceField.clear();
        dobPicker.setValue(null);
        genderGroup.selectToggle(null);

        showAlert("Reset", "All fields have been cleared.", Alert.AlertType.INFORMATION);
    }

    private void findPrevious() {
        if (!records.isEmpty() && currentIndex > 0) {
            currentIndex--;
            displayRecord(currentIndex);
        } else {
            showAlert("Error", "No previous record found!", Alert.AlertType.ERROR);
        }
    }

    private void findNext() {
        if (!records.isEmpty() && currentIndex < records.size() - 1) {
            currentIndex++;
            displayRecord(currentIndex);
        } else {
            showAlert("Error", "No next record found!", Alert.AlertType.ERROR);
        }
    }

    private void findByName() {
        String nameToFind = fullNameField.getText().trim();
        if (nameToFind.isEmpty()) {
            showAlert("Validation Error", "Enter a name to search for!", Alert.AlertType.ERROR);
            return;
        }

        for (int i = 0; i < records.size(); i++) {
            if (records.get(i)[0].equalsIgnoreCase(nameToFind)) {
                currentIndex = i;
                displayRecord(currentIndex);
                return;
            }
        }

        showAlert("Not Found", "No record found with the given name.", Alert.AlertType.INFORMATION);
    }

    private void displayRecord(int index) {
        if (index >= 0 && index < records.size()) {
            String[] record = records.get(index);
            fullNameField.setText(record[0]);
            idField.setText(record[1]);
            if (record[2].equalsIgnoreCase("Male")) {
                maleRadio.setSelected(true);
            } else if (record[2].equalsIgnoreCase("Female")) {
                femaleRadio.setSelected(true);
            }
            homeProvinceField.setText(record[3]);
            dobPicker.setValue(record[4].isEmpty() ? null : java.time.LocalDate.parse(record[4]));
        }
    }

    private void saveRecords() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("records.txt"))) {
            for (String[] record : records) {
                writer.write(String.join(",", record));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRecords() {
        records.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("records.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line.split(","));
            }
        } catch (FileNotFoundException e) {
            System.out.println("No records found. Starting fresh.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!records.isEmpty()) {
            currentIndex = 0;
            displayRecord(currentIndex);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

