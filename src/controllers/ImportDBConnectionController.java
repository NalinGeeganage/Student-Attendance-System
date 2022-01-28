package controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ImportDBConnectionController {

    public ToggleGroup abc;
    public TextField txtBrowse;
    public Button btnBrowse;
    public Button btnOk;
    public RadioButton rdoRestore;
    public RadioButton rdoBoot;
    private SimpleObjectProperty<File> fileProperty;

    public void initFileProperty(SimpleObjectProperty<File> fileProperty){
        this.fileProperty = fileProperty;
    }

    public void initialize(){

        txtBrowse.setEditable(false);
//        rdoRestore.selectedProperty().addListener((observable, oldValue, newValue) ->
//                btnOk.setDisable(txtBrowse.getText().isEmpty() && newValue));
    }

    public void txtBrowseClickOnAction(ActionEvent event) {
    }

    public void btnBrowseClickOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select backup file");
        fileChooser.getExtensionFilters().add(new
                FileChooser.ExtensionFilter("Backup file", "*.dep8backup"));
        File file = fileChooser.showOpenDialog(btnOk.getScene().getWindow());
        txtBrowse.setText(file != null? file.getAbsolutePath() : "");
        fileProperty.setValue(file);

    }
    public void btnOkClickOnAction(ActionEvent event) {
        if (rdoBoot.isSelected()){
            fileProperty.setValue(null);
        }
        ((Stage)(btnOk.getScene().getWindow())).close();
    }
}
