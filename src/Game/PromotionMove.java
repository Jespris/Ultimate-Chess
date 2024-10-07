package Game;

import Game.Pieces.Piece;

public class PromotionMove extends Move {
    Piece promotionPiece;
    public PromotionMove(int fromSquare, int toSquare, Piece pieceMoved, Piece pieceCaptured, Piece promotionPiece) {
        super(fromSquare, toSquare, pieceMoved, pieceCaptured);
        this.promotionPiece = promotionPiece;
    }

    @Override
    public boolean isPromotionMove() {
        return true;
    }

    public Piece getPromotionPiece() {
        return promotionPiece;
    }
}
