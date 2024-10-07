package Game.Pieces;

import Game.*;
import Game.Moves.*;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(boolean isWhite, int squareIndex) {
        super('P', isWhite, squareIndex);
    }

    @Override
    public List<Move> getMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1;
        int[] captureDirections = {7 * direction, 9 * direction};
        int current = getCurrentSquare();
        int[] startingRow = isWhite() ? board.getRowIndexes(2) : board.getRowIndexes(7);
        int[] promotionRow = isWhite() ? board.getRowIndexes(8) : board.getRowIndexes(1);
        boolean firstMove = contains(startingRow, current);

        // move one square
        int destination = current + direction * 8;
        if (!contains(promotionRow, destination)) {
            Piece pieceOnSquare = board.getPieceOnSquare(destination);
            if (pieceOnSquare == null) {
                moves.add(new StandardMove(current, destination, this));
                if (firstMove) {
                    int doubleMoveDestination = destination + direction * 8;
                    pieceOnSquare = board.getPieceOnSquare(doubleMoveDestination);
                    if (pieceOnSquare == null) {
                        moves.add(new DoublePawnMove(current, doubleMoveDestination, this, destination));
                    }
                }
            }
            for (int captureDirection : captureDirections) {
                if (!validMove(current, captureDirection, board)){
                    continue;
                }
                int captureDestination = current + captureDirection;
                pieceOnSquare = board.getPieceOnSquare(captureDestination);
                if (pieceOnSquare != null) {
                    if (pieceOnSquare.isWhite() != isWhite()) {
                        // opponent capturable piece
                        moves.add(new CaptureMove(current, captureDestination, this, pieceOnSquare));
                    }
                } else {
                    if (captureDestination == board.getEnPassantSquare()) {
                        Pawn enPassantPawn = board.getEnPassantPawn();
                        assert enPassantPawn != null; // TODO: remove redundant check that the board has stored the pawn
                        moves.add(new EnPassantCapture(current, captureDestination, this));
                    }
                }
            }
        } else {
            // pawn moves to promotion
            Piece pieceOnSquare = board.getPieceOnSquare(destination);
            if (pieceOnSquare == null) {
                moves.add(new PromotionMove(current, destination, this, null, new Queen(this.isWhite(), destination)));
                moves.add(new PromotionMove(current, destination, this, null, new Knight(this.isWhite(), destination)));
                moves.add(new PromotionMove(current, destination, this, null, new Bishop(this.isWhite(), destination)));
                moves.add(new PromotionMove(current, destination, this, null, new Rook(this.isWhite(), destination)));
            }
            for (int captureDirection : captureDirections) {
                if (!validMove(current, captureDirection, board)){
                    continue;
                }
                int captureDestination = current + captureDirection;
                pieceOnSquare = board.getPieceOnSquare(captureDestination);
                if (pieceOnSquare != null) {
                    if (pieceOnSquare.isWhite() != isWhite()) {
                        // opponent capturable piece to promotion
                        moves.add(new PromotionMove(current, captureDestination, this, pieceOnSquare, new Queen(this.isWhite(), captureDestination)));
                        moves.add(new PromotionMove(current, captureDestination, this, pieceOnSquare, new Knight(this.isWhite(), captureDestination)));
                        moves.add(new PromotionMove(current, captureDestination, this, pieceOnSquare, new Bishop(this.isWhite(), captureDestination)));
                        moves.add(new PromotionMove(current, captureDestination, this, pieceOnSquare, new Rook(this.isWhite(), captureDestination)));
                    }
                }
            }
        }
        return moves;
    }

    private boolean validMove(int current, int direction, Board board) {
        // TODO: optimize by removing the board call
        if (contains(board.getFileIndexes("A"), current) && (direction == -9 || direction == 7)) {
            // Left edge of the board capturing to the left
            return false;
        }
        if (contains(board.getFileIndexes("H"), current) && (direction == -7 || direction == 9)) {
            // Right edge of the board capturing to the right
            return false;
        }
        int destination = current + direction;
        return 0 <= destination && destination < 64;
    }
}
