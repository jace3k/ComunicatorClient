import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Jacek on 06.04.2017.
 */
public class MainController implements Initializable {

    @FXML TextArea read_area;
    @FXML TextField send_field;

    @FXML Button send_button;
    @FXML Button refresh_button;
    @FXML Button connect_button;
    @FXML Button disconnect_button;

    @FXML ListView active_list;

    private Thread t;
    private Client client;
    private int portToSend;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createNewThreadAndConnect();
        setElements();
    }

    private void setElements() {
        read_area.setEditable(false);
        send_button.setDefaultButton(true);

        send_button.setOnAction(event -> {
            client.sendMessage(send_field.getText(),portToSend);
            send_field.setText("");
        });

        refresh_button.setOnAction(event -> active_list.setItems(client.getUsers()));

        active_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                portToSend = client.ports.get(active_list.getSelectionModel().getSelectedIndex());
                send_button.setDisable(false);
            }
        });

        disconnect_button.setOnAction(event -> {
            client.closeSocket();
            System.exit(0);
        });

        connect_button.setOnAction(event -> {
            client.closeSocket();
            createNewThreadAndConnect();
        });
    }

    private void createNewThreadAndConnect() {

        t = new Thread(() -> {
            client = new Client();
            if(client.connect(read_area)) read_area.appendText("Połączenie zakończone.\n");
            else read_area.appendText("Rozłączono.\n");
        });
        t.start();
    }
}
