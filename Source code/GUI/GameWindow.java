package GUI;

import AI.AIPlayer;
import AI.Heuristics;
import Board.*;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class GameWindow implements EventHandler<MouseEvent> {

    static final double pieceRatio = 0.75;  //piece size as ratio to board cube

    private  Label topLabel;

    private StackPane[][] boardCube;
    private Color player1Color, player2Color;

    AIPlayer AI1 = null, AI2 = null;

    //cube colors
    BackgroundFill oddCubeFill = new BackgroundFill(Color.rgb(158, 232, 255),
            CornerRadii.EMPTY, Insets.EMPTY);
    Background oddCubeBG = new Background(oddCubeFill);
    BackgroundFill evenCubeFill = new BackgroundFill(Color.rgb(114, 208, 242),
            CornerRadii.EMPTY, Insets.EMPTY);
    Background evenCubeBG = new Background(evenCubeFill);

    //cube colors to guide moves
    BackgroundFill oddMovePathFill = new BackgroundFill(Color.rgb(224, 191, 255),
            CornerRadii.EMPTY, Insets.EMPTY);
    Background oddMovePathBG = new Background(oddMovePathFill);
    BackgroundFill evenMovePathFill = new BackgroundFill(Color.rgb(208, 154, 245),
            CornerRadii.EMPTY, Insets.EMPTY);
    Background evenMovePathBG = new Background(evenMovePathFill);


    void display() {
        BorderPane layout = new BorderPane();

        /*
         * TOP PANE
         */
        StackPane topPane = new StackPane();
        layout.setTop(topPane);
        topPane.setMinHeight(40);

        topLabel = new Label("Player " + (States.player1Move? 1:2) + "'s move");
        topLabel.setFont(States.largeFont);
        topPane.getChildren().add(topLabel);

        /*
         * BOTTOM PANE
         */
        HBox bottomPane = new HBox();
        layout.setBottom(bottomPane);
        bottomPane.setMinHeight(60);
        int bottomPaneButtons = 2;

        Button unshowButton = new Button("Hide path");
        unshowButton.setFont(States.defaultFont);
        unshowButton.setOnAction(event -> {
            clearMovePath();
            States.lastClick = null;
        });
        StackPane leftButtonPane = new StackPane(unshowButton);
        leftButtonPane.prefWidthProperty().bind(bottomPane.widthProperty().divide(bottomPaneButtons));
        leftButtonPane.prefHeightProperty().bind(bottomPane.heightProperty());

        Button exitButton  = new Button("Exit");
        exitButton.setFont(States.defaultFont);
        exitButton.setOnAction(event -> GUIMain.window.close());
        StackPane rightButtonPane = new StackPane(exitButton);
        rightButtonPane.prefWidthProperty().bind(bottomPane.widthProperty().divide(bottomPaneButtons));
        rightButtonPane.prefHeightProperty().bind(bottomPane.heightProperty());

        bottomPane.getChildren().addAll(leftButtonPane, rightButtonPane);

        /*
         * GRID PANE
         */
        GridPane boardPane = new GridPane();
        boardPane.setAlignment(Pos.CENTER);
        boardResizer(layout, boardPane, topPane, bottomPane);  //always keeps board square sized
        layout.setCenter(boardPane);

        //board cubes
        int boardLength = States.boardLength;
        boardCube = new StackPane[boardLength][boardLength];

        for(int i = 0; i < States.boardLength; i++) {
            for(int j = 0; j < States.boardLength; j++) {
                boardCube[i][j] = new StackPane();
                boardPane.add(boardCube[i][j],j,i);

                boardCube[i][j].prefWidthProperty().bind(boardPane.widthProperty().divide(boardLength));
                boardCube[i][j].prefHeightProperty().bind(boardPane.heightProperty().divide(boardLength));

                if((i+j) % 2 == 0)
                    boardCube[i][j].setBackground(evenCubeBG);
                else
                    boardCube[i][j].setBackground(oddCubeBG);

                boardCube[i][j].setOnMouseClicked(this);
            }
        }

        //board pieces
        player1Color = Color.rgb(227, 248, 255);
        player2Color = Color.rgb(26, 93, 117);

        for(int i = 0; i < boardLength; i++) {
            for(int j = 0; j < boardLength; j++) {
                updatePiece(i,j);
            }
        }
        States.showScene(layout);

        //initializing AIs
        if(States.player1AI > 0) {
            AI1 = new AIPlayer(States.player1AI, States.defaultAIDepth, 1);
        }
        if(States.player2AI > 0) {
            AI2 = new AIPlayer(States.player2AI, States.defaultAIDepth, 2);
        }

        if(AI1 != null && AI2 != null)  //AI only game
            AIPlay();
        else {  //if player1 is AI, player1AI moves first
            if(AI1 != null) {
                if(!States.player1Move)  System.out.println("Error in states: player1 doesn't have the first move.");
                AI.Move move = AI1.giveMove(States.currentBoard);
                handleMove(move.from, move.to);
            }
        }
    }

    void AIPlay() {
        while(!States.gameEnded) {
            if(States.player1Move && AI1 != null) {
                AI.Move move = AI1.giveMove(States.currentBoard);
                handleMove(move.from, move.to);
            }
            else if(!States.player1Move && AI2 != null) {
                AI.Move move = AI2.giveMove(States.currentBoard);
                handleMove(move.from, move.to);
            }
        }
    }

    @Override
    public void handle(MouseEvent mouseEvent) {  //handles clicking on board cubes
        //no effect of click after game end
        if(States.gameEnded) {
            return;
        }
        //no effect of click during AI's turn
        if(States.player1AI != 0 && States.player1Move)
            return;
        if(States.player2AI != 0 && !States.player1Move)
            return;

        Location clickLocation = null;

        for(int i = 0; i < States.boardLength; i++) {
            for (int j = 0; j < States.boardLength; j++) {
                if(mouseEvent.getSource() == boardCube[i][j]) {
                    clickLocation = new Location(i,j);
                    break;
                }
            }
        }

        if(clickLocation == null) {
            System.out.println("Mouse event not from the board. Ignoring...");
            return;
        }

        clearMovePath();

        if(States.lastClick != null && States.lastClick.equals(clickLocation)) {
            States.lastClick = null;
            clickLocation = null;
        }

        if(States.lastClick != null) {
            ArrayList<Location> moves = BoardFx.moveCubes(States.lastClick.row, States.lastClick.column);
            if(moves.contains(clickLocation)) {
                //moving piece
                /*int movedPiece = States.currentBoard[States.lastClick.row][States.lastClick.column];
                States.currentBoard[clickLocation.row][clickLocation.column] = movedPiece;
                States.currentBoard[States.lastClick.row][States.lastClick.column] = 0;

                updatePiece(States.lastClick.row, States.lastClick.column);
                updatePiece(clickLocation.row, clickLocation.column);

                //checking game state
                int winner = BoardFx.gameState();
                if(winner != 0) {  //game has a winner
                    States.gameEnded = true;
                    if(winner == 3)
                        winner = States.player1Move? 1:2;
                    topLabel.setText("Player " + winner + " wins!");
                }
                else{  //changing turn
                    States.player1Move = !States.player1Move;
                    topLabel.setText("Player " + (States.player1Move? 1:2) + "'s move");
                }*/
                handleMove(States.lastClick, clickLocation);
                //giving AI move
                if(States.player1Move && AI1 != null) {
                    AI.Move move = AI1.giveMove(States.currentBoard);
                    handleMove(move.from, move.to);
                }
                else if(!States.player1Move && AI2 != null) {
                    AI.Move move = AI2.giveMove(States.currentBoard);
                    handleMove(move.from, move.to);
                }

                clickLocation = null;
            }
            States.lastClick = null;
        }


        if(clickLocation != null) {  //show move path for click location
            ArrayList<Location> moves = BoardFx.moveCubes(clickLocation.row, clickLocation.column);
            if(!moves.isEmpty()) {
                States.lastClick = clickLocation;
                States.coloredCubes.add(clickLocation);

                for (Location move : moves) {
                    States.coloredCubes.addAll(BoardFx.movePath(clickLocation, move));
                }
                for (Location cube : States.coloredCubes) {
                    if (cube.isEven())
                        boardCube[cube.row][cube.column].setBackground(evenMovePathBG);
                    else
                        boardCube[cube.row][cube.column].setBackground(oddMovePathBG);
                }
            }
        }
        //mouse event handled using last click and click location
    }

    /**
     * Helper methods
     */
    private static void boardResizer(BorderPane layout, Pane boardPane, Pane topPane, Pane bottomPane) {
        ChangeListener<Number> resizeBoard = (observableValue, number, t1) -> {
            if(layout.getWidth() > layout.getHeight()- topPane.getHeight() - bottomPane.getHeight()) {
                boardPane.maxWidthProperty().bind(boardPane.heightProperty());
                boardPane.maxHeightProperty().bind(layout.heightProperty());
            }
            else {
                boardPane.maxHeightProperty().bind(boardPane.widthProperty());
                boardPane.maxWidthProperty().bind(layout.widthProperty());
            }
        };

        boardPane.heightProperty().addListener(resizeBoard);
        boardPane.widthProperty().addListener(resizeBoard);
    }

    private void updatePiece(int row, int col) {

        boardCube[row][col].getChildren().clear();
        if(States.currentBoard[row][col] == 0) {
            return;
        }

        Circle piece = new Circle();
        piece.radiusProperty().bind(boardCube[row][col].widthProperty().multiply(pieceRatio/2));
        if(States.currentBoard[row][col] == 1) {
            piece.setFill(player1Color);
        }
        else
            piece.setFill(player2Color);

        boardCube[row][col].getChildren().add(piece);
    }

    private void clearMovePath() {
        for(Location cube: States.coloredCubes) {
            if(cube.isEven())
                boardCube[cube.row][cube.column].setBackground(evenCubeBG);
            else
                boardCube[cube.row][cube.column].setBackground(oddCubeBG);
        }
        States.coloredCubes.clear();
    }

    private void handleMove(Location oldLoc, Location newLoc) {
        int movedPiece = States.currentBoard[oldLoc.row][oldLoc.column];
        States.currentBoard[newLoc.row][newLoc.column] = movedPiece;
        States.currentBoard[oldLoc.row][oldLoc.column] = 0;

        updatePiece(oldLoc.row, oldLoc.column);
        updatePiece(newLoc.row, newLoc.column);

        //checking game state
        int winner = BoardFx.gameState();
        if(winner != 0) {  //game has a winner
            States.gameEnded = true;
            if(winner == 3)
                winner = States.player1Move? 1:2;
            topLabel.setText("Player " + winner + " wins!");
        }
        else{  //changing turn
            States.player1Move = !States.player1Move;
            topLabel.setText("Player " + (States.player1Move? 1:2) + "'s move");
        }

    }

}
