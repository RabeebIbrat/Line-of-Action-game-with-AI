package GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GUIMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    static Stage window;
    static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;
        new StartupWindow().display();
        /*States.boardLength = 6;
        States.startBoard();
        new GameWindow().display();*/
    }
}
