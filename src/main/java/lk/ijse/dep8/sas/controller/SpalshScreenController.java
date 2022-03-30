package lk.ijse.dep8.sas.controller;


import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.dep8.sas.util.DBConnection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.Thread.sleep;

public class SpalshScreenController {

    //private final SimpleDoubleProperty prg = new SimpleDoubleProperty(0.0);
   // private final SimpleStringProperty status = new SimpleStringProperty("Wait....");

    public ProgressBar prgsBar;
    public Label statusText;
    public SimpleObjectProperty<File> fileProperty= new SimpleObjectProperty<>();
    public void initialize() {
        establishDBConnection();
    }

    private void establishDBConnection() {
        statusText.setText("Establishing DB Connection..");

        new Thread(() -> {

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dep8_student_attendance", "root", "root");
                Platform.runLater(() -> {
                    loadLoginForm(connection);
                });


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                shutdownApp(e);
            } catch (SQLException e) {

                //Platform.runLater(this::loadImportDBForm);
                /* Let's find out whether the DB exists or not */

                if (e.getSQLState().equals("42000")) {Platform.runLater(this::loadImportDBForm);
                }else{
                    shutdownApp(e);
                    e.printStackTrace();
                }
            }

        }).start();
    }
    //private

    private void loadLoginForm(Connection connection){
        /* Let's store the connection first */
        DBConnection.getInstance().init(connection);

        /* Let's redirect to log in form */
        try{
            Stage stage = new Stage();
            AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/LoginForm.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Student Attendance System: Log In");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.sizeToScene();
            stage.show();

            /* Let's close the splash screen eventually */
            ((Stage)(statusText.getScene().getWindow())).close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    private void loadImportDBForm() {
        try {
            Stage stage = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/ImportDB.fxml"));
            AnchorPane root = fxmlLoader.load();
            ImportDBFormController controller = fxmlLoader.getController();

            controller.initFileProperty(fileProperty);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setTitle("Student Attendance System: First Time Boot");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(statusText.getScene().getWindow());
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(statusText.getScene().getWindow());
            stage.centerOnScreen();
            stage.setOnCloseRequest(event -> {
                event.consume();
            });

            stage. showAndWait();

            if (fileProperty.getValue() == null) {
                statusText.setText("Creating a new DB..");
                new Thread(() -> {
                    try {
                        sleep(100);
                        Platform.runLater(() -> statusText.setText("Loading database script.."));

                        InputStream is = this.getClass().getResourceAsStream("/asserts/dbScript.sql");
                        byte[] buffer = new byte[is.available()];
                        is.read(buffer);
                        String script = new String(buffer);
                        sleep(100);

                        Connection connection = DriverManager.
                                getConnection("jdbc:mysql://localhost:3306?allowMultiQueries=true", "root", "root");
                        Platform.runLater(() -> statusText.setText("Execute database script.."));
                        Statement stm = connection.createStatement();
                        stm.execute(script);
                        connection.close();
                        sleep(100);

                        Platform.runLater(() -> statusText.setText("Obtaining a new DB Connection.."));
                        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dep8_student_attendance", "root", "root");
                        DBConnection.getInstance().init(connection);
                       // System.out.println("AAASSSAS");

                        sleep(100);

                        Platform.runLater(() -> {
                            statusText.setText("Setting up the UI..");
                           loadCreateAdmin();
                        });

                        /*Connection bConnection = connection;
                        Platform.runLater(()->{
                            loadLoginForm(bConnection);
                        });*/

                    } catch (IOException | SQLException e) {
                        //////shutdownApp
                        e.printStackTrace();
                    }
                }).start();
            } else {
                /* Todo: Restore the backup */
                System.out.println("Restoring...!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadLoginForm(){

    }
    private void loadCreateAdmin()  {
        try {
            Stage stage = new Stage();
            AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/CreateAdminForm.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Student Attendance System: Create Admin");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.sizeToScene();
            stage.show();

            /* Let's close the splash screen eventually */
            ((Stage)(statusText.getScene().getWindow())).close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dropDatabase(){
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "mysql");
            Statement stm = connection.createStatement();
            stm.execute("DROP DATABASE IF EXISTS dep8_student_attendance");
            connection.close();
        } catch (SQLException e) {
            shutdownApp(e);
        }
    }


    private void shutdownApp(Throwable t){
        Platform.runLater(() -> statusText.setText("Failed to initialize the app"));

        sleep(2000);

        if (t != null)
            t.printStackTrace();

        System.exit(1);

    }

    private void sleep(long millis){
       try {
           Thread.sleep(millis);
       } catch (Exception e) {
           e.printStackTrace();
       }
    }
}