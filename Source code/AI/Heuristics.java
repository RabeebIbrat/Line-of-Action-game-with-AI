package AI;

import Board.*;

import java.util.ArrayList;
import java.util.function.DoubleToIntFunction;

public class Heuristics {

    /*
     * MAIN HEURISTICS -> AREA, EATING, PIECE COUNT, DENSITY(inverted)
     */

    /*
     * SECONDARY HEURISTICS -> PIECE SQUARE TABLE, CONNECTEDNESS, QUAD
     */

    public static int utility(int board[][], int player, int aiNo) {  //symmetric

        if(aiNo == 1) {
            int eatingHeur = 40 * (eatingHeur(board, player) - eatingHeur(board, 3 - player));
            int pieceCountHeur = 40 * (pieceCountHeur(board, player) - pieceCountHeur(board, 3 - player));
            double densityHeur = (-400) * (densityHeur(board, player) - densityHeur(board, 3 - player));

            //System.out.println("eating: " + eatingHeur);
            //System.out.println("piece count: " + pieceCountHeur);
            //System.out.println("density: " + densityHeur);

            return eatingHeur + pieceCountHeur + (int) Math.round(densityHeur);
        }

        else if(aiNo == 2) {
            int pieceSquareHeur = pieceSquareHeur(board,player);
            int areaHeur = -areaHeur(board,player);
            double densityHeur = (-200) * (densityHeur(board, player) - densityHeur(board, 3 - player));

            //System.out.println("Piece square: " + pieceSquareHeur);
            //System.out.println("density: " + densityHeur);
            //System.out.println("area:" + areaHeur);

            return pieceSquareHeur + (int) Math.round(densityHeur);
        }

        else {  //Random AI
            return (int) (1000*Math.random());
        }
    }

    /*
    public static void printHeuristics(int[][] board, boolean player1Move) {
        int player = player1Move? 1:2;

        System.out.println("****HEURISTIC VALUES****");
        System.out.println("Player -> " + player);
        System.out.println("Area: " + areaHeur(board,player));
        System.out.println("Eating: " + eatingHeur(board,player));
        System.out.println("Piece count: " + pieceCountHeur(board, player));
        System.out.println("Density: " + densityHeur(board,player));
        System.out.println();
    }
     */

    /*
     * PIECE SQUARE TABLE HEURISTIC
     */

    static int[][] piece6 = new int[][] {

            { -5, -2, -2, -2, -2, -5},
            { -2,  2,  2,  2,  2, -2},
            { -2,  2,  4,  4,  2, -2},
            { -2,  2,  4,  4,  2, -2},
            { -2,  2,  2,  2,  2, -2},
            { -5, -2, -2, -2, -2, -5}

    };

    static int[][] piece8 = new int[][] {

            { -16,  -5,  -4,  -4,  -4,  -4,  -5, -16},
            {  -5,   2,   2,   2,   2,   2,   2,  -5},
            {  -4,   2,   5,   5,   5,   5,   2,  -4},
            {  -4,   2,   5,  10,  10,   5,   2,  -4},
            {  -4,   2,   5,  10,  10,   5,   2,  -4},
            {  -4,   2,   5,   5,   5,   5,   2,  -4},
            {  -5,   2,   2,   2,   2,   2,   2,  -5},
            { -16,  -5,  -4,  -4,  -4,  -4,  -5, -16}

    };


