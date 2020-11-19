/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visao;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tales
 */
public class WifiEditController implements Initializable {

    private Stage stage;
    private String comando;
    
    private final String[] menu = {"Conectar","Criar"};
    
    private final ObservableList list = FXCollections.observableArrayList();
    
    @FXML
    private Button btenviarWifi;
    
    @FXML
    private TextField tfwifiname;
    
    @FXML
    private TextField tfwifipass;
    @FXML
    private ChoiceBox<String> cbModo;
    @FXML
    private Label lberro;
    @FXML
    private Label lberroSenha;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadData();
    }    

    @FXML
    private void configWifi() {
        String selectedMenu = (String) cbModo.getSelectionModel().getSelectedItem();
        String name = tfwifiname.getText().trim();
        String pass = tfwifipass.getText().trim();
        
        if(selectedMenu==null){
            lberro.visibleProperty().setValue(true);
        }else{
            lberro.visibleProperty().setValue(false);
            if(name.isEmpty()||pass.isEmpty()){
                lberro.visibleProperty().setValue(true);
            }else{
                if(pass.length()<8){
                    lberroSenha.visibleProperty().setValue(true);
                }else{
                    lberroSenha.visibleProperty().setValue(false);
                    comando = "/wifi/"+name+"/"+pass+"/";
                    if(selectedMenu.contains("Conectar")){
                        comando += 1;
                    }else{
                        comando += 0;
                    }
                    stage.close();
                }
            }
        }
    }

    /**
     * @return the stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * @return the comando
     */
    public String getComando() {
        return comando;
    }
    public void loadData(){
        list.removeAll(list);
        list.addAll(menu);
        cbModo.getItems().addAll(list);    
    }
    
}
