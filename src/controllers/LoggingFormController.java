package controllers;

import db.DBConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoggingFormController {
    public TextField txtUserName;
    public TextField txtPassword;
    public Label lblResult;
    public Button btnSignIn;

    public void btnSignInClickOnAction(ActionEvent event) {

        if (!isValidated()){
            new Alert(Alert.AlertType.ERROR,"Invalid username or password");
            txtUserName.selectAll();
            txtUserName.requestFocus();

        }
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("SELECT name , role FROM user WHERE username=? AND password=?");
            stm.setString(1,txtUserName.getText());
            stm.setString(2,txtPassword.getText());
            ResultSet resultSet = stm.executeQuery();

            if (!resultSet.next()){
                new Alert(Alert.AlertType.ERROR,"Invalid username and password").show();
                txtUserName.selectAll();
                txtUserName.requestFocus();
            }
            else {
                if(resultSet.getString("role").equals("ADMIN")){
                    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/AdminHomeForm.fxml"));
                    AnchorPane pane = fxmlLoader.load();
                    Scene scene = new Scene(pane);
                    AdminHomeFormController controller = fxmlLoader.getController();
                    controller.initGreeting(resultSet.getString("name"));
                    Stage primaryStage = (Stage) (btnSignIn.getScene().getWindow());
                    primaryStage.setScene(scene);
                    primaryStage.setResizable(false);

                    primaryStage.setTitle("Student Attendance system : Admin Home");
                    Platform.runLater(() -> {
                        primaryStage.centerOnScreen();
                        primaryStage.sizeToScene();
                    });

                }
                else {
                    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/UserHomeForm.fxml"));
                    AnchorPane pane = fxmlLoader.load();
                    Scene scene = new Scene(pane);
                    UserHomeFormController controller = fxmlLoader.getController();
                    controller.initGreeting(resultSet.getString("name"));
                    Stage primaryStage = (Stage) (btnSignIn.getScene().getWindow());
                    primaryStage.setScene(scene);
                    primaryStage.setResizable(false);
                    primaryStage.setTitle("Student Attendance system : User Home");

                    Platform.runLater(() -> {
                        primaryStage.centerOnScreen();
                        primaryStage.sizeToScene();
                    });


                }
            }


        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidated(){
        return (txtUserName.getText().length() < 4 || !txtUserName.getText().matches("\\b[A-Za-z0-9]*\\b") || txtPassword.getText().trim().length() < 6
                || !txtPassword.getText().trim().matches("\\b[A-Za-z0-9 ]*\\b"));

        }

    }