    static int pieceSquareHeur(int[][] board, int player) {
        int[][] table;
        if(board.length == 6)
            table = piece6;
        else if(board.length == 8)
            table = piece8;
        else return 0;  //heuristic inactive if board size neither 6 nor 8

        int sum = 0;
        for(int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if(board[i][j] == player)
                    sum += table[i][j];
            }
        }
        return sum;
    }


    //piece square table editor
    /*public static void main(String[] args) {
        int len = 8;
        for(int i = 0; i < len; i++) {
            System.out.print("{");
            for(int j = 0; j < len; j++) {
                System.out.format("%4d",piece8[i][j]/5);
                if(j != len-1)
                    System.out.print(",");
            }
            System.out.print("}");
            if(i != len-1)
                System.out.print(",");
            System.out.println();
        }
    }*/

    /*
     * AREA HEURISTIC
     */

    static int areaHeur(int[][] board, int player) {

        int leftCol = 0, rightCol = board.length-1;
        int leftRow = 0, rightRow = board.length-1;

        boolean change;
        //row reduction
        change = true;
        while(change) {
            change = false;
            boolean leftReduction = true, rightReduction = true;
            for (int j = 0; j < board.length; j++) {
                if(board[leftRow][j] == player) {
                    leftReduction = false;
                }
                if(board[rightRow][j] == player) {
                    rightReduction = false;
                }
            }
            if(leftReduction) {
                change = true;
                leftRow++;
            }
            if(rightReduction) {
                change = true;
                rightRow--;
            }
        }
        //column reduction
        change = true;
        while(change) {
            change = false;
            boolean leftReduction = true, rightReduction = true;
            for (int i = 0; i < board.length; i++) {
                if(board[i][leftCol] == player) {
                    leftReduction = false;
                }
                if(board[i][rightCol] == player) {
                    rightReduction = false;
                }
            }
            if(leftReduction) {
                change = true;
                leftCol++;
            }
            if(rightReduction) {
                change = true;
                rightCol--;
            }
        }

        return (rightCol-leftCol + 1) * (rightRow-leftRow + 1);
    }

    /*
     * EATING HEURISTIC
     */

    static int eatingHeur(int[][] board, int player) {
        int sum = 0;

        for(int i = 0;  i < board.length; i++) {
            for(int j = 0; j < board.length; j++) {
                //if player can eat, 1 added
                if(board[i][j] == player) {
                    ArrayList<Location> moves = BoardFx.moveCubes(board,i,j, (player==1) );
                    for(Location move: moves) {
                        if(board[move.row][move.column] == 3-player) {
                            sum += 1;
                        }
                    }
                }

            }
        }

        return sum;
    }

    /*
     * PIECE COUNT HEURISTIC
     */

    static int pieceCountHeur(int[][]board, int player) {  //player piece count
        int sum = 0;

        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board.length; j++) {
               if(board[i][j] == player)
                   sum++;
            }
        }
        return sum;
    }

    /*
     * CONNECTEDNESS HEURISTIC
     */

    static int connectedHeur(int[][] board, int player) {
        int sum = 0, connections;
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board.length; j++) {
                //counting connections for a piece
                connections = 0;
                if(board[i][j] == player) {
                    for(int y = -1; y <= 1; y++) {
                        for(int x = -1; x <= 1; x++) {
                           Location adjLoc = new Location(i+y,j+x);
                           if(adjLoc.isValid(board.length) && board[adjLoc.row][adjLoc.column] == player)
                               connections++;
                        }
                    }
                    connections--;
                    //connections for a piece counted
                    sum  += connections*connections;
                }
            }
        }
        //connectedness heuristic done
        return sum;
    }


    /*
     * QUAD HEURISTIC
     */

    static int quadHeur(int[][] board, int player) {
        int sum = 0, pieces;
        for(int i = 0; i < board.length-1; i++) {
            for(int j = 0; j < board.length-1; j++) {
                pieces = 0;
                for(int y = 0; y <= 1; y++) {
                    for(int x = 0; x <= 1; x++) {
                        if(board[i+y][j+x] == player)
                            pieces++;
                    }
                }
                //pieces in a quad counted
                switch(pieces) {
                    case 3:
                        sum += 1;
                        break;
                    case 4:
                        sum += 2;
                        break;
                }

            }
        }
        //quad heuristic done
        return sum;
    }

    /*
     * DENSITY HEURISTIC
     */

    static double densityHeur(int[][] board, int player) {
        int sumRow = 0, sumCol = 0, pieces = 0;

        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board.length; j++) {
                if(board[i][j] == player) {
                    sumRow += i;
                    sumCol += j;
                    pieces++;
                }
            }
        }
        //center of mass
        double middleRow = (double) sumRow/pieces;
        double middleCol = (double) sumCol/pieces;

        double sum = 0;

        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board.length; j++) {
                if(board[i][j] == player) {
                    sum += Math.max( Math.abs(i-middleRow), Math.abs(j-middleCol));
                }
            }
        }
        return sum/pieces;
    }

}
