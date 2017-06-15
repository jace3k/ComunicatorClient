package jack;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    TextField login_field;
    @FXML
    PasswordField password_field;
    @FXML
    TextField port_field;
    @FXML
    Label title_label;

    Manager man = Manager.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        port_field.setText(String.valueOf(man.getPort()));
    }

    public void login_action(ActionEvent actionEvent) {
        man.setPort(Integer.valueOf(port_field.getText()));
        man.connection = new Connection(man.getAddress(), man.getPort());
        man.getReceiver().setLoginController(this);
        man.connection.sendMessage(Connection.LOGIN_REQUEST, man.getNick(), man.getNick() ,login_field.getText().toLowerCase() + "/" + password_field.getText());

    }

    public void openSecretMenu(MouseEvent mouseEvent) {
        Manager.getInstance().setActiveWindow(Manager.SECRET_SCREEN);
    }


    public void openRegister(MouseEvent mouseEvent) {
        man.setPort(Integer.valueOf(port_field.getText()));
        Manager.getInstance().setActiveWindow(Manager.REGISTER_SCREEN);
    }

    public void tryLogin(String line) {
        man.setPort(Integer.valueOf(port_field.getText()));
        String[] linePart = line.split("/");
        if(linePart[3].equals("true")) { //wiadomość od serwera która mówi że udało się zalogować
            man.setNick(linePart[1]);
            man.userData = new UserData();
            man.connection.sendMessage(Connection.HISTORY_REQUEST, man.getNick(), man.getNick(), "");
            Platform.runLater(() -> man.setActiveWindow(Manager.MAIN_SCREEN));

        } else {
            Lo.g("Logowanie się nie powiodło.");
            Platform.runLater(() ->  title_label.setText("Niepowodzenie :("));
                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            Platform.runLater(() -> title_label.setText("Placek"));

        }
    }
}
