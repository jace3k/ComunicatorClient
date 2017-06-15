package jack;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    TextField login_field;
    @FXML
    TextField password_field;
    @FXML
    Label success_label;

    Manager man = Manager.getInstance();
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void registerAction(ActionEvent actionEvent) {
        man.connection = new Connection(man.getAddress(), man.getPort());
        Manager.getInstance().receiver.setRegisterController(this);
        man.connection.sendMessage(Connection.REGISTER_REQUEST, man.getNick(), man.getNick(), login_field.getText().toLowerCase() + "/" + password_field.getText());
        //success_label.setText("Kliknieto zarejestruj.");
    }

    public void InfoLabelAction(MouseEvent mouseEvent) {
        Manager.getInstance().setActiveWindow(Manager.LOGIN_SCREEN);

    }


    public void tryRegister(String line) {
        String[] linePart = line.split("/");
        if(linePart[3].equals("true")) { //wiadomość od serwera która mówi że udało się zarejestrować
            Platform.runLater(() -> success_label.setText("Udało sie!. Kliknij aby wrócić do logowania."));
        } else {
            Lo.g("Rejestracja sie nie udała.");
            Platform.runLater(() -> success_label.setText("Nie udało sie zarejestrować. Kliknij aby wrócić do logowania."));
        }
    }
}
