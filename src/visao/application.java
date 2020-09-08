package visao;

import java.io.*;
import static javafx.application.Application.launch;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.ENTER;
import javafx.stage.Stage;

public class application extends Application {

    private Parent parent;
    private FXMLLoader fxmlLoader;
    private static FXMLDocumentController controller;
    private byte[] receiveData = new byte[1024];

    @Override
    public void start(Stage stage) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(8888);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("Aguardando msg!");
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    try {
                        System.out.println("Mensagem Recebida!");
                        serverSocket.receive(receivePacket);
                        String sentence = new String(receivePacket.getData());
                        InetAddress ipAddress = receivePacket.getAddress();
                        String msg[] = sentence.split(" - ");
                        String esp = msg[0];
                        
                        if(!esp.contains("/")){
                            String recebido = msg[1];
                            ListView<String> espOn = controller.getEspRede();
                            if (!espOn.getItems().contains(esp)) {
                                controller.getEspRede().getItems().add(esp);
                            }
                            if (recebido.compareTo("bye") == 0) {
                                controller.getEspRede().getItems().remove(esp);
                            } else {
                                controller.getMensagemRecebida().appendText(esp + " -> " + recebido + "\n");
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
            System.out.println("Erro ao iniciar a tela de cadastro de Estado " + ex.getMessage());
        }
        controller = (FXMLDocumentController) fxmlLoader.getController();
        Scene scene = new Scene(parent);

        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });

        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                try {
                    controller.enviar();
                } catch (IOException ex) {
                    Logger.getLogger(application.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        stage.setScene(scene);
        stage.setTitle("Comando UDP");
        stage.setResizable(false);
        stage.show();
    }

    public static FXMLDocumentController getController() {
        return controller;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
