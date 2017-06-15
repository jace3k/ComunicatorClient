package jack;

import com.google.common.collect.Lists;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserData {
    private String selectedUser = "noname";
    public List<String> userList;

    public ArrayList<MyLabel> allLabels = new ArrayList<>();

    public static final String DRIVER = "org.sqlite.JDBC";
    public static final String DB_URL = "jdbc:sqlite:history.db";

    private java.sql.Connection conn;
    private java.sql.Statement stat;

    public UserData() {
        userList = Lists.newArrayList();

        try {
            Class.forName(UserData.DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Brak sterownika JDBC");
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(DB_URL);
            stat = conn.createStatement();
        } catch (SQLException e) {
            System.err.println("Problem z otwarciem polaczenia");
            e.printStackTrace();
        }

        try {
            stat.execute("DROP table IF EXISTS history");
            stat.execute("create table history (source VARCHAR(20), destination VARCHAR(20), message VARCHAR )");
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Blad przy tworzeniu tabeli");
        }
    }

    public String getSelectedUser() {
        return selectedUser;
    }

    public void selectUser(String name) {
        selectedUser = name.toLowerCase();
        for (MyLabel label : allLabels) {
            label.setSelected(false);
        }

    }

    public ArrayList<String> getConversationWith(String a) {
        ArrayList<String> filteredHistory = new ArrayList<>();

        try {
            ResultSet result = stat.executeQuery("SELECT * from history WHERE source='"+a+"' OR destination='"+a+"'");
            while(result.next()) {
                StringBuilder v = new StringBuilder(result.getString("source"));
                v.deleteCharAt(0);
                String source = (result.getString("source").charAt(0)+"").toUpperCase() + v.toString();
                String message = source + ": " + result.getString("message");
                Lo.g("Wiadomość z bazy: " + message);
                filteredHistory.add(message);
            }

            while(filteredHistory.size()>14) {
                filteredHistory.remove(0);
            }


        } catch (SQLException e) {
            System.err.println("Coś nie tak z SELECT history.");
            e.printStackTrace();
        }


        return filteredHistory;
    }

    public void put(String source, String destination, String msg) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement("insert into history values (?, ?, ?);");
            prepStmt.setString(1, source);
            prepStmt.setString(2, destination);
            prepStmt.setString(3, msg);
            prepStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Blad przy wstawianiu wpisu");
        }
    }

    public void updateHistory(String line) {
        String partsOfMessage[] = line.split("/");
        put(partsOfMessage[3], partsOfMessage[4], partsOfMessage[5]);
    }
}
