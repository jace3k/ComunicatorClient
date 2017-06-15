import jack.Manager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class App extends Application
{
    public static void main( String[] args )
    {
        launch(args);
    }

    @SuppressWarnings("ConstantConditions")
    public void start(Stage primaryStage) throws Exception {
        Manager.getInstance().setStage(primaryStage);



        ClassLoader classLoader = getClass().getClassLoader();
        Image image = new Image(classLoader.getResource("icon.png").toExternalForm());
        primaryStage.getIcons().add(image);
        Parent root = FXMLLoader.load(classLoader.getResource("login_screen.fxml"));
        primaryStage.setTitle("Communicator Client");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }
}
