import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.*;

/**
 * Created by Jacek on 06.04.2017.
 */
public class MainController implements Initializable, Runnable {

    @FXML BorderPane bp;

    @FXML TextArea read_area;
    @FXML TextField send_field;
    @FXML TextField nick_field;

    @FXML Button send_button;
    @FXML Button refresh_button;
    @FXML Button connect_button;
    @FXML Button disconnect_button;

    @FXML ListView active_list;

    @FXML Label info_write_label;

    private int selectedUser;
    private Map<String, Integer> users;
    private Map<String, ArrayList<String>> history = new HashMap<>();

    private Socket socket = null;
    private Thread thread = null;
    private PrintWriter out = null;
    private Scanner in = null;

    private ObservableList<String> emptyList = FXCollections.observableArrayList();
    private String nick = "Jonny";

    private boolean sent = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //connect();
        disconnectedStatus();
        emptyList.add("<Rozłączono>");
        Platform.runLater(() -> active_list.setItems(emptyList));
        send_button.setOnAction(event -> {
            if(!send_field.getText().equals(""))
            sendMessage(socket.getLocalPort(), selectedUser, send_field.getText());
            send_field.setText("");
        });

        send_field.setOnKeyReleased(event -> {

            if(!send_field.getText().equals("") && !sent)
            sendMessage(1,selectedUser, "@" + nick + " pisze...");
            sent = true;
            if(send_field.getText().equals("")) {
                sendMessage(1, selectedUser, "!");
                sent = false;
            }

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

                read_area.clear();
                for(String key : history.keySet()) {
                    if(key.equals(a)) {
                        ArrayList<String> list = history.get(a);
                        for(String msg : list) {
                            read_area.appendText(msg+"\n");
                        }
                    }
                }
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
        Platform.runLater(() -> active_list.setItems(emptyList));



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
            socket = new Socket("192.168.1.111",8189);
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
        history = new HashMap<>();
        out.println(nick);
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    private boolean sendMessage(int myPort, int destPort, String msg) {
        if(msg.matches("(.*)~(.*)")) {
            msg = msg.replaceAll("~"," ");
        }
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
        read_area.setText("Koniec połączenia.\n");
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
        } else if(line.matches("@(.*)")) {
            setInfoLabel(line, true);
        } else if(line.equals("!")) {
            setInfoLabel(line, false);
        } else {
            //read_area.appendText(line + "\n");
            showMessage(selectedUser, line);


        }
    }

    private void setInfoLabel(String line, boolean visible) {
        Platform.runLater(() -> info_write_label.setText(line));
        info_write_label.setVisible(visible);
    }

    private void showMessage(int selectedUser, String line) {
        // czy wybrany użytkownik na liście zgadza się z nazwą która przyszła od serwera
        try {
            String splitMessage[] = line.split(":");

            /////////// zapisywanie historii //////////////////////
            if(!history.containsKey(splitMessage[0])) {
                ArrayList<String> hisList = new ArrayList<>();
                hisList.add(line);
                history.put(splitMessage[0], hisList);
            } else {
                history.get(splitMessage[0]).add(line);
            }

            if(splitMessage[0].equals(nick)) {
                for(String key : users.keySet()) {
                    if(users.get(key) == selectedUser) {
                        try{
                            history.get(key).add(line);
                        }catch (Exception e) {
                            ArrayList<String> hisList = new ArrayList<>();
                            hisList.add(line);
                            history.put(key,hisList);
                        }
                    }
                }
            }
            ///////////////////////////////////////////////////////

            int port = users.get(splitMessage[0]);
            if (selectedUser == port || socket.getLocalPort() == port) read_area.appendText(line+"\n");
        }catch (Exception e) {
                read_area.appendText(line + "\n");
        }
    }

    private void updateUsersList() {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (String key : users.keySet()) items.add(key);
        Platform.runLater(() -> active_list.setItems(items));
    }
}
