package GUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertWindow {

    Stage alertWindow;
    Scene alertScene;

    public void display(String windowTitle, String message, String buttonText) {
        alertWindow = new Stage();
        alertWindow.setTitle(windowTitle);
        alertWindow.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        //layout elements
        Label label = new Label(message);
        Button button = new Button(buttonText);
        button.setOnAction(action -> alertWindow.close());
        layout.getChildren().addAll(label, button);

        alertScene = new Scene(layout,300,80);
        alertWindow.setScene(alertScene);
        alertWindow.showAndWait();
    }
}
