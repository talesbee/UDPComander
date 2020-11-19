package visao;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class FXMLDocumentController {

    public byte[] receiveData = new byte[1024];
    byte[] sendData;
    private final String[] menu = {"Botão","WiFi Mode","Nome","Reset"};
    
    private final ObservableList list = FXCollections.observableArrayList();
    
    private static WifiEditController controller;
    private static NomeEditController controller2;
    


    @FXML
    private TextArea mensagemRecebida;

    @FXML
    private Button btEnviar;

    @FXML
    private ListView<?> espRede;

    @FXML
    private Button btAtualizar;
    
    @FXML
    private Button btLimpar;
    
     @FXML
    private ChoiceBox<String> selectMenu;
    @FXML
    private Label lbmenu;
    @FXML
    private Label lbesp;
     
     @FXML
    void actionLimpar(ActionEvent event) {
        mensagemRecebida.clear();
    }

    @FXML
    void actionEnviar(ActionEvent event) throws UnknownHostException, IOException {
        enviar();
    }
    
    @FXML
    void actionAtualizar(ActionEvent event) throws SocketException, UnknownHostException, IOException, InterruptedException {
        espRede.getItems().clear();

        for(int i=0;i<3;i++){
            sendMSG("REDE");
        } 
    }
    
    public void sendMSG(String msg) throws SocketException, UnknownHostException, IOException {
        sendData = msg.getBytes();
        DatagramSocket clientSocket = new DatagramSocket();
        clientSocket.setBroadcast(true);
        InetAddress ipAddress = InetAddress.getByName("255.255.255.255");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, 8888);
        clientSocket.send(sendPacket);
        clientSocket.close();
    }

    /**
     * @return the mensagemRecebida
     */
    public TextArea getMensagemRecebida() {
        return mensagemRecebida;
    }

    /**
     * @return the usuariosOnline
     */
    public ListView<String> getEspRede() {
        return (ListView<String>) espRede;
    }

    public void enviar() throws IOException {
        String selectedMenu = (String) selectMenu.getSelectionModel().getSelectedItem();
        String selectedItem = (String) espRede.getSelectionModel().getSelectedItem();
        String msgEnviar = "";

        if(selectedMenu==null){
            lbmenu.visibleProperty().setValue(true);
        }else{
            lbmenu.visibleProperty().setValue(false);
            if(selectedItem==null){
                lbesp.visibleProperty().setValue(true);
            }else{
                lbesp.visibleProperty().setValue(false);
                switch (selectedMenu){
                    case "Botão":
                        msgEnviar=selectedItem+"/botao";
                        sendMSG(msgEnviar);
                        break;
                    case "WiFi Mode":
                        showWifiConfig();
                        msgEnviar = selectedItem;
                        msgEnviar += controller.getComando();

                        System.out.println(msgEnviar);
                        sendMSG(msgEnviar);
                        break;
                    case "Nome":
                        showNomeConfig();
                        msgEnviar = selectedItem;
                        msgEnviar += controller2.getComando();
                        sendMSG(msgEnviar);
                        espRede.getItems().clear();
                        break;
                    case "Reset":
                        msgEnviar = selectedItem;
                        msgEnviar += "/reset";
                        sendMSG(msgEnviar);
                        break;     
                    default:
                        mensagemRecebida.appendText("Sistema -> Nenhuma placa selecionada!");
                        break;
                }
            }
        }
    }
    
    public void loadData(){
        list.removeAll(list);
        list.addAll(menu);
        selectMenu.setItems(list);    
    }
    
    public void showWifiConfig() throws IOException{
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("wifiEdit.fxml"));
            Parent parent = (Parent) fxmlLoader.load(); 
 
            controller = (WifiEditController) fxmlLoader.getController();
            
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            
            controller.setStage(stage);
            
            stage.setScene(scene);
            stage.setTitle("Editando as configurações do Wifi");
            stage.setResizable(false);
            stage.showAndWait();
   
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showNomeConfig() throws IOException{
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("nomeEdit.fxml"));
            Parent parent = (Parent) fxmlLoader.load(); 
 
            controller2 = (NomeEditController) fxmlLoader.getController();
            
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            
            controller2.setStage(stage);
            
            stage.setScene(scene);
            stage.setTitle("Editando o nome do ESP");
            stage.setResizable(false);
            stage.showAndWait();
   
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}
