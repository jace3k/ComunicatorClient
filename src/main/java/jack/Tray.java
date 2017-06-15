package jack;

import javafx.application.Platform;

import javax.swing.*;
import java.awt.*;

public class Tray {
    private String text;
    private String small;
    SystemTray tray = SystemTray.getSystemTray();
    TrayIcon trayIcon;

    public Tray() {
        if (!SystemTray.isSupported()) System.out.println("Tray nie wspierany!");

        ClassLoader classLoader = getClass().getClassLoader();
        ImageIcon icon = new ImageIcon(classLoader.getResource("icon.png"));
        Image image = icon.getImage();
        trayIcon = new TrayIcon(image, "Trajek");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Co jest mały kurwiu?");


        PopupMenu popup = new PopupMenu();

        MenuItem disconnectItem = new MenuItem("Rozłącz");
        disconnectItem.addActionListener(s -> {
            Manager.getInstance().connection.disconnect();
            Platform.runLater(() -> Manager.getInstance().setActiveWindow(Manager.LOGIN_SCREEN));
        });

        popup.add(disconnectItem);

        trayIcon.setPopupMenu(popup);


        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void display(String text, String small) {


        trayIcon.displayMessage(text, small, TrayIcon.MessageType.INFO);
    }
}
