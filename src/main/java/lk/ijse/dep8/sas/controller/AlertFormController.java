package lk.ijse.dep8.sas.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlertFormController {
    public AnchorPane lblStatus;
    public Label lblName;
    public Label lblDate;
    public Label lblIds;
    public Button btnContinue;
    public Button btnCallPolice;
    private SimpleObjectProperty<Boolean> b1;

    public void initialize() throws URISyntaxException {
        playSiran();

    }
public void playSiran() throws URISyntaxException {
    Media media = new Media(this.getClass().getResource("/asserts/Sirengrp1_tone1.mp3").toURI().toString());
    MediaPlayer player = new MediaPlayer(media);
    player.play();
}


    public void initData(String studentID, String studentName, LocalDateTime date,boolean in){
        lblIds.setText("ID: " + studentID.toUpperCase());
        lblName.setText("NAME: " + studentName.toUpperCase());
        lblDate.setText(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")) + " - " + (in? "IN" : "OUT"));

    }
    public void initFilepropertyForSave(SimpleObjectProperty<Boolean> b1){
this.b1=b1;


    }


    public void btnCallPoliceOnAction(ActionEvent actionEvent) {
        b1.setValue(false);
    }

    public void btnContinueOnAction(ActionEvent actionEvent) {
        b1.setValue(true);
        ( (Stage)(lblDate.getScene().getWindow())).close();
        System.out.println(b1.getValue());

    }
}
