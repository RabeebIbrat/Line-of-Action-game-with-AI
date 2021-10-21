package GUI;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StartupWindow {

    void display() {

        //layout
        VBox layout = new VBox(12);
        layout.setAlignment(Pos.CENTER);
        int choiceMenuSpace = 30;

        HBox row1 = new HBox(choiceMenuSpace);
        row1.setAlignment(Pos.CENTER);
        HBox row2 = new HBox(choiceMenuSpace);
        row2.setAlignment(Pos.CENTER);
        HBox row3 = new HBox(choiceMenuSpace);
        row3.setAlignment(Pos.CENTER);
        HBox row4 = new HBox(choiceMenuSpace);
        row4.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(row1, row2, row3, row4);

        //row1
        Label player1Label = new Label("Player 1:");
        player1Label.setFont(States.defaultFont);
        ChoiceBox<String> player1Choice = new ChoiceBox<>();
        player1Choice.getItems().addAll("Human","AI 1", "AI 2");
        player1Choice.setValue("Human");
        row1.getChildren().addAll(player1Label, player1Choice);

        //row2
        Label player2Label = new Label("Player 2:");
        player2Label.setFont(States.defaultFont);
        ChoiceBox<String> player2Choice = new ChoiceBox<>();
        player2Choice.getItems().addAll("Human","AI 1","AI 2");
        player2Choice.setValue("AI 1");
        row2.getChildren().addAll(player2Label, player2Choice);

        //row3
        Label boardSizeLabel = new Label("Board size: ");
        boardSizeLabel.setFont(States.defaultFont);
        ChoiceBox<String> boardSizeChoice = new ChoiceBox<>();
        boardSizeChoice.getItems().addAll("6x6","8x8");
        boardSizeChoice.setValue("6x6");
        row3.getChildren().addAll(boardSizeLabel, boardSizeChoice);

        //row4
        Button startGameButton = new Button("Start game");
        startGameButton.setFont(States.defaultFont);
        startGameButton.setOnAction(action -> {
            if(player1Choice.getValue() == null || player2Choice.getValue() == null || boardSizeChoice.getValue() == null)
                new AlertWindow().display("Warning!", "Please select all parameters.", "Ok");
            if(!player1Choice.getValue().equals("Human") && !player2Choice.getValue().equals("Human"))
                new AlertWindow().display("Warning!", "Please choose at least 1 human.", "Ok");
            else
                startGame(player1Choice.getValue(), player2Choice.getValue(), boardSizeChoice.getValue());
        });
        row4.getChildren().add(startGameButton);

        States.showScene(layout);
    }

    private void startGame(String player1, String player2, String boardSize) {
        if(player1.equals("Human"))
            States.player1AI = 0;
        else {
            States.player1AI = Integer.parseInt( player1.split(" ")[1] );  //AI <num>
        }

        if(player2.equals("Human"))
            States.player2AI = 0;
        else {
            States.player2AI = Integer.parseInt( player2.split(" ")[1] );  //AI <num>
        }

        if(boardSize.equals("6x6"))
            States.boardLength = 6;
        else
            States.boardLength = 8;
        States.startBoard();

        new GameWindow().display();
    }

}
