package jack;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Manager {
    private static Manager ourInstance = new Manager();

    public final static String LOGIN_SCREEN = "login_screen.fxml";
    public final static String REGISTER_SCREEN = "register_screen.fxml";
    public final static String SECRET_SCREEN = "secret_screen.fxml";
    public final static String MAIN_SCREEN = "main_screen.fxml";

    private String nick = "noname";
    private String address = "0.tcp.ngrok.io";
    private int port = 15820;

    public MainController mainController;
    public LoginController loginController;

    public UserData userData;
    public Connection connection;
    public Receiver receiver;

    private transient Stage stage;



    public static Manager getInstance() {
        return ourInstance;
    }

    private Manager() {
    }

    public void setActiveWindow(final String window) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Parent root = FXMLLoader.load(classLoader.getResource(window));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getNick() {
        return nick;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }
}
