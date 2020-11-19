package visao;

import static javafx.application.Application.launch;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class application extends Application {

    private Parent parent;
    private FXMLLoader fxmlLoader;
    private static FXMLDocumentController controller;
    private byte[] receiveData = new byte[1024];
    
    private final String[] menu = {"Botão","WiFi Mode"};
    
    private final ObservableList list = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) throws Exception {
        
        DatagramSocket serverSocket = new DatagramSocket(8888);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    try {
                        serverSocket.receive(receivePacket);
                        String sentence = new String(receivePacket.getData());
                        InetAddress ipAddress = receivePacket.getAddress();
                        String msg[] = sentence.split("/");
                        if(msg[0].contains("PLACA")){
                            System.out.println("Msg!");
                            ListView<String> espOn = controller.getEspRede();
                            if (!espOn.getItems().contains(msg[1])) {
                                controller.getEspRede().getItems().add(msg[1]);
                            }else {
                                controller.getMensagemRecebida().appendText(msg[1] + " -> " + msg[2]);
                            }
                            receiveData = new byte[1024];
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();

        Parent parent = null;
        FXMLLoader fxmlLoader = null;
        
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            parent = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            System.out.println("Erro " + ex.getMessage());
        }
        
        controller = (FXMLDocumentController) fxmlLoader.getController();
        Scene scene = new Scene(parent);

        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });

        stage.setScene(scene);
        stage.setTitle("Comando UDP");
        stage.setResizable(false);
        stage.show();
        controller.loadData();
    }
    
  

    public static FXMLDocumentController getController() {
        return controller;
    }

    public static void main(String[] args) {
        launch(args);
        
    }
    
}