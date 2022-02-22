package controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AlertFormController {

    public Button btnProceed;
    public Button btnCallPolice;
    public Label lblID;
    public Label lblName;
    public Label lblDate;


    public void initialize() throws URISyntaxException {
        playSiren();
    }

    public void playSiren() throws URISyntaxException {
        Media media = new Media(this.getClass().getResource("/assets/siren.mp3").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(2);
        mediaPlayer.play();
    }

    public void btnProceed_OnAction(ActionEvent event) {
    }

    public void btnCallPolice_OnAction(ActionEvent event) {
    }

    public void initData(String studentID, String studentName, LocalDateTime date, boolean in){
        lblID.setText(studentID);
        lblName.setText(studentName);
        lblDate.setText(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"))+"-"+ (in ? "IN": "OUT"));
    }
}
