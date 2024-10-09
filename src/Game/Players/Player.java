package Game.Players;

import Game.Board.Board;
import Game.Moves.Move;
import Game.Moves.MoveStatus;
import Game.Moves.MoveTransition;
import Game.Pieces.King;
import Game.Pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final List<Move> legalMoves;

    private final boolean isInCheck;

    Player(final Board board, final List<Move> legalMoves, final List<Move> opponentMoves){
        this.board = board;
        this.playerKing = establishKing();
        List<Move> concatenatedMoves = new ArrayList<>(legalMoves);
        concatenatedMoves.addAll(calculateCastles(legalMoves, opponentMoves));
        this.legalMoves = List.copyOf(concatenatedMoves);

        if (this.playerKing == null){
            this.isInCheck = false;
        } else {
            this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
        }
    }

    public King getPlayerKing(){
        return this.playerKing;
    }

    public List<Move> getLegalMoves(){
        return this.legalMoves;
    }

    public static List<Move> calculateAttacksOnTile(int position, final List<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();

        for (final Move move : moves){
            if (position == move.getDestination()){
                attackMoves.add(move);
            }
        }

        return List.copyOf(attackMoves);
    }

    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType() == Piece.PieceType.KING){
                return (King)piece;
            }
        }
        // throw new RuntimeException("No king was found, not a valid board!");
        return null;
    }

    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck(){
        return this.isInCheck;
    }

    public boolean isInCheckMate(){
        return this.isInCheck && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves() {
        return this.legalMoves.stream().anyMatch(move -> makeMove(move).getMoveStatus().isDone());
    }

    public boolean isInStaleMate(){
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean hasCastled(){
        return this.playerKing.hasCastled();
    }

    public boolean isKingSideCastleCapable() {
        return this.playerKing.isKingSideCastleCapable();
    }

    public boolean isQueenSideCastleCapable() {
        return this.playerKing.isQueenSideCastleCapable();
    }

    public MoveTransition makeMove(final Move move){
        if (!this.legalMoves.contains(move)){
            return new MoveTransition(this.board, this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionedBoard = move.execute();
        return transitionedBoard.currentPlayer().getOpponent().isInCheck() ?
                new MoveTransition(this.board, this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK) :
                new MoveTransition(this.board, transitionedBoard, move, MoveStatus.DONE);
    }

    public MoveTransition unMakeMove(final Move move) {
        // TODO: implement undo method
        return new MoveTransition(this.board, this.board, move, MoveStatus.DONE);
    }

    public abstract List<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    public abstract List<Move> calculateCastles(final List<Move> playerLegalMoves, final List<Move> opponentLegalMoves);

    protected boolean hasCastleOpportunities(){
        if (this.playerKing == null){
            System.out.println("No king found! Cannot castle");
            return false;
        }
        return !this.isInCheck &&
                !this.playerKing.hasCastled() &&
                (this.playerKing.isKingSideCastleCapable() || this.playerKing.isQueenSideCastleCapable());
    }
}
