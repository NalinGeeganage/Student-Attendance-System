package controllers;

import db.DBConnection;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class RecordAttendanceController {

    public TextField txtStudentID;
    public ImageView imgStudent;
    public TextField txtStudentName;
    public Button btnIN;
    public Button btnOUT;
    public Label lblID;
    public Label lblName;
    public Label lblStatus;
    public Label lblDate;
    public Label lblStudentName;
    private PreparedStatement stmSearchStudent;

    public void initialize(){
        btnIN.setDisable(true);
        btnOUT.setDisable(true);
        lblStudentName.setText("Please Enter Student ID or Read QR Code");

        //display time
        lblDate.setText(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %1$Tp",new Date()));

        // update the time
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            lblDate.setText(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %1$Tp",new Date()));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();


        Connection connection = DBConnection.getInstance().getConnection();
        try {
            stmSearchStudent =connection.prepareStatement("SELECT * FROM student WHERE id=?");
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Unable connect to DB, Connection failure").show();
            e.printStackTrace();

            ((Stage)(btnIN.getScene().getWindow())).close();
        }


    }

    public void txtStudentID_OnAction(ActionEvent event) {
        btnIN.setDisable(true);
        btnOUT.setDisable(true);

        if(txtStudentID.getText().trim() == null){
            return;
        }
        try {
            stmSearchStudent.setString(1, txtStudentID.getText().trim());
            ResultSet resultSet = stmSearchStudent.executeQuery();

            if (resultSet.next()){
                txtStudentName.setText(resultSet.getString("name").toUpperCase());
                InputStream picture = resultSet.getBlob("picture").getBinaryStream();
                imgStudent.setImage(new Image(picture));
                btnIN.setDisable(false);
                btnOUT.setDisable(false);
            }
            else {
                new Alert(Alert.AlertType.ERROR,"Invalid Index Number").show();
                txtStudentID.selectAll();
                txtStudentID.requestFocus();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Something went wrong, please try again! ");
            txtStudentID.selectAll();
            txtStudentID.requestFocus();
            e.printStackTrace();
        }
    }

    public void btnINClickOnAction(ActionEvent event) {

    }

    public void btnOUTClickOnAction(ActionEvent event) {


    }
}
