package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class UserHomeFormController {

    public Button btnLogOut;
    public Button btnViewReports;
    public Button btnUserProfile;
    public Button btnRecordAttendance;
    public Label lblGreeting;

    public void initGreeting(String string){
        lblGreeting.setText(string);

    }

    public void btnRecordAttendanceClickOnAction(ActionEvent event) {

        try {
            AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/recordAttendance.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void btnUserProfileClickOnAction(ActionEvent event) {
    }

    public void btnViewReportsClickOnAction(ActionEvent event) {
    }

    public void btnLogOutClickOnAction(ActionEvent event) {
    }
}
