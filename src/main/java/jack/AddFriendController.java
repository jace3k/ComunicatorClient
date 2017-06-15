package jack;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddFriendController implements Initializable {
    public TextField friend_field;

    Manager man = Manager.getInstance();
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void addFriendAction(ActionEvent actionEvent) {
        man.connection.sendMessage(Connection.ADD_FRIEND, man.getNick(), man.getNick(), friend_field.getText().toLowerCase());
    }
}
