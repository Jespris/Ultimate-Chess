package Game;

import Game.Moves.Move;
import Game.Pieces.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;

public class Board {
    // private int[] board;
    public HashMap<String, Integer> squareNumbers;
    public HashMap<Integer, String> squareNames;
    private boolean whiteToMove;
    private int enPassantSquare;
    private Pawn enPassantPawn;
    private List<Move> legalMoves;
    private List<Piece> whitePieces;
    private List<Piece> blackPieces;
    private Move previousMove;

    public Board(){
        // this.board = new int[64];

        this.squareNumbers = createSquareHash();
        this.squareNames = createSquareNames();
        this.whitePieces = new ArrayList<>();
        this.blackPieces = new ArrayList<>();

        this.whiteToMove = true;
        this.enPassantSquare = -1;
        this.enPassantPawn = null;
        this.legalMoves = new ArrayList<>();

        this.previousMove = null;
    }

    public Board makeMove(Move move){
        this.previousMove = move;
        System.out.println("Doing a move!");
        // Step 0. Reset enPassantSquare and pawn
        this.enPassantPawn = null;
        this.enPassantSquare = -1;
        // Step 1. Remove captured piece
        if (move.isCaptureMove()){
            // This should work for enPassantMoves
            List<Piece> opponentPieces = this.whiteToMove ? this.blackPieces : this.whitePieces;
            boolean removeSuccess = opponentPieces.remove(move.getPieceCaptured());
            System.out.println("Removed opponents piece successfully? " + removeSuccess);
        }
        List<Piece> myPieces = this.whiteToMove ? this.whitePieces : this.blackPieces;
        // Step 2. Is it a promotion move?
        if (move.isPromotionMove()) {
            // Step 2.A remove the pawn
            boolean removeSuccess = myPieces.remove(move.getPieceMoved());
            System.out.println("Removed the promoted pawn successfully? " + removeSuccess);
            // Step 2.B add the promoted piece
            Piece promotionPiece = move.getPromotionPiece();
            assert promotionPiece != null;
            myPieces.add(promotionPiece);
        }
        // TODO: Step 3. Is it a castle move?
        // Step 4. Its a normal move
        move.getPieceMoved().setCurrentSquare(move.getToSquare());
        if (move.isPawnDoubleMove()){
            this.enPassantSquare = move.getEnPassantSquare();
            this.enPassantPawn = (Pawn) move.getPieceMoved();
        }
        switchTurn();
        calculateLegalMoves();
        return this;
    }

    public Board undoMove(){
        System.out.println("Doing a move!");
        // Step 0. Reset enPassantSquare and pawn
        this.enPassantPawn = null;
        this.enPassantSquare = -1;
        List<Piece> myPieces = this.whiteToMove ? this.whitePieces : this.blackPieces;
        // Step 1. Restore captured piece
        if (this.previousMove.isCaptureMove()){
            if (this.previousMove.isEnPassantCaptureMove()){
                // TODO: handle enPassantCapture undo move
            } else {
                myPieces.add(this.previousMove.getPieceCaptured());
            }

        }
        List<Piece> opponentPieces = this.whiteToMove ? this.blackPieces : this.whitePieces;
        // Step 2. Was it a promotion move?
        if (previousMove.isPromotionMove()) {
            // Step 2.A restore the pawn
            opponentPieces.add(previousMove.getPieceMoved());
            // Step 2.B remove the promoted piece
            Piece promotionPiece = previousMove.getPromotionPiece();
            assert promotionPiece != null;
            myPieces.remove(promotionPiece);
        }
        // TODO: Step 3. Is it a castle move?
        // Step 4. Its a normal move
        previousMove.getPieceMoved().setCurrentSquare(previousMove.getFromSquare());
        switchTurn();
        calculateLegalMoves();
        return this;
    }

    private void switchTurn(){
        this.whiteToMove = !this.whiteToMove;
    }

    public Move moveSelector(int start, int end){
        for (Move move : legalMoves){
            if (move.getFromSquare() == start && move.getToSquare() == end){
                // TODO: promotion move only returns one of 4 possible promotion moves
                System.out.println("Found a legal move to make!");
                return move;
            }
        }
        return null;
    }

    public List<Move> getMovesFromSquare(int squareIndex){
        List<Move> moves = new ArrayList<>();
        for (Move move : getLegalMoves()) {
            if (move.getFromSquare() == squareIndex){
                moves.add(move);
            }
        }
        return moves;
    }

    public List<Move> getLegalMoves() {
        if (legalMoves.isEmpty()){
            System.out.println("Legal moves hasn't been calculated, calculating...");
            calculateLegalMoves();
        }
        return new ArrayList<>(legalMoves);
    }

    private void calculateLegalMoves() {
        // Step 1.  Calculate all normal moves
        List<Move> allMoves = calculateAllMoves();
        // TODO: Step 2. Remove all moves that put the king in check
        // TODO: Step 3. Add castling moves

        this.legalMoves = allMoves;
    }

    private List<Move> calculateAllMoves() {
        List<Move> allMoves = new ArrayList<>();
        List<Piece> piecesToMove = this.whiteToMove ? this.whitePieces : this.blackPieces;
        // System.out.println("Nr of pieces to calculate moves for: " + piecesToMove.size());
        for (Piece piece : piecesToMove) {
            allMoves.addAll(piece.getMoves(this));
        }
        // System.out.println("Number of moves: " + allMoves.size());
        return allMoves;
    }

    public void printBoard(){
        System.out.println("Printing Board");
        StringBuilder rowPieces = new StringBuilder();
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                Piece pieceOnSquare = getPieceOnSquare(row * 8 + col);
                String pieceName;
                if (pieceOnSquare != null){
                    pieceName = pieceOnSquare.getPieceName();
                } else {
                    pieceName = "--";
                }
                rowPieces.append(pieceName).append(" ");
            }
            System.out.println(rowPieces);
            rowPieces.setLength(0);
        }
    }

    public Piece getPieceOnSquare(final int squareIndex){

        for (Piece piece : getAllPieces()) {
            if (piece.getCurrentSquare() == squareIndex) {
                return piece;
            }
        }
        return null;
    }

    public void createStandardBoard() {
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

        calculateLegalMoves();
    }

    public List<Piece> getAllPieces(){
        List<Piece> allPieces = new ArrayList<>(this.whitePieces);  // Create a new list to avoid modifying the original list
        allPieces.addAll(this.blackPieces);  // Safely combine white and black pieces
        return allPieces;
    }


    public void setPiece(final Piece piece) {
        if (piece.isWhite()){
            whitePieces.add(piece);
        } else {
            blackPieces.add(piece);
        }
        // this.board[piece.getInitSquareIndex()] = piece.getPieceDesignator();
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

    public int[] getEdgeSquares(){
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7,
                63, 62, 61, 60, 59, 58, 57, 56,
                8, 16, 24, 32, 40, 48,
                15, 23, 31, 39, 47, 55};

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

    public boolean getWhiteToMove() {
        return this.whiteToMove;
    }

    public int getEnPassantSquare() {
        return this.enPassantSquare;
    }

    public Pawn getEnPassantPawn(){
        return this.enPassantPawn;
    }
}




