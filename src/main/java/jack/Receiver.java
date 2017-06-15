package jack;

import javafx.application.Platform;

import java.util.Scanner;

public class Receiver implements Runnable {
    private Scanner in;
    private MainController mainController;
    private LoginController loginController;
    private RegisterController registerController;

    Manager man = Manager.getInstance();

    public Receiver(Scanner in) {
        this.in = in;
    }


    @Override
    public void run() {
        while (in.hasNextLine()) {
            String line = in.nextLine();
            Lo.g("Odebrano: " + line);
            analyze(line);

        }
        Lo.g("Koniec połączenia.");
        Platform.runLater(() -> mainController.title_label.setText("Rozłączono."));
    }

    private synchronized void analyze(String line) {
        switch (line.charAt(0)) {
            case '1':
                mainController.updateUsersList(line);
                break;
            case '2':
                mainController.messageReceived(line);
                break;
            case '3':
                mainController.someoneTyping(line);
                break;
            case '4':
                loginController.tryLogin(line);
                break;
            case '5':
                registerController.tryRegister(line);
                break;
            case '6':
                man.userData.updateHistory(line);
                break;
            case '8':
                mainController.setActiveUsers(line);
                break;
            case '9':
                //man.connection.sendMessage(Connection.ACTIVE_REQUEST, man.getNick(), man.getNick(), "");
        }



    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public void setRegisterController(RegisterController registerController) {
        this.registerController = registerController;
    }
}
