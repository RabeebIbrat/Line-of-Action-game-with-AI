package GUI;

import Board.Location;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class States {
    public static int player1AI;  //0- -> human
    public static int player2AI;  //0 -> human
    public static int boardLength;

    public static int[][] currentBoard;  //1st index -> row no.; 2nd index -> col no.

    public static void startBoard() {  //call after players and board length have been set
        int[][] board = new int[boardLength][boardLength];  //all entries -> 0

        boolean rowForPlayer1 = true;
        if(player1AI != 0 && player2AI == 0)
            rowForPlayer1 = false;

        int i = 0, i2 = boardLength-1;
        for(int j = 1; j < boardLength-1; j++) {
            board[i][j] = board[i2][j] = rowForPlayer1? 1:2;  //top & bottom row
            board[j][i] = board[j][i2] = rowForPlayer1? 2:1;  //left & right column
        }
        currentBoard = board;
    }

    public static boolean player1Move = true;
    public static boolean gameEnded = false;

    public static Location lastClick = null;  //null signifies no last click (accounted for)
    public static ArrayList<Location> coloredCubes = new ArrayList<>();

    public static final int defaultAIDepth = 4;

    /*
     * DISPLAY STATES
     */
    public static final double defaultScreenWidth = 560;
    public static final double defaultScreenHeight = 664;

    public static boolean firstWindow = true;
    public static Scene showScene(Pane layout) {
        if(firstWindow) {
            firstWindow = false;
            GUIMain.scene = new Scene(layout, defaultScreenWidth, defaultScreenHeight);
            GUIMain.window.setScene(GUIMain.scene);
            GUIMain.window.show();
        }
        else {
            GUIMain.scene = new Scene(layout, GUIMain.scene.getWidth(), GUIMain.scene.getHeight());
            GUIMain.window.setScene(GUIMain.scene);
            GUIMain.window.show();
        }
        return GUIMain.scene;
    }

    public static final Font defaultFont = new Font(15);
    public static final Font largeFont = new Font(20);
}
