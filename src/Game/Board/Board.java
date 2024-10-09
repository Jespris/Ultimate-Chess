package Game.Board;

import Game.Moves.Move;
import Game.Pieces.*;
import Game.Players.Alliance;
import Game.Players.BlackPlayer;
import Game.Players.Player;
import Game.Players.WhitePlayer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {
    private final Map<Integer, Piece> boardConfig;
    private final List<Piece> whitePieces;
    private final List<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private final Pawn enPassantPawn;

    private final Move transitionMove;

    private static final Board STANDARD_BOARD = createStandardBoardImpl();

    public Board(final Builder builder){
        // this.board = new int[64];
        this.boardConfig = Collections.unmodifiableMap(builder.boardConfig);

        this.whitePieces = calculateActivePieces(builder, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(builder, Alliance.BLACK);

        this.enPassantPawn = builder.enPassantPawn;

        final List<Move> whiteLegalMoves = calculateLegalMoves(this.whitePieces);
        final List<Move> blackLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackLegalMoves, whiteLegalMoves);

        this.currentPlayer = builder.nextMoveMaker.choosePlayerByAlliance(this.whitePlayer, this.blackPlayer);

        this.transitionMove = builder.transitionMove != null ? builder.transitionMove : Move.MoveFactory.getNullMove();
    }

    public Player whitePlayer(){
        return this.whitePlayer;
    }

    public Player blackPlayer(){
        return this.blackPlayer;
    }

    public Player currentPlayer() {
        return this.currentPlayer;
    }

    public List<Piece> getAllPieces() {
        return Stream.concat(this.whitePieces.stream(),
                this.blackPieces.stream()).toList();
    }

    public Move getTransitionMove(){
        return this.transitionMove;
    }

    private List<Move> calculateLegalMoves(List<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final Piece piece : pieces){
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return List.copyOf(legalMoves);
    }

    private static List<Piece> calculateActivePieces(final Builder builder, final Alliance alliance){
        return builder.boardConfig.values().stream().  // get all values from pieces map
                filter(piece -> piece.getPieceAlliance() == alliance).  // check if correct alliance
                collect(Collectors.toList());  // return a list
    }

    public static Board createStandardBoard() {
        return STANDARD_BOARD;
    }

    public static Board createStandardBoardImpl() {
        final Builder builder = new Builder();
        // White pieces
        int[] secondRow = {8, 9, 10, 11, 12, 13, 14, 15};
        for (int index : secondRow){
            builder.setPiece(new Pawn(Alliance.BLACK, index));
        }
        int[] seventhRow = {55, 54, 53, 52, 51, 50, 49, 48};
        for (int index : seventhRow){
            builder.setPiece(new Pawn(Alliance.WHITE, index));
        }
        builder.setPiece(new Rook(Alliance.WHITE, 63));
        builder.setPiece(new Knight(Alliance.WHITE, 62));
        builder.setPiece(new Bishop(Alliance.WHITE, 61));
        builder.setPiece(new Queen(Alliance.WHITE, 60));
        builder.setPiece(new King(Alliance.WHITE, 59, true, true));
        builder.setPiece(new Bishop(Alliance.WHITE, 58));
        builder.setPiece(new Knight(Alliance.WHITE, 57));
        builder.setPiece(new Rook(Alliance.WHITE, 56));
        // Black pieces

        builder.setPiece(new Rook(Alliance.BLACK, 0));
        builder.setPiece(new Knight(Alliance.BLACK, 1));
        builder.setPiece(new Bishop(Alliance.BLACK, 2));
        builder.setPiece(new Queen(Alliance.BLACK, 3));
        builder.setPiece(new King(Alliance.BLACK, 4, true, true));
        builder.setPiece(new Bishop(Alliance.BLACK, 5));
        builder.setPiece(new Knight(Alliance.BLACK, 6));
        builder.setPiece(new Rook(Alliance.BLACK, 7));

        builder.setMoveMaker(Alliance.WHITE);
        return builder.build();
    }

    public Pawn getEnPassantPawn(){
        return this.enPassantPawn;
    }

    public Piece getPiece(final int tileCoordinate) {
        return this.boardConfig.get(tileCoordinate);
    }

    public static class Builder {
        Map<Integer, Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;
        Move transitionMove;

        public Builder() {
            this.boardConfig = new HashMap<>();
        }

        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance nextMoveMaker){
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public Builder setEnPassantPawn(final Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
            return this;
        }

        public Builder setTransitionMove(final Move transitionMove) {
            this.transitionMove = transitionMove;
            return this;
        }

        public Board build(){
            return new Board(this);
        }
    }
}




