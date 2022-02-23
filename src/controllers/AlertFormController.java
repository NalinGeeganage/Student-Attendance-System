package controllers;

import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlertFormController {

    public Button btnProceed;
    public Button btnCallPolice;
    public Label lblID;
    public Label lblName;
    public Label lblDate;
    public ImageView imgDanger;
    private SimpleBooleanProperty proceed;


    public void initialize() throws URISyntaxException {
        ScaleTransition sct = new ScaleTransition(Duration.millis(400),imgDanger);
        sct.setFromX(0.8);
        sct.setFromY(0.8);
        sct.setToX(1.2);
        sct.setToY(1.2);
        sct.setCycleCount(-1);
        sct.play();
        playSiren();
    }

    public void playSiren() throws URISyntaxException {
        Media media = new Media(this.getClass().getResource("/assets/siren.mp3").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(2);
        mediaPlayer.play();
    }

    public void btnProceed_OnAction(ActionEvent event) {
        proceed.setValue(true);
        ((Stage)(btnProceed.getScene().getWindow())).close();
    }

    public void btnCallPolice_OnAction(ActionEvent event) {
        ((Stage)(btnProceed.getScene().getWindow())).close();
    }

    public void initData(String studentID, String studentName, LocalDateTime date, boolean in, SimpleBooleanProperty proceed){
        lblID.setText(studentID);
        lblName.setText(studentName);
        lblDate.setText(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"))+"-"+ (in ? "IN": "OUT"));
        this.proceed = proceed;
    }
}
