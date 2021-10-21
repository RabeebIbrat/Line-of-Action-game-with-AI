package AI;

import Board.BoardFx;
import Board.Location;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentNavigableMap;

public class AIPlayer {

    int aiNo;
    int maxDepth = 4;
    int player;  // 1 or 2
    boolean player1;

    public AIPlayer(int aiNo, int depth, int player) {
        this.aiNo = aiNo;
        this.maxDepth = depth;
        this.player = player;
        player1 = (player == 1);
    }

    public Move giveMove(int[][] board) {  //player's main move
        int alpha = -Constants.INFINITY;
        int beta = Constants.INFINITY;

        int utility = -Constants.INFINITY+1;  //+1 so that utility is chosen once no matter what
        Move chosenMove = null;

        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board.length; j++) {
                if(board[i][j] == player) {
                    //moves for one piece
                    Location oldLoc = new Location(i,j);
                    ArrayList<Location> moves = BoardFx.moveCubes(board, i, j, player1);
                    if(!moves.isEmpty()) {
                    }
                    for(Location move: moves) {
                        utility = Math.max(utility,minPlay(BoardFx.afterMoveBoard(board, oldLoc, move), alpha, beta, 1));
                        //alpha = Math.max(utility,alpha);
                        if(utility > alpha) {
                            alpha = utility;
                            chosenMove = new Move(oldLoc, move);
                        }
                    }
                }
                //one piece dealt with
            }
        }
        return chosenMove;
    }

    private int maxPlay(int[][]board, int alpha, int beta, int depth) {  //player's move
        int gameState = BoardFx.gameState(board);
        if(gameState == player)  return Constants.INFINITY;
        if(gameState == 3-player)  return -Constants.INFINITY;
        if(gameState == 3) return -Constants.INFINITY;

        if(depth == maxDepth)
            return Heuristics.utility(board, player, aiNo);

        int utility = -Constants.INFINITY;

        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board.length; j++) {
                if(board[i][j] == player) {
                    //moves for one piece
                    Location oldLoc = new Location(i,j);
                    ArrayList<Location> moves = BoardFx.moveCubes(board, i, j, player1);
                    for(Location move: moves) {
                        utility = Math.max(utility,minPlay(BoardFx.afterMoveBoard(board, oldLoc, move), alpha, beta, depth+1));
                        if(utility >= beta)  return utility;
                        alpha = Math.max(utility,alpha);
                    }
                }
                //one piece dealt with
            }
        }
        return utility;
    }

    private int minPlay(int[][]board, int alpha, int beta, int depth) {  //opponent's move
        int gameState = BoardFx.gameState(board);
        if(gameState == player)  return Constants.INFINITY;
        if(gameState == 3-player)  return -Constants.INFINITY;
        if(gameState == 3) return Constants.INFINITY;

        if(depth == maxDepth)
            return Heuristics.utility(board, player, aiNo);

        int utility = Constants.INFINITY;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if(board[i][j] == 3-player) {
                    //moves for one piece
                    Location oldLoc = new Location(i,j);
                    ArrayList<Location> moves = BoardFx.moveCubes(board, i, j, !player1);
                    for(Location move: moves) {
                        utility = Math.min(utility, maxPlay(BoardFx.afterMoveBoard(board, oldLoc, move), alpha, beta,depth+1));
                        if(utility <= alpha)  return utility;
                        beta = Math.min(utility, beta);
                    }
                }
                //one piece dealt with
            }
        }
        return utility;
    }
}
