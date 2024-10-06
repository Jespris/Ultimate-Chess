package Game;

import Game.Pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessGameLayout extends JFrame {

    private Board board;  // Reference to the board that tracks the game state
    private Map<String, ImageIcon> pieceIcons;  // Map to hold piece images
    private JLayeredPane[][] tiles;  // Store references to the tiles on the board

    private JLayeredPane selectedTile;
    private Color originalTileColor;

    public ChessGameLayout(Board board) {
        this.board = board;  // Initialize the board
        this.selectedTile = null;
        this.originalTileColor = null;

        // Load piece images into the map
        loadPieceIcons();

        setTitle("Chess Time");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main panel for the entire UI
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create placeholders for the top settings and customization
        JPanel topPanel = createPanelWithLabel("Settings and Customization", Color.LIGHT_GRAY, new Dimension(1000, 80));
        JPanel leftPanel = createPanelWithLabel("Information (Left)", Color.BLACK, new Dimension(150, 800));
        JPanel rightPanel = createPanelWithLabel("Information (Right)", Color.BLACK, new Dimension(150, 800));
        JPanel topPlayerPanel = createPanelWithLabel("Name | Clock", Color.BLACK, new Dimension(600, 50));
        JPanel bottomPlayerPanel = createPanelWithLabel("Name | Clock", Color.BLACK, new Dimension(600, 50));

        // Create the chess board panel
        JPanel chessBoardPanel = createChessBoard();

        // Add player panels and chessboard to the center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topPlayerPanel, BorderLayout.NORTH);
        centerPanel.add(chessBoardPanel, BorderLayout.CENTER);
        centerPanel.add(bottomPlayerPanel, BorderLayout.SOUTH);

        // Add components to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // Add the main panel to the frame
        add(mainPanel, BorderLayout.CENTER);

        // Set window size and make it scalable
        setSize(1000, 800);
        setVisible(true);
    }

    // Helper method to create panels with labels
    private JPanel createPanelWithLabel(String labelText, Color bgColor, Dimension size) {
        JPanel panel = new JPanel();
        panel.setBackground(bgColor);
        panel.setPreferredSize(size);
        panel.add(new JLabel(labelText));
        return panel;
    }

    // Method to load piece icons from the PNG files
    private void loadPieceIcons() {
        pieceIcons = new HashMap<>();
        String[] pieceNames = {"wP", "wK", "wQ", "wB", "wN", "wR", "bP", "bK", "bQ", "bB", "bN", "bR"};
        for (String pieceName : pieceNames) {
            String path = "/resources/pieceImages/" + pieceName + ".png";
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            pieceIcons.put(pieceName, icon);
        }
    }

    // Method to create a chessboard with 8x8 grid of tiles and pieces
    private JPanel createChessBoard() {
        JPanel chessBoardPanel = getjPanel();  // Get panel with null layout for manual control
        tiles = new JLayeredPane[8][8];  // 8x8 grid for storing tiles

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JLayeredPane tile = createTile(row, col);
                chessBoardPanel.add(tile);

                // Add row and file labels on the edge tiles
                addRowAndFileLabels(tile, row, col);
            }
        }

        // Ensure tiles are resized and repositioned correctly when the window is resized
        chessBoardPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int panelSize = Math.min(chessBoardPanel.getWidth(), chessBoardPanel.getHeight());
                int tileSize = panelSize / 8;  // Make sure tiles remain square
                positionAndResizeTiles(tileSize, chessBoardPanel.getWidth(), chessBoardPanel.getHeight());
            }
        });

        return chessBoardPanel;
    }

    // Helper method to create a tile (as JLayeredPane) at a given row and column
    private JLayeredPane createTile(int row, int col) {
        JLayeredPane tile = new JLayeredPane();
        tile.setLayout(null);  // Null layout for custom positioning

        // Set tile background color
        tile.setBackground((row + col) % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        tile.setOpaque(true);

        // Get the board index and add the piece (if any)
        int boardIndex = (row * 8) + col;
        Piece pieceOnSquare = board.getPieceOnSquare(boardIndex);

        if (pieceOnSquare != null) {
            JLabel pieceLabel = new JLabel(pieceIcons.get(pieceOnSquare.getPieceName()));
            pieceLabel.setBounds(0, 0, 75, 75);  // Initial size of the piece
            tile.add(pieceLabel, JLayeredPane.DEFAULT_LAYER);  // Add piece at the default layer
        }

        // Add MouseListener to detect clicks on the tile
        tile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    handleTileClick(boardIndex, tile, row, col);
                }
            }
        });

        tiles[row][col] = tile;  // Store tile reference
        return tile;
    }

    // Handle click events on tiles, including displaying legal moves
    private void handleTileClick(int boardIndex, JLayeredPane tile, int row, int col) {
        Piece pieceOnSquare = board.getPieceOnSquare(boardIndex);
        // If the clicked tile is already selected, deselect it
        if (selectedTile == tile) {
            // Deselect the tile and clear the dots
            resetTileColor(selectedTile);
            clearDotsFromBoard();
            selectedTile = null;  // No tile is selected now
            return;
        }

        // If a tile is already selected, reset its color
        if (selectedTile != null) {
            resetTileColor(selectedTile);
        }

        // Highlight the new selected tile
        originalTileColor = tile.getBackground();  // Store the original color
        tile.setBackground(Color.YELLOW);  // Highlight the selected tile
        selectedTile = tile;  // Set the new selected tile

        // If the piece is the correct color to move, display legal moves
        if (pieceOnSquare != null && pieceOnSquare.isWhite() == board.getWhiteToMove()) {
            List<Move> pieceMoves = board.getMovesFromSquare(boardIndex);
            clearDotsFromBoard();  // Clear old dots
            displayLegalMoves(pieceMoves);  // Display dots for legal moves
        } else {
            clearDotsFromBoard();  // Clear old dots if wrong piece is clicked
        }
    }

    private void resetTileColor(JLayeredPane tile) {
        tile.setBackground(this.originalTileColor);
    }


    // Display dots on the destination tiles for legal moves
    private void displayLegalMoves(List<Move> moves) {
        for (Move move : moves) {
            int destinationIndex = move.getToSquare();
            int destRow = destinationIndex / 8;
            int destCol = destinationIndex % 8;

            JLayeredPane destinationTile = tiles[destRow][destCol];

            JLabel dotLabel = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.BLACK);  // Dot color
                    g.fillOval(0, 0, 20, 20);  // Dot size and position
                }
            };

            dotLabel.setBounds(27, 27, 20, 20);  // Center the dot
            destinationTile.add(dotLabel, JLayeredPane.PALETTE_LAYER);  // Add dot at a higher layer
            destinationTile.repaint();
        }
    }

    // Clear existing dots from all tiles
    private void clearDotsFromBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JLayeredPane tile = tiles[row][col];
                for (Component comp : tile.getComponentsInLayer(JLayeredPane.PALETTE_LAYER)) {
                    tile.remove(comp);  // Remove dots (palette layer)
                }
                tile.repaint();
            }
        }
    }

    // Helper method to add row and file labels to the appropriate tiles
    private void addRowAndFileLabels(JLayeredPane tile, int row, int col) {
        // Add row number to leftmost tiles
        if (col == 0) {
            JLabel rowLabel = new JLabel(String.valueOf(8 - row));
            rowLabel.setBounds(5, 5, 20, 20);
            tile.add(rowLabel, JLayeredPane.PALETTE_LAYER);  // Add label at a higher layer
        }

        // Add file letter to bottommost tiles
        if (row == 7) {
            char fileLetter = (char) ('A' + col);
            JLabel fileLabel = new JLabel(String.valueOf(fileLetter));
            fileLabel.setBounds(55, 55, 20, 20);  // Bottom-right corner
            tile.add(fileLabel, JLayeredPane.PALETTE_LAYER);
        }
    }

    // Helper method to reposition and resize tiles when the window is resized
    private void positionAndResizeTiles(int tileSize, int width, int height) {
        int offsetX = (width - tileSize * 8) / 2;
        int offsetY = (height - tileSize * 8) / 2;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JLayeredPane tile = tiles[row][col];
                tile.setBounds(offsetX + col * tileSize, offsetY + row * tileSize, tileSize, tileSize);

                // Reposition pieces within the tiles
                for (Component comp : tile.getComponents()) {
                    if (comp instanceof JLabel) {
                        comp.setBounds(0, 0, tileSize, tileSize);  // Resize pieces to fit tile
                    }
                }
            }
        }
    }

    // Create a chess board panel with manual layout control
    private static JPanel getjPanel() {
        JPanel chessBoardPanel = new JPanel(null) {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                int size = Math.min(getWidth(), getHeight());
                setPreferredSize(new Dimension(size, size));  // Ensure the board stays square
            }
        };

        chessBoardPanel.setBackground(Color.BLACK);
        return chessBoardPanel;
    }
}
