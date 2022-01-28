package controllers;

import db.DBConnection;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
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
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SplashScreenController {
    public Label lblLoading;
    public ProgressBar pgbLoading;
    private final SimpleObjectProperty<File> fileProperty = new SimpleObjectProperty<>();     // Backup file to restore
    private SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
    public void initialize(){
        pgbLoading.progressProperty().bind(progress);
        establishDBConnection();
    }
    private void establishDBConnection(){
        lblLoading.setText("Establishing DB connection");
        progress.set(0.1);

        // Use new thread to run the database loading part
        new Thread(()->{
            try{
                sleep(500);
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.
                        getConnection("jdbc:mysql://localhost:3306/dep8_student_attendance", "root", "mysql");
                Platform.runLater(() -> lblLoading.setText("Setting up the UI"));
                sleep(500);

                Platform.runLater(() -> loadLoginForm(connection));

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
               if (e.getSQLState().equals("42000")){
                   Platform.runLater(this::loadImportDBForm);
               }else {
                   shutdownApp(e);
               }
            }
        }).start();
    }
    private void loadImportDBForm(){
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader
                     (this.getClass().getResource("/view/ImportDBConnection.fxml"));
            AnchorPane pane = fxmlLoader.load();
            ImportDBConnectionController controller = fxmlLoader.getController();
            controller.initFileProperty(fileProperty);

//            AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/ImportDBConnection.fxml"));
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setTitle("Student Attendance System: First time boot");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(lblLoading.getScene().getWindow());
            stage.centerOnScreen();
            stage.setOnCloseRequest(event -> {
                event.consume();
            });
            stage.showAndWait();
            if (fileProperty == null){
                lblLoading.setText("Creating a new DB");
                new Thread(()->{
                    try{
                        sleep(500);

                        Platform.runLater(() -> lblLoading.setText("Loading database"));

                        InputStream stream = this.getClass().
                                getResourceAsStream("/assets/db-script.sql");
                        byte[] buffer=new byte[stream.available()];
                        stream.read(buffer);
                        String script = new String(buffer);

                        Connection connection = DriverManager.
                                getConnection("jdbc:mysql://localhost:3306?allowMultiQueries", "root", "mysql");
                        Platform.runLater(() -> lblLoading.setText("Execute database script"));
                        Statement stm = connection.createStatement();
                        stm.execute(script);
                        sleep(500);

                        Platform.runLater(() -> lblLoading.setText("Obtaining a new DB connection"));
                        connection= DriverManager.
                                getConnection("jdbc:mysql://localhost:3306/Dep8_Student_Attendance");
                        sleep(500);

                        DBConnection.getInstance().init(connection);

                        Platform.runLater(() -> lblLoading.setText("Setting Up the UI...."));

                        loadCreateAdminForm();
                    } catch (IOException | SQLException e) {
                        if(e instanceof SQLException){
                            dropDatabase();
                        }
                        shutdownApp(e);
                    }
                }).start();
            }
            else{
                /*TODO */
                System.out.println("Restoring.....");
  //              loadLoginForm(connection);
            }

        }catch (IOException e) {
            shutdownApp(e);
        }
    }
    private void loadLoginForm(Connection connection){

        DBConnection.getInstance().init(connection);
        try{
            Stage stage = new Stage();
            AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/loggingForm.fxml"));
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.setTitle("Student attendant System : Create admin");
            stage.sizeToScene();
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

            ((Stage)(lblLoading.getScene().getWindow())).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadCreateAdminForm(){
        try{
            Stage stage = new Stage();
            AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/CreateAdminForm.fxml"));
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.setTitle("Student attendant System ; Create admin");
            stage.sizeToScene();
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

            ((Stage)(lblLoading.getScene().getWindow())).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void shutdownApp(Throwable t){

        Platform.runLater(() -> {
            lblLoading.setText("Fail to connect ");
        });
        sleep(1000);

        if (t != null) {
            t.printStackTrace();
        }
        System.exit(1);
    }

    private void dropDatabase(){
        Connection connection = null;
        try {
            connection = DriverManager.
                    getConnection("jdbc:mysql://localhost:3306", "root", "mysql");
            Statement stm = connection.createStatement();
            stm.execute("DROP DATABASE IF EXISTS dep8_student_attendance");
            connection.close();
        } catch (SQLException e) {
            shutdownApp(e);
        }
    }
    private void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
