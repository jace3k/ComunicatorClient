import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Jacek on 06.04.2017.
 */

public class Client {

    private Socket socket;

    private ObservableList<String> users;
    ArrayList<Integer> ports;

    private Scanner in;
    private PrintWriter out;


    public boolean connect(TextArea tf) {
        try(Socket soc = new Socket("192.168.1.111",8189) )  {
            System.out.println("Połączono");
            socket = soc;
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(),true);

            while(in.hasNextLine()) {
                String line = in.nextLine();
                System.out.println(line);
                    if (line.equals("qxqxstart")){
                        line = in.nextLine();
                        users = FXCollections.observableArrayList();
                        ports = new ArrayList<>();
                        while(!line.equals("qxqxend")) {
                            if(!line.equals("qxqxend")) {
                                String userek[] = line.split("~");
                                ports.add(Integer.parseInt(userek[0]));
                                users.add(userek[1]);
                            }
                            line = in.nextLine();
                        }
                    }
                    else {
                        tf.appendText(line + "\n");
                    }
            }
            socket.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessage(String msg, int port) {
        try{
            out.println(port+"~"+msg);
            return true;
        } catch (Exception e) {
            System.out.println("Nie wysłano.");
            return false;
        }
    }

    public ObservableList<String> getUsers() {
        return users;
    }
}
