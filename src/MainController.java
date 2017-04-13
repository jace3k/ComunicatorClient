import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    @FXML Button send_button;
    @FXML Button refresh_button;
    @FXML Button connect_button;
    @FXML Button disconnect_button;

    @FXML ListView active_list;

    int selectedUser;
    Map<String, Integer> users;

    Socket socket = null;
    Thread thread = null;
    PrintWriter out = null;
    Scanner in = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connect();
        send_button.setDisable(false);
        send_button.setOnAction(event -> {
            if(!send_field.getText().equals(""))
            sendMessage(socket.getLocalPort(), selectedUser, send_field.getText());
        });


        connect_button.setOnAction(event -> {
            disconnect();
            connect();
        });

        disconnect_button.setOnAction(event -> {
            disconnect();
        });

        refresh_button.setOnAction(event -> updateUsersList());

        active_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                String a = (String) observable.getValue();
                System.out.println("Selected: " + a);
                selectedUser = users.get(a);
            }
        });

    }

    private void disconnect() {
        try {
            socket.close();
            thread = null;
            in.close();
            out.close();
            System.out.println("Rozłączono.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket = new Socket("localhost",8189);
            System.out.println("Połączono!");
            start();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public boolean sendMessage(int myPort, int destPort, String msg) {
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
                //updateUsersList();
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
        active_list.setItems(items);
    }


}
