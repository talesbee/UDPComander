package visao;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;

public class FXMLDocumentController {

    public byte[] receiveData = new byte[1024];
    byte[] sendData;

    @FXML
    private TextField mensagemEnviar;

    @FXML
    private TextArea mensagemRecebida;

    @FXML
    private Button btEnviar;

    @FXML
    private ListView<?> espRede;

    @FXML
    private CheckBox sensor;

    @FXML
    private CheckBox led;

    @FXML
    private Button btEnviar1;

    @FXML
    private Button btAtualizar;

    @FXML
    void actionEnviar(ActionEvent event) throws UnknownHostException, IOException {
        enviar();
    }

    @FXML
    void actionAtualizar(ActionEvent event) throws SocketException, UnknownHostException, IOException {
        String msg = "pc/rede";
        sendMSG(msg);
    }

    void sendMSG(String msg) throws SocketException, UnknownHostException, IOException {
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

    private void comandoChekBox() {

    }

    public void enviar() throws IOException {
        String selectedItem = (String) espRede.getSelectionModel().getSelectedItem();
        String msgEnviar = "";

        if (sensor.selectedProperty().getValue()) {
            msgEnviar = selectedItem + "/sensor/valor";
            System.out.println("Envia Sensor");

        } else if (led.selectedProperty().getValue()) {
            msgEnviar = selectedItem + "/led";
            System.out.println("Envia Led");
        }

        if (msgEnviar.compareTo("") == 0) {
            System.out.println("Sem msg para enviar! Error!");
        } else {
            System.out.print("Mensagem Enviada: ");
            System.out.println(msgEnviar);
            sendMSG(msgEnviar);
        }
        System.out.print(selectedItem);

    }
}
