package lk.ijse.dep8.sas.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep8.sas.security.SecurityContextHolder;
import lk.ijse.dep8.sas.util.DBConnection;
import lk.ijse.dep8.sas.util.DepAlert;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

public class RecordAttendanceFormController {
    public TextField txtStudentID;
    public ImageView imgProfile;
    public Button btnIn;
    public Button btnOut;
    public Label lblDate;
    public Label lblID;
    public Label lblName;
    public Label lblStatus;
    public Label lblStudentName;
    public AnchorPane root;
    private PreparedStatement stmSearchStudent;
    private String studentId;
    private String name;
    private SimpleObjectProperty<Boolean> b1=new SimpleObjectProperty<>();
    private boolean abc;
    private Student student;

    public void initialize(){
        b1.setValue(false);
        root.setOnKeyReleased(event -> {
            switch (event.getCode()){
                case SPACE:btnIn.fire();

            }

        });


        btnIn.setDisable(true);
        btnOut.setDisable(true);
        lblDate.setText(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %1$Tp", new Date()));

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), (event -> {
            lblDate.setText(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %1$Tp", new Date()));
        })));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            stmSearchStudent = connection.prepareStatement("SELECT * FROM student WHERE id=?");
        } catch (Exception e) {
            new DepAlert(Alert.AlertType.WARNING, "Failed to connect with DB","Connection Error", "Error").show();
            e.printStackTrace();
            Platform.runLater(()->{
                ((Stage)(btnIn.getScene().getWindow())).close();
            });
        }
        try {
            PreparedStatement finalRecord=connection.prepareStatement("SELECT * FROM attendance ORDER BY date DESC LIMIT 1");
            ResultSet resultSet = finalRecord.executeQuery();

            if(resultSet.next()){
            lblID.setText(resultSet.getString("student_id"));
            lblName.setText(resultSet.getString("status"));
            lblStatus.setText(String.valueOf(resultSet.getObject("date"))+"-"+resultSet.getString("status"));}
            else {
                lblStatus.setText("-----------");
                lblName.setText("-----------");
                lblID.setText("-----------");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    public void txtStudentID_OnAction(ActionEvent actionEvent) {
        btnIn.setDisable(true);
        btnOut.setDisable(true);
        lblStudentName.setText("Please enter/scan the student ID to proceed");
        imgProfile.setImage(new Image("/view/assets/qr-code.png"));

        if (txtStudentID.getText().trim().isEmpty()){
            return;
        }

        try {
            stmSearchStudent.setString(1, txtStudentID.getText().trim());
            ResultSet rst = stmSearchStudent.executeQuery();
            studentId=txtStudentID.getText();

            if (rst.next()){
                lblStudentName.setText(rst.getString("name").toUpperCase());
                name=lblStudentName.getText();
                InputStream is = rst.getBlob("picture").getBinaryStream();
                imgProfile.setImage(new Image(is));
                btnIn.setDisable(false);
                btnOut.setDisable(false);
                txtStudentID.selectAll();
            }else{
                new DepAlert(Alert.AlertType.ERROR, "Invalid Student ID, Try again!", "Oops!", "Error").show();
                txtStudentID.selectAll();
                txtStudentID.requestFocus();
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new DepAlert(Alert.AlertType.WARNING, "Something went wrong. Please try again!", "Connection Failure", "Error").show();
            txtStudentID.selectAll();
            txtStudentID.requestFocus();
        }

        System.out.println(studentId);
    }




    public void btnIn_OnAction(ActionEvent actionEvent) {
        recordAttendance(true);
        abc =true;
    }

    public void btnOut_OnAction(ActionEvent actionEvent) {

        recordAttendance(false);
        abc =false;
    }
    private void sendSMS(boolean in) {
        try {
            URL url = new URL("https://api.smshub.lk/api/v2/send/single");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "A4n2dulq9EZvFVNHsO4oufAqBtQTr4tj7WZYgkqskF9L9EQrcUhSm10OtBw3UOnVGoScqBmXIIKM5eCGsLdyu4CUmWfxwXHM10pMUtGKZ5OPhFSauEXWUqpfq07YW9j2Fu3Hf36uz8ShSVBNHtS9TKInYLRGrveo6a7MO5ftvU4t6Ha5T65wahDEdh1OCkghJclAH1Nj6ExcMm5BOWzseFlMALqIcmKc0xRCd02qhNVbv6WQRBEhlPnnogWG7yWwtxtFF087xkaGlhcyT6dCMXMmJyM6sa7cSNCyxaZA5OK3Oi4PA798pswC9tlvBMbLPPqKxZ3N7pz8U2S9thdMKJyQJsG6FFXBaIlE65fvdnMmvsdQHSx8Enee6utzGigczFUclT6be6RQDqksUPmNdOxlXrU6F3WOjUdZ7v3BodGHyvfW2yh6XsDSpVF7OaYnIbB0kgxUAV55bYpy6UdrK2j2QTwlEvPcKg59eZen9wdLtwJjEPxCXW2hEQeSXgelpB2rgvAGbUnB9ViqMJjCVc0ovA4sArNs0stcR4Qiw0o2nyhcL51SQb8Adm8axh2m2Tk49tdJzZu8hLB2jZ1mmTMcQwQqr1aeU7mv8Uh2S79wOFBKA2yhTCf1kG73KgaK8ieltQVRBNeJwR011gT5k72udiS3COQHNBE03WDfhdobVpT9oU7d5wLmGMh30AsAsNrq9beLWo3qU12hzfYamjbyWyDPlru68FFciVpAG1jo4oYCfz8lUCy1UPv6CL0MuTsm0uNfbxnIy5MttCfFHJQRXHDivPFHjRSaS0mVv8AmXzRtjBIlM4oM8uJohBZm4lZLeUTnXHChANxolPo3NOMkjbsz826uuraeyzuCaISGdt8C9GpoV8vSmjvBdDI4OW7ZcwBr1TJsO3sAZY9FpbyKprhAqRfzIxtNqpJuQgpVBnVCQNDIUkPWwzhjGhi9ozhaD4YSOjo3lU3zluzvS8chT8fQJf0aCfEqU4rF2Cesmto3ROTibGFfu1XxjkAv");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String payload = String.format("{\n" +
                            "  \"message\": \"%s\",\n" +
                            "  \"phoneNumber\": \"%s\"\n" +
                            "}",
                    student.name + "has " + (in?"entered": "exited") + " at :" + LocalDateTime.now(),
                    student.guardianContact);
            connection.getOutputStream().write(payload.getBytes());
            connection.getOutputStream().close();
            System.out.println(connection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static class Student {
        String id;
        String name;
        String guardianContact;

        public Student(String id, String name, String guardianContact) {
            this.id = id;
            this.name = name;
            this.guardianContact = guardianContact;
        }
    }
    
    private void recordAttendance(boolean in){
        Connection connection = DBConnection.getInstance().getConnection();

        /* Check last record status */
        try {
            String lastStatus = null;
            PreparedStatement stm = connection.
                    prepareStatement("SELECT status,date FROM attendance WHERE student_id=? ORDER BY date DESC LIMIT 1");
            stm.setString(1, txtStudentID.getText());

            ResultSet rst = stm.executeQuery();
            if (rst.next()){
                lastStatus = rst.getString("status");
            }

            if ((lastStatus != null && lastStatus.equals("IN") && in)||(lastStatus !=null && lastStatus.equals("OUT") && !in)){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AlertForm.fxml"));
                AnchorPane root =fxmlLoader.load();
                AlertFormController controller = fxmlLoader.getController();
                controller.initData(studentId,name,rst.getTimestamp("date").toLocalDateTime(),in);
                controller.initFilepropertyForSave(b1);
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setResizable(false);
                stage.setTitle("Alert! Horek");
                stage.sizeToScene();
                stage.centerOnScreen();
                stage.showAndWait();
                if (b1.getValue()){
                  //  System.out.println("ASDFGH");
                    abc =in;
                    recordAttendance(!in);


                }

            }else{
               // throw new RuntimeException()

                PreparedStatement stm2 = connection.
                        prepareStatement("INSERT INTO attendance (date, status, student_id, username) VALUES (NOW(),?,?,?)");
                stm2.setString(1, abc ? "IN": "OUT");
                stm2.setString(2,txtStudentID.getText());
                stm2.setString(3, SecurityContextHolder.getPrincipal().getUsername());
                if (stm2.executeUpdate() != 1){
                    throw new RuntimeException("Failed to add the attendance");
                }
                txtStudentID.clear();
                txtStudentID_OnAction(null);
            }
        }

      /*  catch (RuntimeException e) {
            PreparedStatement stm2 = connection.
                    prepareStatement("INSERT INTO attendance (date, status, student_id, username) VALUES (NOW(),?,?,?)");
            stm2.setString(1, abc ? "IN": "OUT");
            stm2.setString(2,txtStudentID.getText());
            stm2.setString(3, SecurityContextHolder.getPrincipal().getUsername());
            if (stm2.executeUpdate() != 1){
                throw new RuntimeException("Failed to add the attendance");
            }
            txtStudentID.clear();
            txtStudentID_OnAction(null);

        }*/

        catch (Throwable e) {
            new DepAlert(Alert.AlertType.ERROR, "Failed to save the attendance, try again",
                    "Failure", "Error", ButtonType.OK).show();
        }
    }
}
