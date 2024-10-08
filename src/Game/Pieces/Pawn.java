package Game.Pieces;

import Game.Board.Board;
import Game.Board.BoardUtils;
import Game.Moves.*;
import Game.Players.Alliance;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    private final static int[] PAWN_VECTORS = {7, 8, 9, 16};

    public Pawn(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.PAWN, pieceAlliance, piecePosition, true);
    }

    public Pawn(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove){
        super(PieceType.PAWN, pieceAlliance, piecePosition, isFirstMove);

    }

    @Override
    public List<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int pawnVector: PAWN_VECTORS){
            final int destination = this.piecePosition + (pawnVector * this.getPieceAlliance().getDirection());

            if (!BoardUtils.isValidTileCoordinate(destination)){
                continue;
            }

            if (pawnVector == 8 && board.getPiece(destination) == null){
                // normal pawn move
                if (this.pieceAlliance.isPawnPromotionSquare(destination)){
                    // TODO: add more promotion alternatives
                    legalMoves.add(new Move.PromotionMove(new Move.PawnMove(board, this, destination)));
                } else {
                    legalMoves.add(new Move.PawnMove(board, this, destination));
                }
            } else if (pawnVector == 16 &&
                    this.isFirstMove() &&
                    ((BoardUtils.SECOND_ROW[this.piecePosition] && this.pieceAlliance.isBlack()) ||
                            (BoardUtils.SEVENTH_ROW[this.piecePosition] && this.pieceAlliance.isWhite()))){
                // two-square pawn move
                final int behindDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (board.getPiece(behindDestinationCoordinate) == null &&
                        board.getPiece(destination) == null){
                    legalMoves.add(new Move.PawnJump(board, this, destination));
                }
            } else if (pawnVector == 7 &&
                    !((BoardUtils.EIGHT_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                            (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))){
                // attack to the right, with exception on the edge of the board
                if (board.getPiece(destination) != null){
                    final Piece pieceOnTile = board.getPiece(destination);
                    if (this.pieceAlliance != pieceOnTile.getPieceAlliance()){
                        if (this.pieceAlliance.isPawnPromotionSquare(destination)){
                            legalMoves.add(new Move.PromotionMove(new Move.PawnAttackMove(board, this, destination, pieceOnTile)));
                        } else {
                            legalMoves.add(new Move.PawnAttackMove(board, this, destination, pieceOnTile));
                        }
                    }
                } else if (board.getEnPassantPawn() != null){ // en passant square
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            legalMoves.add(new Move.EnPassantMove(board, this, destination, pieceOnCandidate));
                        }
                    }
                }
            } else if (pawnVector == 9 &&
                    !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                            (BoardUtils.EIGHT_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))){
                // attack to the left, with exception on the edge of the board
                if (board.getPiece(destination) != null){
                    final Piece pieceOnTile = board.getPiece(destination);
                    if (this.pieceAlliance != pieceOnTile.getPieceAlliance()){
                        if (this.pieceAlliance.isPawnPromotionSquare(destination)){
                            legalMoves.add(new Move.PromotionMove(new Move.PawnAttackMove(board, this, destination, pieceOnTile)));
                        } else {
                            legalMoves.add(new Move.PawnAttackMove(board, this, destination, pieceOnTile));
                        }
                    }
                } else if (board.getEnPassantPawn() != null){ // en passant square
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            legalMoves.add(new Move.EnPassantMove(board, this, destination, pieceOnCandidate));
                        }
                    }
                }
            }
        }
        return List.copyOf(legalMoves);
    }

    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }

    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getPieceMoved().getPieceAlliance(), move.getDestination());
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.pawnBonus(this.piecePosition);
    }

    public Piece getPromotionPiece(){
        // TODO: implement promoting to different pieces
        return new Queen(this.pieceAlliance, this.piecePosition, false);
    }
}
