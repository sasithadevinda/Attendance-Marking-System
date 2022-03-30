package lk.ijse.dep8.sas.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BackAndRestoreFormController {
    public Button btnBackUp;
    public Button btnRestore;

    public void btnBackUpOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File","*.dep8"));
     //   fileChooser.getInitialFileName(LocalDate.now()+"");
        File file = fileChooser.showSaveDialog(btnBackUp.getScene().getWindow());
        if (file!=null){
            ProcessBuilder mysqlprocessBuilder = new ProcessBuilder("mysqldump", "-h", "localhost", "--port", "3306", "-u", "root", "-proot", "--add-drop-database", "--databases", "dep8_student_attendance");
            mysqlprocessBuilder.redirectOutput(file);
            if (!System.getProperty("os.name").equals("Windows")){}
            try {
                System.out.println( );

                Process start = mysqlprocessBuilder.start();
                int i = start.waitFor();
                if (i==0){
                    new Alert(Alert.AlertType.INFORMATION,"Sucseesfully").show();
                }


            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void btnRestoreOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File","*.dep8"));
        File file = fileChooser.showOpenDialog(btnBackUp.getScene().getWindow());
      //  fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File","*.dep8"));
        ProcessBuilder mysqlprocessBuilder = new ProcessBuilder("mysqldump", "-h", "localhost", "--port", "3306", "-u", "root", "-proot", "source",file.getAbsolutePath());

      mysqlprocessBuilder.redirectInput();
        try {
            Process start = mysqlprocessBuilder.start();
            int i = start.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}
