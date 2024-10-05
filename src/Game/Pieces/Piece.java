package Game.Pieces;

import Game.Move;

import java.util.HashMap;

public abstract class Piece {

    private final String pieceName;
    private final int pieceDesignator;
    private final boolean isWhite;
    private final int initSquareIndex;

    public Piece(final char pieceName, final boolean isWhite, final int squareIndex) {
        this.pieceName = setPieceName(isWhite, pieceName);
        this.isWhite = isWhite;
        this.pieceDesignator = getDesignator();
        this.initSquareIndex = squareIndex;
    }

    private String setPieceName(boolean isWhite, char pieceName) {
        if (isWhite) {
            return 'w' + String.valueOf(pieceName);
        } else {
            return 'b' + String.valueOf(pieceName);
        }
    }

    private int getDesignator() {
        HashMap<Character, Integer> pieces = new HashMap<>();
        pieces.put('P', 1);
        pieces.put('K', 2);
        pieces.put('Q', 3);
        pieces.put('B', 4);
        pieces.put('N', 5);
        pieces.put('R', 6);

        if (this.isWhite){
            return pieces.get(this.pieceName.charAt(1));
        } else {
            return pieces.get(this.pieceName.charAt(1)) * -1;
        }
    }

    public int getInitSquareIndex(){
        return this.initSquareIndex;
    }

    public String getPieceName() {
        return pieceName;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public int getPieceDesignator() {
        return pieceDesignator;
    }

    public Move getMoves(){
        return null;
    }
}
