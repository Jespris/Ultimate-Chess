package Game.Moves;

import Game.Board.Board;
import Game.Board.BoardUtils;
import Game.Pieces.Pawn;
import Game.Pieces.Piece;
import Game.Pieces.Rook;

public abstract class Move {
    protected final Board board;
    protected final int endSquare;
    protected final Piece pieceMoved;
    protected boolean isFirstMove;

    public static final Move NULL_MOVE = new NullMove();

    private Move(final Board board, final int endSquare, final Piece pieceMoved) {
        this.board = board;
        this.pieceMoved = pieceMoved;
        this.endSquare = endSquare;
        this.isFirstMove = pieceMoved.isFirstMove();
    }

    private Move(final Board board, final int endCoordinate) {
        this.board = board;
        this.endSquare = endCoordinate;
        this.pieceMoved = null;
        this.isFirstMove = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.endSquare;
        result = prime * result + this.pieceMoved.hashCode();
        result = prime * result + this.pieceMoved.getPiecePosition();
        result = result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Move)) {
            return false;
        }
        final Move otherMove = (Move) other;
        return getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
                getDestination() == otherMove.getDestination() &&
                getPieceMoved().equals(otherMove.getPieceMoved());
    }

    public Board getBoard() {
        return this.board;
    }

    public int getCurrentCoordinate() {
        return this.getPieceMoved().getPiecePosition();
    }

    public int getDestination() {
        return this.endSquare;
    }

    public Piece getPieceMoved() {
        return this.pieceMoved;
    }

    public boolean isAttackMove() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    public Board execute() {
        final Board.Builder builder = new Board.Builder();

        for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
            if (!this.pieceMoved.equals(piece)) {
                builder.setPiece(piece);
            }
        }

        for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
            builder.setPiece(piece);
        }

        // move the moved piece
        builder.setPiece(this.pieceMoved.movePiece(this));
        // switch turns
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
        builder.setTransitionMove(this);

        return builder.build();
    }

    public Board undo() {
        final Board.Builder builder = new Board.Builder();
        this.board.getAllPieces().forEach(builder::setPiece);
        builder.setMoveMaker(this.board.currentPlayer().getAlliance());
        return builder.build();
    }

    public static final class StandardMove extends Move {
        public StandardMove(final Board board, final int toSquare, final Piece pieceMoved) {
            super(board, toSquare, pieceMoved);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof StandardMove && super.equals(other);
        }

        @Override
        public String toString() {
            return pieceMoved.getPieceType().toString() + BoardUtils.getChessNotationAtCoordinate(this.endSquare);
        }
    }

    public static final class MajorAttackMove extends AttackMove {

        public MajorAttackMove(final Board board, final Piece pieceMoved, final int endCoordinate, final Piece attackedPiece) {
            super(board, pieceMoved, endCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return pieceMoved.getPieceType().toString() + "x" + BoardUtils.getChessNotationAtCoordinate(this.endSquare);
        }
    }

    public static class AttackMove extends Move {

        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece pieceMoved, final int endCoordinate, final Piece attackedPiece) {
            super(board, endCoordinate, pieceMoved);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public boolean isAttackMove(){
            return true;
        }

        @Override
        public Piece getAttackedPiece(){
            return this.attackedPiece;
        }

        @Override
        public int hashCode(){
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other){
            if (this == other){
                return true;
            }
            if (!(other instanceof AttackMove)){
                return false;
            }
            final Move otherAttackMove = (AttackMove)other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public String toString(){
            return pieceMoved.getPieceType().toString() + "x" + BoardUtils.getChessNotationAtCoordinate(this.endSquare);
        }
    }

    public static class PromotionMove extends Move{

        final Move decoratedMove;
        final Pawn promotedPawn;

        public PromotionMove(final Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getDestination(), decoratedMove.getPieceMoved());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn)decoratedMove.getPieceMoved();
        }

        @Override
        public int hashCode(){
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnPromotion && super.equals(other);
        }

        @Override
        public Board execute(){
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces()){
                if (!this.promotedPawn.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttackMove(){
            return this.decoratedMove.isAttackMove();
        }

        @Override
        public Piece getAttackedPiece(){
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString(){
            return decoratedMove.toString() + "=" + this.promotedPawn.getPromotionPiece().toString();
        }
    }

    public static class PawnMove extends Move{

        public PawnMove(Board board, Piece pieceMoved, int endCoordinate) {
            super(board, endCoordinate, pieceMoved);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnMove && super.equals(other);
        }
        @Override
        public String toString(){
            return BoardUtils.getChessNotationAtCoordinate(this.endSquare);
        }
    }

    public static class PawnAttackMove extends AttackMove{

        public PawnAttackMove(Board board, Piece pieceMoved, int endCoordinate, final Piece attackedPiece) {
            super(board, pieceMoved, endCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString(){
            return BoardUtils.getChessNotationAtCoordinate(this.pieceMoved.getPiecePosition()).charAt(0) + "x" + BoardUtils.getChessNotationAtCoordinate(this.endSquare);
        }
    }

    public static final class EnPassantMove extends PawnAttackMove{

        public EnPassantMove(Board board, Piece pieceMoved, int endCoordinate, final Piece attackedPiece) {
            super(board, pieceMoved, endCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof EnPassantMove && super.equals(other);
        }

        @Override
        public String toString(){
            return BoardUtils.getChessNotationAtCoordinate(this.pieceMoved.getPiecePosition()).charAt(0) + "x" + BoardUtils.getChessNotationAtCoordinate(this.endSquare);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();

            for (final Piece piece : this.board.currentPlayer().getActivePieces()){
                if (!this.pieceMoved.equals(piece)){
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }

            // move the moved piece
            builder.setPiece(this.pieceMoved.movePiece(this));
            // switch turns
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());

            return builder.build();
        }
    }

    public static final class PawnJump extends Move {

        public PawnJump(Board board, Piece pieceMoved, int endCoordinate) {
            super(board, endCoordinate, pieceMoved);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.pieceMoved.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.pieceMoved.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getChessNotationAtCoordinate(this.endSquare);
        }
    }

    public static final class PawnPromotion extends PawnMove{

        public PawnPromotion(Board board, Piece pieceMoved, int endCoordinate) {
            super(board, pieceMoved, endCoordinate);
        }
    }

    static abstract class CastleMove extends Move{

        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public CastleMove(final Board board, final Piece pieceMoved, final int endCoordinate,
                          final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, endCoordinate, pieceMoved);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook(){
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove(){
            return true;
        }

        @Override
        public Board execute(){
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : this.board.getAllPieces()){
                if (!this.pieceMoved.equals(piece) && !this.castleRook.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.pieceMoved.movePiece(this));
            // need to create a new rook
            builder.setPiece(new Rook(this.castleRook.getPieceAlliance(), this.castleRookDestination, false));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            builder.setTransitionMove(this);
            return builder.build();
        }

        @Override
        public boolean equals(final Object other){
            if (this == other){
                return true;
            }
            if (!(other instanceof CastleMove)){
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }

        @Override
        public int hashCode(){
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }
    }

    public static final class KingSideCastleMove extends CastleMove{

        public KingSideCastleMove(final Board board, final Piece pieceMoved, final int endCoordinate,
                                  final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, pieceMoved, endCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString(){
            return "O-O";
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof KingSideCastleMove && super.equals(other);
        }
    }

    public static final class QueenSideCastleMove extends CastleMove{

        public QueenSideCastleMove(final Board board, final Piece pieceMoved, final int endCoordinate,
                                   final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, pieceMoved, endCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString(){
            return "O-O-O";
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }
    }

    public static final class NullMove extends Move{

        public NullMove() {
            super(null,65);
        }

        @Override
        public Board execute(){
            throw new RuntimeException("Cannot execute a null move!");
        }

        @Override
        public int getCurrentCoordinate(){
            return -1;
        }

        @Override
        public String toString(){
            return "Null Move";
        }
    }

    public static class MoveFactory{
        private MoveFactory(){
            throw new RuntimeException("MoveFactory not instantiable!");
        }

        public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate){
            for (final Move move : board.getAllLegalMoves()){
                if (move.getCurrentCoordinate() == currentCoordinate && move.getDestination() == destinationCoordinate){
                    return move;
                }
            }
            return NULL_MOVE;
        }

        public static Move getNullMove() {
            return NULL_MOVE;
        }
    }
}
