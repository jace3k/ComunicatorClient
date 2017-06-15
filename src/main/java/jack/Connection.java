package jack;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Connection {
    public final static int USER_REQUEST = 1;
    public final static int MESSAGE = 2;
    public final static int TYPING = 3;

    public final static int LOGIN_REQUEST = 4;
    public final static int REGISTER_REQUEST = 5;
    public final static int HISTORY_REQUEST = 6;
    public final static int ADD_FRIEND = 7;
    public final static int ACTIVE_REQUEST = 8;

    public final static String DISCONNECTED_STATUS = "<Rozłączono>";


    private Socket socket = null;
    private Thread thread = null;
    private PrintWriter out = null;
    private Scanner in = null;

    private boolean isConnected = false;


    private Manager man = Manager.getInstance();

    public Connection(String address, int port) {
        try {
            socket = new Socket(address,port);
            Lo.g("Połączono!");
            isConnected = true;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            if (thread == null) {
                man.setReceiver(new Receiver(in));
                thread = new Thread(man.getReceiver());
                thread.start();
            }
        }catch (Exception e) {
            e.printStackTrace();
            Lo.g("Błąd łączności.");
            isConnected = false;
        }
    }

    public  void disconnect() {
        try {
            socket.close();
            thread = null;
            in.close();
            out.close();
            Lo.g("Rozłączono.");
        } catch (Exception e) {
            //e.printStackTrace();
            Lo.g("Disconnect - brak obiektów");
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void sendMessage(int header, String source, String destination, String message) {
        out.println(header + "/" + source + "/" + destination + "/" + message);
        Lo.g("Wiadomość wysłana:" + header + "/" + source + "/" + destination + "/" + message);
    }

    public void reconnect() {
    }
}
