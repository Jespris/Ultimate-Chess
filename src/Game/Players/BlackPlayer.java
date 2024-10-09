package Game.Players;

import Game.Board.Board;
import Game.Board.BoardUtils;
import Game.Moves.Move;
import Game.Pieces.Piece;
import Game.Pieces.Rook;

import java.util.ArrayList;
import java.util.List;

import static Game.Pieces.Piece.PieceType.ROOK;

public class BlackPlayer extends Player {
    public BlackPlayer(final Board board, final List<Move> blackStandardLegalMoves, final List<Move> whiteStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    @Override
    public List<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    public List<Move> calculateCastles(List<Move> playerLegalMoves, List<Move> opponentLegalMoves) {

        if (!hasCastleOpportunities()) {
            return new ArrayList<>();
        }

        final List<Move> castleMoves = new ArrayList<>();

        if (this.playerKing.isFirstMove() && this.playerKing.getPiecePosition() == 4 && !this.isInCheck()) {
            // black kingside castle
            if (this.board.getPiece(6) == null &&
                    this.board.getPiece(5) == null) {
                // empty squares between kingside rook and king
                final Piece queenSideRook = this.board.getPiece(7);
                if (queenSideRook != null && queenSideRook.isFirstMove() &&
                        Player.calculateAttacksOnTile(6, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(5, opponentLegalMoves).isEmpty() &&
                        queenSideRook.getPieceType() == ROOK) {
                    // no attacks on squares between rook and king and the rook is a rook
                    if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12)) {
                        castleMoves.add(
                                new Move.KingSideCastleMove(
                                        this.board, this.playerKing, 6,
                                        (Rook) queenSideRook, queenSideRook.getPiecePosition(), 5));

                    }
                }
            }
            if (this.board.getPiece(1) == null &&
                    this.board.getPiece(2) == null &&
                    this.board.getPiece(3) == null) {
                // empty squares between queenside rook and king
                final Piece queenSideRook = this.board.getPiece(0);
                if (queenSideRook != null && queenSideRook.isFirstMove() &&
                        Player.calculateAttacksOnTile(2, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(3, opponentLegalMoves).isEmpty() &&
                        queenSideRook.getPieceType() == ROOK) {
                    // no attacks on squares between rook and king and the rook is a rook
                    if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12)) {

                        castleMoves.add(
                                new Move.QueenSideCastleMove(
                                        this.board, this.playerKing, 2,
                                        (Rook) queenSideRook, queenSideRook.getPiecePosition(), 3));
                    }
                }
            }
        }

        return List.copyOf(castleMoves);
    }

    @Override
    public String toString(){
        return "BLACK";
    }
}
