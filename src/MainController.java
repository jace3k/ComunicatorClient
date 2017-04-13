import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Created by Jacek on 06.04.2017.
 */
public class MainController implements Initializable, Runnable {

    @FXML TextArea read_area;
    @FXML TextField send_field;
    @FXML TextField nick_field;

    @FXML Button send_button;
    @FXML Button refresh_button;
    @FXML Button connect_button;
    @FXML Button disconnect_button;

    @FXML ListView active_list;

    private int selectedUser;
    private Map<String, Integer> users;

    private Socket socket = null;
    private Thread thread = null;
    private PrintWriter out = null;
    private Scanner in = null;

    private ObservableList<String> emptyList = FXCollections.observableArrayList();
    private String nick = "Jonny";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //connect();
        disconnectedStatus();
        emptyList.add("<Rozłączono>");
        active_list.setItems(emptyList);
        send_button.setOnAction(event -> {
            if(!send_field.getText().equals(""))
            sendMessage(socket.getLocalPort(), selectedUser, send_field.getText());
            send_field.setText("");
        });

        connect_button.setOnAction(event -> {
            nick = nick_field.getText();
            disconnect();
            connect();
            connectedStatus();
        });

        disconnect_button.setOnAction(event -> {
            disconnect();
            disconnectedStatus();
        });

        refresh_button.setOnAction(event -> updateUsersList());

        active_list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String a = (String) observable.getValue();
            System.out.println("Selected: " + a);
            if(a != null && !a.equals("<Rozłączono>")) {
                send_button.setDisable(false);
                selectedUser = users.get(a);
            } else {
                selectedUser = 0;
                send_button.setDisable(true);
            }
        });
    }

    private void connectedStatus() {
        nick_field.setDisable(true);
        connect_button.setDisable(true);
        disconnect_button.setDisable(false);
        refresh_button.setDisable(false);
    }
    private void disconnectedStatus() {
        nick_field.setDisable(false);
        connect_button.setDisable(false);
        disconnect_button.setDisable(true);
        refresh_button.setDisable(true);
        send_button.setDisable(true);
        active_list.setItems(emptyList);
    }

    private void disconnect() {
        try {
            socket.close();
            thread = null;
            in.close();
            out.close();
            System.out.println("Rozłączono.");
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Disconnect - brak obiektów");
        }
    }

    private void connect() {
        try {
            socket = new Socket("localhost",8189);
            System.out.println("Połączono!");
            start();
        }catch (Exception e) {
            e.printStackTrace();
            read_area.appendText("Błąd łączności.\n");
        }
    }

    private void start() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());
        out.println(nick);
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    private boolean sendMessage(int myPort, int destPort, String msg) {
        try{
            out.println(myPort+"~"+destPort+"~"+msg);
            return true;
        } catch (Exception e) {
            System.out.println("Nie wysłano.");
            return false;
        }
    }



    /////////////////////// drugi wątek ////////////////


    @Override
    public void run() {
        while (in.hasNextLine()) {
            String line = in.nextLine();
            System.out.println(line);
            checkMessage(line);
        }
        read_area.appendText("Koniec połączenia.\n");
        disconnectedStatus();
    }

    private synchronized void checkMessage(String line) {
        if (line.equals("~qxqxstart")){

            users = new HashMap<>();
            line = in.nextLine();
            while(!line.equals("~qxqxend")) {
                if(!line.equals("~qxqxend")) {
                    String userek[] = line.split("~");
                    int userek0 = Integer.parseInt(userek[0]);
                    if(userek0 != socket.getLocalPort()) {
                        users.put(userek[1],Integer.parseInt(userek[0]));
                    }
                }
                updateUsersList();
                line = in.nextLine();
            }
        }
        else {
            read_area.appendText(line + "\n");
        }
    }

    private void updateUsersList() {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (String key : users.keySet()) {
            items.add(key);
        }
        Platform.runLater(() -> active_list.setItems(items));
    }
}
