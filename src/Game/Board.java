package Game;

import Game.Pieces.*;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;

public class Board {
    public int[] board;
    public HashMap<String, Integer> squareNumbers;
    public HashMap<Integer, String> squareNames;

    public Board(){
        this.board = new int[64];

        this.squareNumbers = createSquareHash();
        this.squareNames = createSquareNames();

        createStandardBoard();
    }

    public void printBoard(){
        System.out.println("Printing Board");
        StringBuilder rowPieces = new StringBuilder();
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                rowPieces.append(getPieceOnSquare(row * 8 + col)).append(" ");
            }
            System.out.println(rowPieces);
            rowPieces.setLength(0);
        }
    }

    public String getPieceOnSquare(final int squareIndex){
        return switch (board[squareIndex]) {
            case 1 -> "wP";
            case 2 -> "wK";
            case 3 -> "wQ";
            case 4 -> "wB";
            case 5 -> "wN";
            case 6 -> "wR";
            case -1 -> "bP";
            case -2 -> "bK";
            case -3 -> "bQ";
            case -4 -> "bB";
            case -5 -> "bN";
            case -6 -> "bR";
            case 0 -> "--";
            default -> "ERROR";
        };
    }

    private void createStandardBoard() {
        // White pieces
        int[] secondRow = getRowIndexes(2);
        for (int index : secondRow){
            setPiece(new Pawn(true, index));
        }
        setPiece(new Rook(true, getSquareIndex("A1")));
        setPiece(new Knight(true, getSquareIndex("B1")));
        setPiece(new Bishop(true, getSquareIndex("C1")));
        setPiece(new Queen(true, getSquareIndex("D1")));
        setPiece(new King(true, getSquareIndex("E1")));
        setPiece(new Bishop(true, getSquareIndex("F1")));
        setPiece(new Knight(true, getSquareIndex("G1")));
        setPiece(new Rook(true, getSquareIndex("H1")));
        // Black pieces
        int[] seventhRow = getRowIndexes(7);
        for (int index : seventhRow){
            setPiece(new Pawn(false, index));
        }
        setPiece(new Rook(false, getSquareIndex("A8")));
        setPiece(new Knight(false, getSquareIndex("B8")));
        setPiece(new Bishop(false, getSquareIndex("C8")));
        setPiece(new Queen(false, getSquareIndex("D8")));
        setPiece(new King(false, getSquareIndex("E8")));
        setPiece(new Bishop(false, getSquareIndex("F8")));
        setPiece(new Knight(false, getSquareIndex("G8")));
        setPiece(new Rook(false, getSquareIndex("H8")));
    }

    private void setPiece(Piece piece) {
        this.board[piece.getInitSquareIndex()] = piece.getPieceDesignator();
    }

    private HashMap<String, Integer> createSquareHash(){
        HashMap<String, Integer> squareHash = new HashMap<>();
        String[] columns = {"A", "B", "C", "D", "E", "F", "G", "H"};
        int i = 0;
        for (int row = 8; row > 0; row--){
            for (int col = 0; col < 8; col++){
                String formattedString = String.format(columns[col]+"%s", (row));
                squareHash.put(formattedString, i);
                i++;
            }
        }
        return squareHash;
    }

    private HashMap<Integer, String> createSquareNames(){
        HashMap<Integer, String> squareNames = new HashMap<>();
        // Populate the reverse map (value to key)
        for (Map.Entry<String, Integer> entry : this.squareNumbers.entrySet()) {
            squareNames.put(entry.getValue(), entry.getKey());
        }
        return squareNames;
    }

    public int getSquareIndex(String square){
        try {
            return squareNumbers.get(square);
        }
        catch (Exception e){
            System.out.println("FUCK! Invalid square");
            System.exit(1);
            return 0;
        }
    }

    public String getSquareName(int squareIndex){
        if (0 <= squareIndex && squareIndex < 64){
            return squareNames.get(squareIndex);
        }
        return "FUCK! Invalid square";
    }

    public int[] getFileIndexes(String file){
        String[] columns = {"A", "B", "C", "D", "E", "F", "G", "H"};
        int index = 0;
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(file)) {
                index = i;
                break; // Exit loop once the value is found
            }
        }

        int[] fileIndexes = new int[8];
        for (int i = 0; i < 8; i++){
            fileIndexes[i] = index + 8 * i;
        }

        return fileIndexes;
    }

    public int[] getRowIndexes(int row){
        // input row is spoken tongue not programming logic, so row 0 doesn't exist, row 8 has indexes 0, 1, 2..
        int[] rowIndexes = new int[8];
        for (int i = 0; i < 8; i++){
            rowIndexes[i] = 8 * (abs(row - 8)) + i;
        }

        return rowIndexes;
    }

    public String[] getSquareNames(int[] squares){
        String[] squareNames = new String[squares.length];
        for (int i = 0; i < squares.length; i++){
            squareNames[i] = getSquareName(squares[i]);
        }
        return squareNames;
    }

    public int[] getRowCol(int squareIndex){
        int row = squareIndex / 8;
        int col = squareIndex % 8;
        return new int[]{row, col};
    }
}




