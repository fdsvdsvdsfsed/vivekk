package com.example.lab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class CrudController {

    @FXML
    private TextField taskIdField;
    @FXML
    private TextField taskNameField;
    @FXML
    private TextField taskDescriptionField;
    @FXML
    private TextField taskStatusField;

    @FXML
    private TableView<task> taskTable;
    @FXML
    private TableColumn<task, Integer> taskIdColumn;
    @FXML
    private TableColumn<task, String> taskNameColumn;
    @FXML
    private TableColumn<task, String> taskDescriptionColumn;
    @FXML
    private TableColumn<task, String> taskStatusColumn;

    private ObservableList<task> taskList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        taskIdColumn.setCellValueFactory(new PropertyValueFactory<>("taskId"));
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        taskDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("taskDescription"));
        taskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("taskStatus"));

        loadTasks(); // Automatically load tasks when the view is initialized
    }

    @FXML
    private void handleInsert(ActionEvent event) {
        String taskName = taskNameField.getText();
        String taskDescription = taskDescriptionField.getText();
        String taskStatus = taskStatusField.getText();

        String jdbcUrl = "jdbc:mysql://localhost:3306/vivek";
        String dbUser = "root";
        String dbPassword = "";

        String query = "INSERT INTO task_management (task_name, task_description, task_status) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, taskName);
            statement.setString(2, taskDescription);
            statement.setString(3, taskStatus);

            statement.executeUpdate();
            loadTasks(); // Reload tasks after insert
            clearFields(); // Clear input fields after successful insert

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Insert Failed", "Error inserting task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        int taskId = Integer.parseInt(taskIdField.getText());
        String taskName = taskNameField.getText();
        String taskDescription = taskDescriptionField.getText();
        String taskStatus = taskStatusField.getText();

        String jdbcUrl = "jdbc:mysql://localhost:3306/vivek";
        String dbUser = "root";
        String dbPassword = "";

        String query = "UPDATE task_management SET task_name = ?, task_description = ?, task_status = ? WHERE task_id = ?";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, taskName);
            statement.setString(2, taskDescription);
            statement.setString(3, taskStatus);
            statement.setInt(4, taskId);

            statement.executeUpdate();
            loadTasks(); // Reload tasks after update
            clearFields(); // Clear input fields after successful update

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Error updating task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        int taskId = Integer.parseInt(taskIdField.getText());

        String jdbcUrl = "jdbc:mysql://localhost:3306/vivek";
        String dbUser = "root";
        String dbPassword = "";

        String query = "DELETE FROM task_management WHERE task_id = ?";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, taskId);

            statement.executeUpdate();
            loadTasks(); // Reload tasks after delete
            clearFields(); // Clear input fields after successful delete

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Delete Failed", "Error deleting task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoad(ActionEvent event) {
        loadTasks(); // Triggered when the "Load" button is pressed
    }

    @FXML
    private void handleFetch(ActionEvent event) {
        loadTasks(); // Triggered when the "Fetch" button is pressed
    }

    private void loadTasks() {
        taskList.clear();

        String jdbcUrl = "jdbc:mysql://localhost:3306/vivek";
        String dbUser = "root";
        String dbPassword = "";

        String query = "SELECT * FROM task_management";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int taskId = resultSet.getInt("task_id");
                String taskName = resultSet.getString("task_name");
                String taskDescription = resultSet.getString("task_description");
                String taskStatus = resultSet.getString("task_status");

                taskList.add(new task(taskId, taskName, taskDescription, taskStatus));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Load Failed", "Error loading tasks: " + e.getMessage());
            e.printStackTrace();
        }

        taskTable.setItems(taskList);
    }

    private void clearFields() {
        taskIdField.clear();
        taskNameField.clear();
        taskDescriptionField.clear();
        taskStatusField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
