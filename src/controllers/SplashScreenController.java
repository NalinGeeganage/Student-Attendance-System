package controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SplashScreenController {
    public Label lblLoading;
    public ProgressBar pgbLoading;
    private File file;     // Backup file to restore

    public void initialize(){
        establishDBConnection();

    }
    private void establishDBConnection(){
        lblLoading.setText("Establishing DB connection");

        new Thread(()->{
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
                DriverManager.getConnection("jdbc:mysql://localhost:3306/Dep8_Student_Attendance");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
               if (e.getSQLState().equals("42000")){
                   Platform.runLater(() -> loadImportDBForm());
               }
                e.printStackTrace();
            }

        }).start();
    }
    private void loadImportDBForm(){
        try {
            SimpleObjectProperty<File> fileProperty = new SimpleObjectProperty<>(file);


            Stage stage = new Stage();
            AnchorPane pane = FXMLLoader.load(this.getClass().getResource("view/ImportDBConnection.fxml"));
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setTitle("Student Attendance System: First time boot");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(lblLoading.getScene().getWindow());
            stage.centerOnScreen();
            stage.showAndWait();
            file = fileProperty.getValue();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }


}
