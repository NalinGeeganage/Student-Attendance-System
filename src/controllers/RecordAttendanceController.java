package controllers;

import db.DBConnection;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import sequrity.SecurityContextHolder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
    public AnchorPane root;
    private PreparedStatement stmSearchStudent;
    private String studentID;
    private Student student;
    private final SimpleBooleanProperty proceed = new SimpleBooleanProperty(false);

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
            stmSearchStudent =connection.prepareStatement("SELECT * FROM dep8_student_attendance.student WHERE id=?");
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Unable connect to DB, Connection failure").show();
            e.printStackTrace();

            ((Stage)(btnIN.getScene().getWindow())).close();
        }

        root.setOnKeyReleased(event -> {
            switch (event.getCode()){
                case F10:
                    btnIN.fire();
                    break;
                case ESCAPE:
                    btnOUT.fire();
                    break;
            }
        });

//     Update record attendance in interface
        updateRecordAttendanceForm();
    }

    public void txtStudentID_OnAction(ActionEvent event) {
        btnIN.setDisable(true);
        btnOUT.setDisable(true);
        studentID = txtStudentID.getText();

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
        recordAttendance(true);
    }
    public void btnOUTClickOnAction(ActionEvent event) {
       recordAttendance(false);
    }
    public void recordAttendance(boolean in){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            String lblStatus = null;
            PreparedStatement stm1 = connection.
                    prepareStatement("SELECT status, date FROM attendance WHERE student_id = ? ORDER BY date DESC LIMIT 1");
            stm1.setString(1,studentID);
            ResultSet rst = stm1.executeQuery();
            if (rst.next()){
                lblStatus = rst.getString("status");
            }
            if ((lblStatus != null && lblStatus.equals("IN") && in)|| (lblStatus != null && lblStatus.equals("OUT") && !in)){
                FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/AlertForm.fxml"));
                AnchorPane pane = fxmlLoader.load();
                AlertFormController controller = fxmlLoader.getController();
                controller.initData(txtStudentID.getText(),txtStudentName.getText(),
                        rst.getTimestamp("date").toLocalDateTime(), in, proceed);
                Scene scene = new Scene(pane);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setResizable(false);
                stage.setTitle("WARNING !");
                stage.sizeToScene();
                stage.showAndWait();
                System.out.println(proceed.getValue());
                if (proceed.getValue()){
                    addRecord(in);
                    updateRecordAttendanceForm();
                }
            }
            else{
                addRecord(in);
                updateRecordAttendanceForm();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to save customer", ButtonType.OK).show();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addRecord(boolean in) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm2 = null;

        stm2 = connection.
                    prepareStatement("INSERT INTO attendance (date, status, student_id, username) VALUES (NOW(),?,?,?)");
        stm2.setString(1,(in? "IN" : "OUT"));
        stm2.setString(2,studentID);
        stm2.setString(3,SecurityContextHolder.getPrinciple().getUsername());
        if (stm2.executeUpdate() != 1){
                throw new RuntimeException("Unable to save customer");
        }
        /*Get guardian contact number and add it to the student */

        PreparedStatement stm = connection.prepareStatement("SELECT guardian_contact FROM student WHERE id = ?");
        stm.setString(1,studentID);
        ResultSet rst = stm.executeQuery();
        if (!rst.next()){
            throw new RuntimeException("Unable to select name");
        }
        student = new Student(txtStudentName.
                getText(),txtStudentID.getText(),rst.getString("guardian_contact"));

        sendSMS(in);

        txtStudentID.clear();
        btnIN.setDisable(true);
        btnOUT.setDisable(true);
    }

    public void updateRecordAttendanceForm() {
        Connection connection = DBConnection.getInstance().getConnection();

        PreparedStatement stm = null;
        try {
            stm = connection.
                    prepareStatement("SELECT student_id, date, status, name FROM attendance JOIN student s on s.id = attendance.student_id ORDER BY date DESC LIMIT 1");
            ResultSet rst = stm.executeQuery();
            if (rst.next()){
                lblID.setText(rst.getString("student_id"));
                lblName.setText(rst.getString("name"));
                lblStatus.setText(rst.getString("date")+"-"+rst.getString("status"));
            }
            else
            {
                /*Fresh start*/
                lblDate.setText("ID : -");
                lblStudentName.setText(" NAME : -");
                lblDate.setText("DATE : -");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void sendSMS(boolean in) {
        System.out.println(student.getGuardianContactNO());
        try {
            URL url = new URL("https://api.smshub.lk/api/v1/send/single");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Before Run the project Add the token");
            connection.setDoOutput(true);


            String payload = String.format("{\n" +
                            "  \"message\": \"%s\",\n" +
                            "  \"phoneNumber\": \"%s\"\n" +
                            "}",
                    student.getName() + " has " + (in?"entered": "exited") + " at :" + LocalDateTime.now(),
                    student.getGuardianContactNO().replace("-",""));
            connection.getOutputStream().write(payload.getBytes());
            connection.getOutputStream().close();
            System.out.println(connection.getResponseCode());

            System.out.println(connection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    static class Student{
        private String name;
        private String Id;
        private String guardianContactNO;

        public Student(String name, String id, String guardianContactNO) {
            this.name = name;
            Id = id;
            this.guardianContactNO = guardianContactNO;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }

        public String getGuardianContactNO() {
            return guardianContactNO;
        }

        public void setGuardianContactNO(String guardianContactNO) {
            this.guardianContactNO = guardianContactNO;
        }
    }
}
