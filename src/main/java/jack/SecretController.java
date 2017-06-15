package jack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SecretController implements Initializable {
    @FXML
    TextField server_field;
    @FXML
    TextField port_field;
    @FXML
    TextField login_field;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void okAction(ActionEvent actionEvent) {
        Manager.getInstance().setPort(Integer.valueOf(port_field.getText()));
        Manager.getInstance().setAddress(server_field.getText());
        Manager.getInstance().setNick(login_field.getText());
        Manager.getInstance().setActiveWindow(Manager.LOGIN_SCREEN);
    }
}
