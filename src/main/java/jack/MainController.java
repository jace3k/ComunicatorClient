package jack;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public TextArea read_area;
    @FXML
    public TextField send_field;
    @FXML
    public Label title_label;
    @FXML
    public VBox vbox_users;
    @FXML
    public Label typing_label;


    private Manager man = Manager.getInstance();

    boolean iAmWriting = false;
    Tray tray = new Tray();

    private ObservableList<String> emptyList = FXCollections.observableArrayList(Connection.DISCONNECTED_STATUS);


    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {
        man.getReceiver().setMainController(this);
        Platform.runLater(() -> title_label.setText("Zalogowny jako: " + man.getNick().toUpperCase()));
        man.connection.sendMessage(Connection.USER_REQUEST, man.getNick(), man.getNick(), "");


        Platform.runLater(() -> vbox_users.getChildren().clear());

        typing_label.setVisible(false);
    }

    @SuppressWarnings("unchecked")
    public void updateUsersList(String line) {
        Lo.g("Update users..");
        String[] users = line.split("/"); // może być problem przy 0 userach.
        List<String> mainUserList = new ArrayList<>();
        for(int i = 1; i < users.length; i++) {
            if(!users[i].equals(man.getNick())) {
                mainUserList.add(users[i]);
                Lo.g("Aktywny: " + users[i]);
            }
        }

        Platform.runLater(() -> {
            vbox_users.getChildren().clear();
            man.userData.allLabels.clear();
            for(String user : mainUserList) {
                vbox_users.getChildren().add(new MyLabel(user, read_area));
            }

                man.connection.sendMessage(Connection.ACTIVE_REQUEST, man.getNick(), man.getNick(), "");


        });

    }

    public void messageReceived(String line) {

        String partsOfMessage[] = line.split("/");

        man.userData.put(partsOfMessage[1], partsOfMessage[2], partsOfMessage[3]);

        if (man.userData.getSelectedUser().equals(partsOfMessage[1]) || man.getNick().equals(partsOfMessage[1])) {
            read_area.appendText(partsOfMessage[1] + ": " + partsOfMessage[3] + "\n");
        }

        if (!man.userData.getSelectedUser().equals(partsOfMessage[1]) && !man.getNick().equals(partsOfMessage[1])) {
            tray.display(partsOfMessage[1] + " napisał(a)!", partsOfMessage[3]);

            for(MyLabel label : man.userData.allLabels) {
                if(label.getText().toLowerCase().equals(partsOfMessage[1])) {
                    label.setBold(true);
                    Lo.g("POGRUBIONO: "+partsOfMessage[1]);

                    ///tutaj dźwięk

                    ClassLoader classLoader = getClass().getClassLoader();
                    File file = new File(classLoader.getResource("msg.mp3").getFile());

                    new MediaPlayer(new Media(file.toURI().toString())).play();

                }
            }

        }
    }

    public void addFriendAction(ActionEvent actionEvent) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Parent root = FXMLLoader.load(classLoader.getResource("addFriend_modal.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            //stage.initOwner(((Node)actionEvent.getSource()).getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void connectAction(ActionEvent actionEvent) {
        man.connection.disconnect();
        man.connection = new Connection(man.getAddress(), man.getPort());
        //connection = new Connection("localhost", 8189, this);
        man.userData = new UserData();
        man.getReceiver().setMainController(this);
        Platform.runLater(() -> title_label.setText("Zalogowny jako: " + man.getNick()));
        man.connection.sendMessage(Connection.USER_REQUEST, man.getNick(), man.getNick(), "");
        man.connection.sendMessage(Connection.HISTORY_REQUEST, man.getNick(), man.getNick(), "");

    }

    public void disconnectAction(ActionEvent actionEvent) {
        man.connection.disconnect();
        man.setActiveWindow(Manager.LOGIN_SCREEN);
    }

    public void refreshAction(ActionEvent actionEvent) {
        man.connection.sendMessage(Connection.ACTIVE_REQUEST, man.getNick(), man.getNick(), "");
    }

    public void exitAction(ActionEvent actionEvent) {
        man.connection.disconnect();
        System.exit(0);
    }

    public void sendAction(ActionEvent actionEvent) {
        if(!send_field.getText().equals("") && !(man.userData.getSelectedUser()==null)) {
            String message = send_field.getText().replace("/","-");

            man.connection.sendMessage(Connection.MESSAGE, man.getNick(), man.userData.getSelectedUser(), message);
            send_field.clear();
            iAmWriting = false;
            man.connection.sendMessage(Connection.TYPING, man.getNick(), man.userData.getSelectedUser(), "notyping");
        }
    }

    private boolean sent;
    private boolean sent2 = false;
    public void keyPressedAction(KeyEvent keyEvent) {
        if(!send_field.getText().equals("") && !sent) {
            man.connection.sendMessage(Connection.TYPING, man.getNick(), man.userData.getSelectedUser(), "typing");
            sent2 = true;
        }
        sent = true;

        if(send_field.getText().equals("")) {

            if (send_field.getText().equals("") && sent2) {
                man.connection.sendMessage(Connection.TYPING, man.getNick(), man.userData.getSelectedUser(), "notyping");
                sent2=false;
            } else if(sent2) {
                man.connection.sendMessage(Connection.TYPING, man.getNick(), man.userData.getSelectedUser(), "typing");
            }
            sent = false;
        }
    }

    public void someoneTyping(String line) {
        String[] partsOfMessage = line.split("/");
        if(partsOfMessage[3].equals("typing")) {
            Platform.runLater(() -> typing_label.setText(partsOfMessage[1] + " pisze.."));
            typing_label.setVisible(true);
        } else {
            typing_label.setVisible(false);
        }
    }

    public void setActiveUsers(String line) {
        Lo.g("Ustawiam aktywnych..");
        String partsOfMessage[] = line.split("/");
        for(MyLabel label : man.userData.allLabels) {
            label.setActive(false);
        }

        for(MyLabel label : man.userData.allLabels) {
            for(String part : partsOfMessage) {
                if(label.getText().toLowerCase().equals(part)) {
                    label.setActive(true);
                    Lo.g("Aktywny: " + label.getName());
                }
            }
        }
    }
}
