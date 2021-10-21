package Board;

import AI.Move;
import GUI.States;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class BoardFx {

    public static ArrayList<Location> moveCubes(int[][] board, Location loc, boolean player1) {
        ArrayList<Location> moves = new ArrayList<>();
        if ((player1 && board[loc.row][loc.column] != 1) || (!player1 && board[loc.row][loc.column] != 2)) {
            return moves;
        }

        int boardLength = board.length;
        int i, j, pieces;
        boolean movable;
        Location loc1, loc2;

        //row cubes
        pieces = 0;
        i = loc.row;
        for (j = 0; j < boardLength; j++) {
            if (board[i][j] > 0)
                pieces++;
        }
        loc1 = new Location(loc.row, loc.column - pieces);
        loc2 = new Location(loc.row, loc.column + pieces);
        if (loc1.isValid(boardLength) && moveToCube(board, loc, loc1, player1))
            moves.add(loc1);
        if (loc2.isValid(boardLength) && moveToCube(board, loc, loc2, player1))
            moves.add(loc2);

        //column cubes
        pieces = 0;
        j = loc.column;
        for (i = 0; i < boardLength; i++) {
            if (board[i][j] > 0)
                pieces++;
        }
        loc1 = new Location(loc.row - pieces, loc.column);
        loc2 = new Location(loc.row + pieces, loc.column);
        if (loc1.isValid(boardLength) && moveToCube(board, loc, loc1, player1))
            moves.add(loc1);
        if (loc2.isValid(boardLength) && moveToCube(board, loc, loc2, player1))
            moves.add(loc2);

        //+ve diagonal cubes
        pieces = 0;
        int leftPart = Math.min(loc.row, loc.column);
        int rightPart = Math.min(boardLength - loc.row, boardLength - loc.column);  // < rightPart is valid actually
        for (i = loc.row - leftPart, j = loc.column - leftPart; i < loc.row + rightPart && j < loc.column + rightPart; i++, j++) {
            if (board[i][j] > 0)
                pieces++;
        }
        loc1 = new Location(loc.row - pieces, loc.column - pieces);
        loc2 = new Location(loc.row + pieces, loc.column + pieces);
        if (loc1.isValid(boardLength) && moveToCube(board, loc, loc1, player1)) {
            moves.add(loc1);
        }
        if (loc2.isValid(boardLength) && moveToCube(board, loc, loc2, player1)) {
            moves.add(loc2);
        }
        //-ve diagonal cubes
        pieces = 0;
        leftPart = Math.min(loc.column, boardLength - 1 - loc.row);
        rightPart = Math.min(loc.row, boardLength - 1 - loc.column);
        for (i = loc.row + leftPart, j = loc.column - leftPart; i >= loc.row - rightPart && j <= loc.column + rightPart; i--, j++) {
            if (board[i][j] > 0)
                pieces++;
        }
        loc1 = new Location(loc.row+pieces, loc.column-pieces);
        loc2 = new Location(loc.row-pieces, loc.column+pieces);
        if (loc1.isValid(boardLength) && moveToCube(board, loc, loc1, player1)) {
            moves.add(loc1);
        }
        if (loc2.isValid(boardLength) && moveToCube(board, loc, loc2, player1)) {
            moves.add(loc2);
        }

        return moves;
    }

    public static ArrayList<Location> moveCubes(int[][] board, int row, int col, boolean player1) {
        return moveCubes(board, new Location(row,col), player1);
    }

    public static ArrayList<Location> moveCubes(int row, int col) {  //moves based on current board
        return moveCubes(States.currentBoard, row, col, States.player1Move);
    }

    private static boolean moveToCube(int[][] board, Location oldLoc, Location newLoc, boolean player1) {
        boolean player2 = !player1;
        if(oldLoc.equals(newLoc))
            return false;
        if(player1 && board[newLoc.row][newLoc.column] == 1)
            return false;
        if(player2 && board[newLoc.row][newLoc.column] == 2)
            return false;

        int changeRow = oldLoc.row - newLoc.row;
        if(changeRow != 0)
            changeRow /= Math.abs(changeRow);
        int changeCol = oldLoc.column - newLoc.column;
        if(changeCol != 0)
            changeCol /= Math.abs(changeCol);
        for(int i = newLoc.row + changeRow, j = newLoc.column + changeCol; i != oldLoc.row || j != oldLoc.column;
                i += changeRow, j += changeCol) {
            if(player1 && board[i][j] == 2)
                return false;
            if(!player1 && board[i][j] == 1)
                return false;
        }
        return true;
    }

    public static ArrayList<Location> movePath(Location oldLoc, Location newLoc) {
        ArrayList<Location> movePath = new ArrayList<>();
        int changeRow = oldLoc.row - newLoc.row;
        if(changeRow != 0)
            changeRow /= Math.abs(changeRow);
        int changeCol = oldLoc.column - newLoc.column;
        if(changeCol != 0)
            changeCol /= Math.abs(changeCol);
        for(int i = newLoc.row, j = newLoc.column; i != oldLoc.row || j != oldLoc.column;
            i += changeRow, j += changeCol) {
            movePath.add(new Location(i,j));
        }

        return movePath;
    }

    public static int gameState(int[][] board) {
        /*
         * 0 -> ongoing game
         * 1 -> player1 wins
         * 2 -> player2 wins
         * 3 -> player who moved last wins
         */
        int connectedPlayer = 0;
        for (int player = 1; player <= 2; player++) {

            Location playerPiece = new Location();
            int pieces = 0;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    if (board[i][j] == player) {
                        playerPiece = new Location(i, j);
                        pieces++;
                    }
                }
            }

            if (pieces == 0) {
                System.out.println("Error in game board: Player " + player + " reached piece-less state.");
                return -1;
            }
            HashMap<Location, Boolean> takenPieces = new HashMap<>();
            Queue<Location> newPieces = new LinkedList<>();
            newPieces.add(playerPiece);
            takenPieces.put(playerPiece, true);

            while(!newPieces.isEmpty()) {
                Location nowLoc = newPieces.remove();
                for(int i = -1; i <= 1; i++) {
                    for(int j = -1; j <= 1; j++) {
                        Location adjLoc = new Location(nowLoc.row + i,nowLoc.column + j);
                        if(adjLoc.isValid(board.length) &&
                                board[adjLoc.row][adjLoc.column] == player && !takenPieces.containsKey(adjLoc)) {
                            newPieces.add(adjLoc);
                            takenPieces.put(adjLoc, true);
                        }
                    }
                }
            }
            //connected graph nodes of playerPiece found

            if(takenPieces.keySet().size() > pieces) {  //error check
                System.out.println("Error in Board.BoardFx::gameState() -> connected graph not found correctly.");
                return -1;
            }

            if(takenPieces.keySet().size() == pieces)
                connectedPlayer += player;
        }
        return connectedPlayer;
    }

    public static int gameState() {  //game state based on current board
        return gameState(States.currentBoard);
    }

    /*
     * AI METHODS
     */

    public static int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[board.length][board.length];
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board.length; j++)
                newBoard[i][j] = board[i][j];
        }
        return newBoard;
    }

    public static int[][] afterMoveBoard(int[][] board, Location oldLoc, Location newLoc) {  //returns a new copy
        int[][] newBoard = copyBoard(board);

        newBoard[newLoc.row][newLoc.column] = board[oldLoc.row][oldLoc.column];
        newBoard[oldLoc.row][oldLoc.column] = 0;

        return newBoard;
    }
}
