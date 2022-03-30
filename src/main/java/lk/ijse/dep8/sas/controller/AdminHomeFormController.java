package lk.ijse.dep8.sas.controller;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep8.sas.security.SecurityContextHolder;

import java.io.IOException;

public class AdminHomeFormController {
    public Button btnBackupRestore;
    public Button btnRecordAttendance;
    public Button btnViewReports;
    public Label lbltxt;
    public Label root;

    public void initialize(){
        root.setOnKeyReleased(event -> {
            switch (event.getCode()){
                case F1:btnRecordAttendance.fire();


            }
        });
    }

    public void btnBackupRestore_OnAction(ActionEvent actionEvent) throws IOException {
        System.out.println("AA");
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/RecordAttendanceForm.fxml"));
        Scene attendanceScene = new Scene(root);

        Stage stage = new Stage();
        stage.setTitle("Student Attendance System: Record Attendance");
        stage.setScene(attendanceScene);
        System.out.println("ss");
        stage.setResizable(false);
        stage.initOwner(btnRecordAttendance.getScene().getWindow());
        System.out.println("dwh");
        stage.show();

    }

    public void btnManageUsers_OnAction(ActionEvent actionEvent) {
    }

    public void btnUserProfile_OnAction(ActionEvent actionEvent) {
    }

    public void btnViewReports_OnAction(ActionEvent actionEvent) {
    }

    public void btnRecordAttendance_OnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/BackAndRestroreForm.fxml"));
        Scene attendanceScene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Student Attendance System: Record Attendance");
        stage.setScene(attendanceScene);
        stage.setResizable(false);
        stage.initOwner(btnRecordAttendance.getScene().getWindow());
        stage.show();

        Platform.runLater(()->{
            stage.sizeToScene();
            stage.centerOnScreen();
        });

    }

    public void btnSignOut_OnAction(ActionEvent actionEvent) throws IOException {
     ///////////////////////   SecurityContextHolder.clear();
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/LoginForm.fxml"));
        Scene loginScene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(loginScene);
        stage.setTitle("Student Attendance System: Log In");
        stage.setResizable(false);
        stage.show();

        Platform.runLater(()->{
            stage.sizeToScene();
            stage.centerOnScreen();
        });

        JFXPanel btnSignOut;
     //  ///////////////////// ((Stage)(btnSignOut.getScene().getWindow())).close();

    }
    public void initUsername(String a){
lbltxt.setText(a);
    }
}
