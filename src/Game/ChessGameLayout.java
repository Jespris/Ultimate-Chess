package Game;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;

public class ChessGameLayout extends JFrame {

    private Board board;  // Reference to the board that tracks the game state
    private Map<String, ImageIcon> pieceIcons;  // Map to hold piece images

    public ChessGameLayout(Board board) {
        this.board = board;  // Initialize the board

        // Load piece images into the map
        loadPieceIcons();

        setTitle("Chess Time");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create placeholders for the top settings and customization
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setPreferredSize(new Dimension(1000, 80));
        topPanel.add(new JLabel("Settings and Customization"));

        // Create placeholders for left and right information panels
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setPreferredSize(new Dimension(150, 800));
        leftPanel.add(new JLabel("Information (Left)"));

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.BLACK);
        rightPanel.setPreferredSize(new Dimension(150, 800));
        rightPanel.add(new JLabel("Information (Right)"));

        // Create the chess board with placeholders for player names and clocks
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel chessBoardPanel = createChessBoard();

        // Placeholder for the top player's name and clock
        JPanel topPlayerPanel = new JPanel();
        topPlayerPanel.setPreferredSize(new Dimension(600, 50));
        topPlayerPanel.setBackground(Color.BLACK);
        topPlayerPanel.add(new JLabel("Name | Clock"));

        // Placeholder for the bottom player's name and clock
        JPanel bottomPlayerPanel = new JPanel();
        bottomPlayerPanel.setPreferredSize(new Dimension(600, 50));
        bottomPlayerPanel.setBackground(Color.BLACK);
        bottomPlayerPanel.add(new JLabel("Name | Clock"));

        // Add player panels and chessboard to the center panel
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

    // Method to load piece icons from the PNG files
    private void loadPieceIcons() {
        pieceIcons = new HashMap<>();
        String[] pieceNames = {"wP", "wK", "wQ", "wB", "wN", "wR", "bP", "bK", "bQ", "bB", "bN", "bR"};
        for (String pieceName : pieceNames) {
            // Load the image from resources using getResource()
            String path = "/resources/pieceImages/" + pieceName + ".png";
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            pieceIcons.put(pieceName, icon);
        }
    }
    private JPanel createChessBoard() {
        JPanel chessBoardPanel = getjPanel();
        int tileSize = 75; // Initial size for the tiles

        // Create the 8x8 grid of squares
        JPanel[][] tiles = new JPanel[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel tile = new JPanel(null);  // Null layout for placing the piece and square name
                tile.setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.GREEN);  // Alternate colors
                tiles[row][col] = tile;
                chessBoardPanel.add(tile);

                // Get the board index based on row and col
                int boardIndex = (row * 8) + col;

                // Add row number to leftmost tiles
                if (col == 0) {
                    JLabel rowLabel = new JLabel(String.valueOf(8 - row));  // Row numbers 8 to 1
                    rowLabel.setBounds(5, 5, 20, 20);  // Position in top-left corner
                    tile.add(rowLabel);
                }

                // Add file letter to bottommost tiles
                if (row == 7) {
                    char fileLetter = (char) ('A' + col);  // File letters A to H
                    JLabel fileLabel = new JLabel(String.valueOf(fileLetter));
                    fileLabel.setBounds(tileSize - 20, tileSize - 20, 20, 20);  // Position in bottom-right corner
                    tile.add(fileLabel);
                }

                // A1 tile (bottom-left) should display both row number and file letter
                if (row == 7 && col == 0) {
                    JLabel fileLabel = new JLabel("A");
                    fileLabel.setBounds(tileSize - 20, tileSize - 20, 20, 20);  // Bottom-right corner
                    tile.add(fileLabel);
                }

                // Get the piece for this board position from the board object
                String pieceName = board.getPieceOnSquare(boardIndex);
                if (!pieceName.equals("--")) {
                    // If there's a piece on this square, add the corresponding image
                    JLabel pieceLabel = new JLabel(pieceIcons.get(pieceName));
                    pieceLabel.setBounds(0, 0, tileSize, tileSize);  // Position the piece in the tile
                    tile.add(pieceLabel);  // Add the piece label to the tile
                }
            }
        }

        // Adjust the size of the tiles to ensure they are always square
        chessBoardPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int panelSize = Math.min(chessBoardPanel.getWidth(), chessBoardPanel.getHeight());
                int tileSize = panelSize / 8; // Divide the panel size by 8 for 8 tiles per row and column

                // Calculate offsets to center the board
                int offsetX = (chessBoardPanel.getWidth() - panelSize) / 2;
                int offsetY = (chessBoardPanel.getHeight() - panelSize) / 2;

                // Resize and reposition each tile to keep them square
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        JPanel tile = tiles[row][col];
                        tile.setBounds(offsetX + col * tileSize, offsetY + row * tileSize, tileSize, tileSize);

                        // Reposition piece and labels (if any) within the tile
                        for (Component comp : tile.getComponents()) {
                            if (comp instanceof JLabel) {
                                JLabel label = (JLabel) comp;
                                if (label.getText() == null){
                                    continue;
                                }
                                if (row == 7 && col == 0 && label.getText().equals("A")) {
                                    label.setBounds(tileSize - 20, tileSize - 20, 20, 20); // File label for A1
                                } else if (col == 0 && label.getText().matches("[1-8]")) {
                                    label.setBounds(5, 5, 20, 20); // Row label
                                } else if (row == 7 && label.getText().matches("[A-H]")) {
                                    label.setBounds(tileSize - 20, tileSize - 20, 20, 20); // File label
                                }
                            }
                        }
                    }
                }
            }
        });

        return chessBoardPanel;
    }

    private static JPanel getjPanel() {
        JPanel chessBoardPanel = new JPanel(null) { // Use null layout for manual sizing
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Ensure the panel is square
                int size = Math.min(getWidth(), getHeight());
                setPreferredSize(new Dimension(size, size));
            }
        };

        chessBoardPanel.setBackground(Color.BLACK);

        // Add tiles to the chessboard and position them manually
        chessBoardPanel.setLayout(null);
        return chessBoardPanel;
    }
}

