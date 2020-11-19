/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visao;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tales
 */
public class NomeEditController implements Initializable {
    
    private Stage stage;
    private String comando;
    
    @FXML
    private TextField lbnome;
    @FXML
    private Button btsalvar;
    @FXML
    private Label lberro;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void salvar(ActionEvent event) {
        String nome = lbnome.getText().trim();
        System.out.println("-"+nome+"- tamanho: "+nome.length());
        if(nome.length()<=0){
            lberro.visibleProperty().setValue(true);
        }else{
            comando = "/nome/"+nome;
            stage.close();
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
    
}
