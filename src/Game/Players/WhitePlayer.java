package Game.Players;

import Game.Board.Board;
import Game.Board.BoardUtils;
import Game.Moves.Move;
import Game.Pieces.Piece;
import Game.Pieces.Rook;

import java.util.ArrayList;
import java.util.List;

import static Game.Pieces.Piece.PieceType.ROOK;

public class WhitePlayer extends Player {
    public WhitePlayer(final Board board, final List<Move> whiteStandardLegalMoves, final List<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    public List<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    public List<Move> calculateCastles(List<Move> playerLegalMoves, List<Move> opponentLegalMoves) {

        if (!hasCastleOpportunities()){
            return new ArrayList<>();
        }

        final List<Move> castleMoves = new ArrayList<>();

        if (this.playerKing.isFirstMove() && this.playerKing.getPiecePosition() == 60 && !this.isInCheck()){
            // white kingside castle
            if (this.board.getPiece(61) == null &&
                    this.board.getPiece(62) == null){
                // empty squares between kingside rook and king
                final Piece queenSideRook = this.board.getPiece(63);
                if (queenSideRook != null && queenSideRook.isFirstMove() &&
                        Player.calculateAttacksOnTile(61, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(62, opponentLegalMoves).isEmpty() &&
                        queenSideRook.getPieceType() == ROOK){
                    // no attacks on squares between rook and king and the rook is a rook
                    if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 52)) {

                        castleMoves.add(
                                new Move.KingSideCastleMove(
                                        this.board, this.playerKing, 62,
                                        (Rook)queenSideRook, queenSideRook.getPiecePosition(), 61));
                    }
                }
            }
            // white queenside castle
            if (this.board.getPiece(59) == null &&
                    this.board.getPiece(58) == null &&
                    this.board.getPiece(57) == null){
                // empty squares between queenside rook and king
                final Piece queenSideRook = this.board.getPiece(56);
                if (queenSideRook != null && queenSideRook.isFirstMove() &&
                        Player.calculateAttacksOnTile(58, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(59, opponentLegalMoves).isEmpty() &&
                        queenSideRook.getPieceType() == ROOK) {
                    // no attacks on squares between rook and king and the rook is a rook
                    if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 52)) {

                        castleMoves.add(
                                new Move.QueenSideCastleMove(
                                        this.board, this.playerKing, 58,
                                        (Rook)queenSideRook, queenSideRook.getPiecePosition(), 59));
                    }
                }
            }
        }

        return List.copyOf(castleMoves);
    }

    @Override
    public String toString(){
        return "WHITE";
    }
}
