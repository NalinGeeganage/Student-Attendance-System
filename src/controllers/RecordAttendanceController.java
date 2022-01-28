package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

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

    public void initialize(){
        lblDate.setText(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$Tp",new Date()));
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            lblDate.setText(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$Tp",new Date()));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    public void btnINClickOnAction(ActionEvent event) {

    }

    public void btnOUTClickOnAction(ActionEvent event) {


    }
}
