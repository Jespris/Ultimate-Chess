package Game;

import Game.Moves.KingSideCastleMove;
import Game.Moves.Move;
import Game.Moves.QueenSideCastleMove;
import Game.Pieces.*;

import java.util.*;

import static java.lang.Math.abs;

public class Board {
    // private int[] board;
    public HashMap<String, Integer> squareNumbers;
    public HashMap<Integer, String> squareNames;
    private boolean whiteToMove;
    private int enPassantSquare;
    private Pawn enPassantPawn;
    private List<Move> legalMoves;
    List<Move> opponentMoves;
    private List<Piece> whitePieces;
    private King whiteKing;
    private King blackKing;
    private List<Piece> blackPieces;
    private Move previousMove;
    private boolean[] whiteCastleRights;
    private boolean[] blackCastleRights;

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
        this.opponentMoves = new ArrayList<>();

        this.whiteCastleRights = new boolean[]{true, true}; // kingside, queenside
        this.blackCastleRights = new boolean[]{true, true};

        this.previousMove = null;

        // Set the kings on home squares
        setPiece(new King(true, getSquareIndex("E1")));
        setPiece(new King(false, getSquareIndex("E8")));
    }

    private boolean hasKingSideCastleRight(){
        return this.whiteToMove ? this.whiteCastleRights[0] : this.blackCastleRights[0];
    }

    private void setWhiteKing(King king){
        this.whiteKing = king;
    }

    private void setBlackKing(King king){
        this.blackKing = king;
    }

    private boolean hasQueenSideCastleRight(){
        return this.whiteToMove ? this.whiteCastleRights[1] : this.blackCastleRights[1];
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
        if (move.isCastleMove()){
            if (move.isKingSideCastleMove()){
                // Grab the rook
                int rookSquare = move.getToSquare() + 1;
                Piece rookPiece = getPieceOnSquare(rookSquare);
                assert rookPiece != null;
                // Move the rook
                rookPiece.setCurrentSquare(move.getToSquare() - 1);
                // King moves normally
            } else {
                // Queen side castle
                int rookSquare = move.getToSquare() - 2;
                Piece rookPiece = getPieceOnSquare(rookSquare);
                assert rookPiece != null;
                // Move the rook
                rookPiece.setCurrentSquare(move.getToSquare() + 1);
            }
            // Remove castling rights
            removeCastleRight(true);
            removeCastleRight(false);
        }
        // Step 4. Its a normal move
        move.getPieceMoved().setCurrentSquare(move.getToSquare());
        move.getPieceMoved().setHasMoved(true);
        if (move.isPawnDoubleMove()){
            this.enPassantSquare = move.getEnPassantSquare();
            this.enPassantPawn = (Pawn) move.getPieceMoved();
        }
        switchTurn();
        calculateLegalMoves();

        // Castling rights are updated when calculating castle moves
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
        // Was it a castle move?
        if (previousMove.isCastleMove()){
            if (previousMove.isKingSideCastleMove()){
                // Grab the rook
                int rookSquare = previousMove.getToSquare() - 1;
                Piece rookPiece = getPieceOnSquare(rookSquare);
                assert rookPiece != null;
                // Reset the rook
                rookPiece.setCurrentSquare(rookPiece.getInitSquareIndex());
                rookPiece.setHasMoved(false);
                // King moves normally
            } else {
                // Queen side castle
                int rookSquare = previousMove.getToSquare() + 1;
                Piece rookPiece = getPieceOnSquare(rookSquare);
                assert rookPiece != null;
                // Reset the rook
                rookPiece.setCurrentSquare(rookPiece.getInitSquareIndex());
                rookPiece.setHasMoved(false);
            }
            // Reset castling rights, which should update correctly when calculating legal moves
            switchTurn();
            resetCastleRights();
            King king = getCurrentKing();
            // Move the king back
            king.setCurrentSquare(king.getInitSquareIndex());
            king.setHasMoved(false);
            calculateLegalMoves();
            return this;
        }
        // Step 4. Its a normal move
        previousMove.getPieceMoved().setCurrentSquare(previousMove.getFromSquare());
        switchTurn();
        calculateLegalMoves();
        return this;
    }

    private void resetCastleRights() {
        if (this.whiteToMove){
            this.whiteCastleRights = new boolean[]{true, true};
        } else {
            this.blackCastleRights = new boolean[]{true, true};
        }
    }

    public void switchTurn(){
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

    public List<Move> getLegalMoves(){
        // First, check if the board is valid, there are kings
        if (this.whiteKing == null || this.blackKing == null){
            System.out.println("Not a valid board, king is missing!");
            return null;
        }
        if (legalMoves.isEmpty()){
            System.out.println("Legal moves hasn't been calculated, calculating...");
            calculateLegalMoves();
        }
        return new ArrayList<>(this.legalMoves);
    }

    private void calculateLegalMoves() {
        // Step 1.  Calculate all normal moves
        List<Move> allMoves = calculateAllMoves();
        // calculate opponent moves
        switchTurn();
        this.opponentMoves = calculateAllMoves();
        switchTurn();

        // TODO: Step 2. Remove all moves that put the king in check
        // TODO: this method only removes moves that the king makes into check, and it's inefficient
        allMoves.removeIf(move -> move.getPieceMoved() == getCurrentKing() && squareIsAttacked(move.getToSquare()));
        // TODO: Step 3. Add castling moves
        allMoves.addAll(calculateCastleMoves());
        this.legalMoves = allMoves;
    }

    private void removeCastleRight(boolean kingside){
        if (this.whiteToMove){
            if (kingside){
                this.whiteCastleRights[0] = false;
            } else {
                this.whiteCastleRights[1] = false;
            }
        } else {
            if (kingside){
                this.blackCastleRights[0] = false;
            } else {
                this.blackCastleRights[1] = false;
            }
        }
    }

    private List<Move> calculateCastleMoves() {
        List<Move> castleMoves = new ArrayList<>();

        King king = getCurrentKing();
        if (kingHasMoved(king) || hasNoCastleRights()){
            // King has moved, remove both castle rights and return
            removeCastleRight(true);
            removeCastleRight(false);
            return castleMoves;
        }
        int kingSquare = king.getCurrentSquare();
        boolean[] castleRights = whiteToMove ? this.whiteCastleRights : this.blackCastleRights;
        if (!isCheck()){
            // We are not in check
            if (castleRights[0]){
                // We have King side castle rights
                Piece kingSideRook = getPieceOnSquare(kingSquare + 3);
                if (kingSideRook != null && kingSideRook.isRook() && !kingSideRook.hasMoved()){
                    // Rook hasn't moved
                    // Check if squares are empty and not under attack
                    int kingBishop = kingSquare + 1;
                    int kingKnight = kingSquare + 2;
                    if (
                            getPieceOnSquare(kingBishop) == null &&
                            getPieceOnSquare(kingKnight) == null &&
                            !squareIsAttacked(kingBishop) &&
                            !squareIsAttacked(kingKnight)
                    ){
                        // We can castle!
                        castleMoves.add(new KingSideCastleMove(kingSquare, kingSquare + 2, king));
                    }
                } else {
                    // The rook has moved
                    removeCastleRight(true);
                }
            }
            if (castleRights[1]){
                // We have queen side castle rights
                Piece queenSideRook = getPieceOnSquare(king.getCurrentSquare() - 4);
                if (queenSideRook != null && queenSideRook.isRook() && !queenSideRook.hasMoved()){
                    // Rook hasn't moved
                    // Check if squares are empty and not under attack
                    int queen = kingSquare - 1;
                    int queenBishop = kingSquare - 2;
                    int queenKnight = kingSquare - 3;
                    if (
                            getPieceOnSquare(queen) == null &&
                                    getPieceOnSquare(queenBishop) == null &&
                                    getPieceOnSquare(queenKnight) == null &&
                                    !squareIsAttacked(queen) &&
                                    !squareIsAttacked(queenBishop)
                    ){
                        // We can castle!
                        castleMoves.add(new QueenSideCastleMove(kingSquare, kingSquare - 2, king));
                    }
                } else {
                    // The rook has moved
                    removeCastleRight(false);
                }
            }
        }
        return castleMoves;
    }

    private boolean hasNoCastleRights() {
        boolean[] castleRights = whiteToMove ? this.whiteCastleRights : this.blackCastleRights;
        return !castleRights[0] && !castleRights[1];
    }

    private boolean isCheck() {
        // If opponent has capture move on the king we are in check
        King king = getCurrentKing();
        int currentSquare = king.getCurrentSquare();
        for (Move move : this.opponentMoves){
            if (currentSquare == move.getToSquare()){
                return true;
            }
        }
        return false;
    }

    private King getCurrentKing() {
        return this.whiteToMove ? this.whiteKing : this.blackKing;
    }

    private boolean kingHasMoved(King king){
        return king.getCurrentSquare() != king.getInitSquareIndex();
    }

    private boolean squareIsAttacked(int squareIndex) {
        for (Move move : this.opponentMoves){
            if (move.getToSquare() == squareIndex && !move.isPawnPushMove()){
                return true;
            }
        }
        // TODO: check for pawn control
        return false;
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
            if (piece.isKing()){
                setWhiteKing((King) piece);
            }
        } else {
            blackPieces.add(piece);
            if (piece.isKing()){
                setBlackKing((King) piece);
            }
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




