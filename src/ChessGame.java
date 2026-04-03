import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Main class for the chess application. Extends Game and handles all user
 * input, game logic flow, animations, rendering, and both normal play
 * and puzzle mode.
 * @author ganeshan
 *
 */
public class ChessGame extends Game
{

    /**
     * Inner class that tracks the current state of the game including
     * whose turn it is, check/checkmate/stalemate flags, and captured pieces
     */
    private class GameState
    {
        Player currentPlayer;
        boolean isCheck;
        boolean isCheckmate;
        boolean isStalemate;
        ArrayList<Piece> capturedWhite;
        ArrayList<Piece> capturedBlack;

        /**
         * Creates a GameState with the white player as the starting player
         * @param white -- the white Player object
         */
        GameState(Player white)
        {
            this.currentPlayer = white;
            this.isCheck = false;
            this.isCheckmate = false;
            this.isStalemate = false;
            this.capturedWhite = new ArrayList<>();
            this.capturedBlack = new ArrayList<>();
        }
    }

    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 750;
    private static final int SQUARE_SIZE = 75;
    private static final int BOARD_OFFSET_X = 80;
    private static final int BOARD_OFFSET_Y = 60;

    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private GameState gameState;
    private boolean whiteToMove;
    private boolean boardFlipped;

    private int cursorRow;
    private int cursorCol;
    private int selectedRow;
    private int selectedCol;
    private boolean pieceSelected;
    private List<Point> currentLegalMoves;

    private boolean puzzleMode;
    private PuzzleLoader puzzleLoader;
    private PuzzleSolver puzzleSolver;
    private String statusMessage;
    private int statusTimer;

    private boolean gameOver;
    private boolean showMenu;

    private Image offscreen;

    private boolean animating;
    private double animProgress;
    private double animStartX;
    private double animStartY;
    private double animEndX;
    private double animEndY;
    private String animSymbol;
    private boolean animPieceWhite;
    private int animToRow;
    private int animToCol;

    private boolean hasCapture;
    private String captureSymbol;
    private boolean capturePieceWhite;
    private double captureCenterX;
    private double captureCenterY;

    private boolean spinning;
    private double spinAngle;
    private Polygon spinPolygon;

    private boolean pendingNormalFinish;
    private boolean pendingPuzzleFinish;
    private boolean pendingPuzzleSuccess;

    /**
     * Creates the ChessGame window and initializes all game objects,
     * players, board, puzzle loader, and key listener
     */
    public ChessGame()
    {
        super("Chess", WINDOW_WIDTH, WINDOW_HEIGHT);

        board = new Board();
        whitePlayer = new Player("White", true);
        blackPlayer = new Player("Black", false);
        gameState = new GameState(whitePlayer);
        whiteToMove = true;
        boardFlipped = false;

        cursorRow = 6;
        cursorCol = 4;
        selectedRow = -1;
        selectedCol = -1;
        pieceSelected = false;
        currentLegalMoves = new ArrayList<>();

        puzzleMode = false;
        puzzleLoader = new PuzzleLoader();
        puzzleSolver = new PuzzleSolver(puzzleLoader);
        statusMessage = "";
        statusTimer = 0;

        gameOver = false;
        showMenu = true;

        animating = false;
        spinning = false;

        this.addKeyListener(new KeyListener()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                handleKeyPress(e);
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
            }

            @Override
            public void keyTyped(KeyEvent e)
            {
            }
        });

        board.setupStandardBoard();
    }

    /**
     * Handles all keyboard input. Routes to menu, game over, or in-game
     * controls depending on the current state. Manages cursor movement,
     * piece selection, and move execution.
     * @param e -- the KeyEvent from the key listener
     */
    private void handleKeyPress(KeyEvent e)
    {
        if (animating)
            return;

        int code = e.getKeyCode();

        if (showMenu)
        {
            if (code == KeyEvent.VK_1)
            {
                showMenu = false;
                gameOver = false;
                puzzleMode = false;
                whiteToMove = true;
                boardFlipped = false;
                board.setupStandardBoard();
                cursorRow = 6;
                cursorCol = 4;
                pieceSelected = false;
                statusMessage = "White's turn - Use arrow keys and Enter";
                statusTimer = 120;
            }
            else if (code == KeyEvent.VK_2)
            {
                showMenu = false;
                gameOver = false;
                puzzleMode = true;
                whiteToMove = true;
                boardFlipped = false;
                puzzleLoader.loadPuzzle(board);
                cursorRow = 4;
                cursorCol = 4;
                pieceSelected = false;
                statusMessage = "Puzzle " + (puzzleLoader.getCurrentPuzzle() + 1) + " - Find the best move!";
                statusTimer = 120;
            }
            return;
        }

        if (gameOver)
        {
            if (code == KeyEvent.VK_R)
            {
                resetGame();
            }
            else if (code == KeyEvent.VK_M)
            {
                showMenu = true;
                gameOver = false;
            }
            return;
        }

        if (code == KeyEvent.VK_ESCAPE)
        {
            showMenu = true;
            return;
        }

        if (code == KeyEvent.VK_H && puzzleMode)
        {
            statusMessage = "Hint: " + puzzleSolver.getHint();
            statusTimer = 90;
            return;
        }

        int dRow = 0, dCol = 0;
        if (code == KeyEvent.VK_UP) dRow = boardFlipped ? 1 : -1;
        if (code == KeyEvent.VK_DOWN) dRow = boardFlipped ? -1 : 1;
        if (code == KeyEvent.VK_LEFT) dCol = boardFlipped ? 1 : -1;
        if (code == KeyEvent.VK_RIGHT) dCol = boardFlipped ? -1 : 1;

        if (dRow != 0 || dCol != 0)
        {
            int newRow = cursorRow + dRow;
            int newCol = cursorCol + dCol;
            if (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7)
            {
                cursorRow = newRow;
                cursorCol = newCol;
            }
            return;
        }

        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE)
        {
            if (!pieceSelected)
            {
                Piece piece = board.getPiece(cursorRow, cursorCol);
                if (piece != null && piece.isWhite() == whiteToMove)
                {
                    selectedRow = cursorRow;
                    selectedCol = cursorCol;
                    pieceSelected = true;
                    board.clearHighlights();
                    currentLegalMoves = board.getLegalMoves(selectedRow, selectedCol);
                    for (Point m : currentLegalMoves)
                    {
                        board.highlightSquare(m.getX(), m.getY());
                    }
                    statusMessage = piece.getPieceName() + " selected - " + currentLegalMoves.size() + " moves";
                    statusTimer = 60;
                }
            }
            else
            {
                if (cursorRow == selectedRow && cursorCol == selectedCol)
                {
                    pieceSelected = false;
                    board.clearHighlights();
                    currentLegalMoves.clear();
                    statusMessage = "";
                    return;
                }

                boolean isLegal = false;
                for (Point m : currentLegalMoves)
                {
                    if (m.getX() == cursorRow && m.getY() == cursorCol)
                    {
                        isLegal = true;
                        break;
                    }
                }

                if (isLegal)
                {
                    startMoveAnimation(selectedRow, selectedCol, cursorRow, cursorCol);
                }
                else
                {
                    Piece piece = board.getPiece(cursorRow, cursorCol);
                    if (piece != null && piece.isWhite() == whiteToMove)
                    {
                        selectedRow = cursorRow;
                        selectedCol = cursorCol;
                        board.clearHighlights();
                        currentLegalMoves = board.getLegalMoves(selectedRow, selectedCol);
                        for (Point m : currentLegalMoves)
                        {
                            board.highlightSquare(m.getX(), m.getY());
                        }
                        statusMessage = piece.getPieceName() + " selected - " + currentLegalMoves.size() + " moves";
                        statusTimer = 60;
                    }
                }
            }
            return;
        }

        if (puzzleMode)
        {
            /*Justin*/
        }

        if (code == KeyEvent.VK_R && !puzzleMode)
        {
            resetGame();
        }
    }

    /**
     * Starts the move animation from one square to another. Sets up the
     * animation coordinates, checks for captures, executes the move on the
     * board backend, and begins the animation loop
     * @param fromRow -- starting row of the piece
     * @param fromCol -- starting column of the piece
     * @param toRow -- destination row
     * @param toCol -- destination column
     */
    private void startMoveAnimation(int fromRow, int fromCol, int toRow, int toCol)
    {
        Piece movingPiece = board.getPiece(fromRow, fromCol);
        animSymbol = movingPiece.getSymbol();
        animPieceWhite = movingPiece.isWhite();
        animToRow = toRow;
        animToCol = toCol;

        animStartX = BOARD_OFFSET_X + fromCol * SQUARE_SIZE;
        animStartY = BOARD_OFFSET_Y + fromRow * SQUARE_SIZE;
        animEndX = BOARD_OFFSET_X + toCol * SQUARE_SIZE;
        animEndY = BOARD_OFFSET_Y + toRow * SQUARE_SIZE;

        Piece targetPiece = board.getPiece(toRow, toCol);
        hasCapture = (targetPiece != null && targetPiece.isWhite() != movingPiece.isWhite());
        if (hasCapture)
        {
            captureSymbol = targetPiece.getSymbol();
            capturePieceWhite = targetPiece.isWhite();
            captureCenterX = animEndX + SQUARE_SIZE / 2.0;
            captureCenterY = animEndY + SQUARE_SIZE / 2.0;
        }

        if (puzzleMode)
        {
            boolean correct = puzzleSolver.checkSolution(fromRow, fromCol, toRow, toCol);
            board.makeMove(fromRow, fromCol, toRow, toCol);
            boolean mated = puzzleSolver.checkIfCheckmate(board, whiteToMove);
            pendingPuzzleFinish = true;
            pendingPuzzleSuccess = correct || mated;
            pendingNormalFinish = false;
        }
        else
        {
            board.makeMove(fromRow, fromCol, toRow, toCol);
            pendingNormalFinish = true;
            pendingPuzzleFinish = false;
        }

        pieceSelected = false;
        board.clearHighlights();
        currentLegalMoves.clear();

        animating = true;
        animProgress = 0;
        spinning = false;
    }

    /**
     * Called when the move animation completes. Switches turns, checks for
     * checkmate/stalemate/check conditions, and updates status messages.
     * For puzzle mode, checks if the solution was correct.
     */
    private void finishAnimation()
    {
        animating = false;
        spinning = false;

        if (pendingNormalFinish)
        {
            pendingNormalFinish = false;
            whiteToMove = !whiteToMove;

            if (board.isCheckmate(whiteToMove))
            {
                gameState.isCheckmate = true;
                gameOver = true;
                if (whiteToMove)
                {
                    blackPlayer.addWin();
                    whitePlayer.addLoss();
                    statusMessage = "Checkmate! Black wins! (R=restart, M=menu)";
                }
                else
                {
                    whitePlayer.addWin();
                    blackPlayer.addLoss();
                    statusMessage = "Checkmate! White wins! (R=restart, M=menu)";
                }
                statusTimer = 600;
            }
            else if (board.isStalemate(whiteToMove))
            {
                gameState.isStalemate = true;
                gameOver = true;
                statusMessage = "Stalemate! Draw! (R=restart, M=menu)";
                statusTimer = 600;
            }
            else if (board.isInCheck(whiteToMove))
            {
                gameState.isCheck = true;
                statusMessage = (whiteToMove ? "White" : "Black") + " is in CHECK!";
                statusTimer = 90;
            }
            else
            {
                gameState.isCheck = false;
                statusMessage = (whiteToMove ? "White" : "Black") + "'s turn";
                statusTimer = 60;
            }
        }

        if (pendingPuzzleFinish)
        {
            pendingPuzzleFinish = false;
            if (pendingPuzzleSuccess)
            {
                whitePlayer.addPuzzleSolved();
                statusMessage = "Correct! Press N for next puzzle";
                statusTimer = 300;
            }
            else
            {
                statusMessage = "Incorrect. Press R to retry";
                statusTimer = 300;
            }
        }
    }

    /**
     * Resets the game to its initial state with a fresh board, white to move,
     * and all flags cleared
     */
    private void resetGame()
    {
        board.setupStandardBoard();
        whiteToMove = true;
        boardFlipped = false;
        pieceSelected = false;
        board.clearHighlights();
        currentLegalMoves.clear();
        cursorRow = 6;
        cursorCol = 4;
        gameOver = false;
        gameState.isCheck = false;
        gameState.isCheckmate = false;
        gameState.isStalemate = false;
        animating = false;
        spinning = false;
        statusMessage = "Game reset - White's turn";
        statusTimer = 90;
    }

    /**
     * Overrides update to call paint directly, preventing screen flicker
     * with double buffering
     * @param g -- the Graphics object
     */
    @Override
    public void update(Graphics g)
    {
        paint(g);
    }

    /**
     * Main rendering method. Uses double buffering to draw the board,
     * animations, cursor, selection, sidebar, and status bar to an
     * offscreen image before painting to screen
     * @param brush -- the Graphics object provided by the system
     */
    @Override
    public void paint(Graphics brush)
    {
        if (offscreen == null)
        {
            offscreen = createImage(WINDOW_WIDTH, WINDOW_HEIGHT);
        }
        if (offscreen == null)
        {
            return;
        }

        Graphics g = offscreen.getGraphics();

        g.setColor(new Color(40, 40, 45));
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        if (board == null)
        {
            brush.drawImage(offscreen, 0, 0, null);
            g.dispose();
            return;
        }

        if (showMenu)
        {
            drawMenu(g);
            brush.drawImage(offscreen, 0, 0, null);
            g.dispose();
            return;
        }

        this.board.drawBoard(g, BOARD_OFFSET_X, BOARD_OFFSET_Y, SQUARE_SIZE, boardFlipped, whiteToMove);

        if (animating)
        {
            animProgress += 0.07;
            double t = animProgress;
            if (t > 1.0) t = 1.0;

            double currentX = animStartX + (animEndX - animStartX) * t;
            double currentY = animStartY + (animEndY - animStartY) * t;

            coverSquare(g, animToRow, animToCol);

            if (hasCapture && !spinning)
            {
                Font pieceFont = new Font("Serif", Font.PLAIN, (int)(SQUARE_SIZE * 0.75));
                g.setFont(pieceFont);
                FontMetrics fm = g.getFontMetrics();
                int fontH = fm.getHeight();
                int halfH = fontH / 2;
                int halfW = fm.stringWidth(animSymbol) / 2;

                double moveCX = currentX + SQUARE_SIZE / 2.0;
                double moveCY = currentY + SQUARE_SIZE / 2.0;

                Point[] movePts = {
                    new Point(-halfW, -halfH), new Point(halfW, -halfH),
                    new Point(halfW, halfH), new Point(-halfW, halfH)
                };
                Polygon movePoly = new Polygon(movePts, new Point(moveCX, moveCY), 0);

                int captHalfW = fm.stringWidth(captureSymbol) / 2;
                Point[] targetPts = {
                    new Point(-captHalfW, -halfH), new Point(captHalfW, -halfH),
                    new Point(captHalfW, halfH), new Point(-captHalfW, halfH)
                };
                Polygon targetPoly = new Polygon(targetPts, new Point(captureCenterX, captureCenterY), 0);

                if (movePoly.collides(targetPoly))
                {
                    spinning = true;
                    spinAngle = 0;
                    int half = SQUARE_SIZE / 2;
                    Point[] spinPts = {
                        new Point(-half, -half), new Point(half, -half),
                        new Point(half, half), new Point(-half, half)
                    };
                    spinPolygon = new Polygon(spinPts, new Point(captureCenterX, captureCenterY), 0);
                }
            }

            if (spinning)
            {
                spinAngle += 15;
                double spinT = spinAngle / 360.0;
                double floatY = captureCenterY - spinT * SQUARE_SIZE;
                spinPolygon.setPosition(new Point(captureCenterX, floatY));
                spinPolygon.setRotation(spinAngle);
                int alpha = Math.max(0, (int)(255 * (1.0 - spinT)));
                g.setColor(capturePieceWhite ? new Color(255, 255, 200, alpha) : new Color(80, 80, 120, alpha));
                spinPolygon.paint(g);
                Font spinFont = new Font("Serif", Font.PLAIN, (int)(SQUARE_SIZE * 0.75));
                g.setFont(spinFont);
                FontMetrics sfm = g.getFontMetrics();
                int tw = sfm.stringWidth(captureSymbol);
                int th = sfm.getAscent();
                int dx = (int)captureCenterX - tw / 2;
                int dy = (int)floatY + th / 2 - sfm.getDescent();
                g.setColor(capturePieceWhite ? new Color(255, 255, 255, alpha) : new Color(0, 0, 0, alpha));
                g.drawString(captureSymbol, dx, dy);
            }

            drawPieceAt(g, animSymbol, animPieceWhite, (int)currentX, (int)currentY);

            boolean spinDone = !spinning || spinAngle >= 360;
            if (t >= 1.0 && spinDone)
            {
                finishAnimation();
            }
        }

        if (!animating)
        {
            drawCursor(g);
            drawSelection(g);
        }
        drawSidebar(g);
        drawStatusBar(g);

        if (statusTimer > 0)
        {
            statusTimer--;
        }

        brush.drawImage(offscreen, 0, 0, null);
        g.dispose();
    }

    /**
     * Redraws a single square's background color during animation to cover
     * up the piece that was there before the move
     * @param g -- the Graphics object
     * @param row -- the row of the square to cover
     * @param col -- the column of the square to cover
     */
    private void coverSquare(Graphics g, int row, int col)
    {
        int sx = BOARD_OFFSET_X + col * SQUARE_SIZE;
        int sy = BOARD_OFFSET_Y + row * SQUARE_SIZE;
        boolean isLight = (row + col) % 2 == 0;
        if (isLight)
            g.setColor(new Color(240, 217, 181));
        else
            g.setColor(new Color(181, 136, 99));
        g.fillRect(sx, sy, SQUARE_SIZE, SQUARE_SIZE);
    }

    /**
     * Draws a piece symbol at a specific pixel position on screen.
     * Used during animation to render the moving piece
     * @param g -- the Graphics object
     * @param symbol -- the Unicode symbol of the piece
     * @param isWhite -- true if white piece, false if black
     * @param x -- the x pixel position
     * @param y -- the y pixel position
     */
    private void drawPieceAt(Graphics g, String symbol, boolean isWhite, int x, int y)
    {
        Font pieceFont = new Font("Serif", Font.PLAIN, (int)(SQUARE_SIZE * 0.75));
        g.setFont(pieceFont);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(symbol);
        int textHeight = fm.getAscent();
        int drawX = x + (SQUARE_SIZE - textWidth) / 2;
        int drawY = y + (SQUARE_SIZE + textHeight) / 2 - fm.getDescent();
        g.setColor(isWhite ? Color.WHITE : Color.BLACK);
        g.drawString(symbol, drawX, drawY);
    }

    /**
     * Draws the main menu screen with the title, game mode options,
     * controls info, and player stats if any games have been played
     * @param g -- the Graphics object
     */
    private void drawMenu(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Serif", Font.BOLD, 56));
        FontMetrics fm = g.getFontMetrics();
        String title = "Two Player Chess";
        g.drawString(title, (WINDOW_WIDTH - fm.stringWidth(title)) / 2, 140);

        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        fm = g.getFontMetrics();

        String[] options = {"Click 1 -> New Chess Game", "Click 2 -> Puzzles"};

        int boxW = 380;
        int boxX = (WINDOW_WIDTH - boxW) / 2;

        g.setColor(new Color(60, 55, 75, 200));
        g.fillRoundRect(boxX, 200, boxW, 50, 15, 15);
        g.setColor(new Color(230, 230, 240));
        g.drawString(options[0], boxX + (boxW - fm.stringWidth(options[0])) / 2, 232);

        g.setColor(new Color(60, 55, 75, 200));
        g.fillRoundRect(boxX, 260, boxW, 50, 15, 15);
        g.setColor(new Color(230, 230, 240));
        g.drawString(options[1], boxX + (boxW - fm.stringWidth(options[1])) / 2, 292);

        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(new Color(255, 255, 255));
        fm = g.getFontMetrics();

        String[] controls = {"How to Play: ", "Arrow Keys = Move Position", "Enter/Space = Select", "H = Hint (Puzzle)", "ESC = Menu", "R = Restart", "N = Next Puzzle"};
        int cy = 530;
        for (String line : controls)
        {
            g.drawString(line, (WINDOW_WIDTH - fm.stringWidth(line)) / 2, cy);
            cy += 22;
        }

        g.setFont(new Font("SansSerif", Font.ITALIC, 12));
        g.setColor(new Color(120, 120, 140));
        fm = g.getFontMetrics();
        String credit = "CMSC132 Project - Justin Liao & Ganeshan Venu";
        g.drawString(credit, (WINDOW_WIDTH - fm.stringWidth(credit)) / 2, WINDOW_HEIGHT - 30);

        drawPlayerStats(g, 620);
    }

    /**
     * Draws the player stats section on the menu showing wins, losses,
     * and puzzles solved. Only draws if at least one stat is nonzero
     * @param g -- the Graphics object
     * @param startY -- the y pixel position to start drawing stats
     */
    private void drawPlayerStats(Graphics g, int startY)
    {
        if (whitePlayer.getWins() == 0 && blackPlayer.getWins() == 0 && whitePlayer.getPuzzlesSolved() == 0)
        {
            return;
        }

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(new Color(200, 200, 210));
        FontMetrics fm = g.getFontMetrics();
        String header = "Player Stats";
        g.drawString(header, (WINDOW_WIDTH - fm.stringWidth(header)) / 2, startY);

        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(new Color(170, 170, 190));
        fm = g.getFontMetrics();

        String wStats = "White - W:" + whitePlayer.getWins() + " L:" + whitePlayer.getLosses() + " Puzzles:" + whitePlayer.getPuzzlesSolved();
        String bStats = "Black - W:" + blackPlayer.getWins() + " L:" + blackPlayer.getLosses();
        g.drawString(wStats, (WINDOW_WIDTH - fm.stringWidth(wStats)) / 2, startY + 22);
        g.drawString(bStats, (WINDOW_WIDTH - fm.stringWidth(bStats)) / 2, startY + 42);
    }

    /**
     * Draws the yellow cursor highlight on the square the player is
     * currently hovering over with arrow keys
     * @param g -- the Graphics object
     */
    private void drawCursor(Graphics g)
    {
        int displayRow = boardFlipped ? 7 - cursorRow : cursorRow;
        int displayCol = boardFlipped ? 7 - cursorCol : cursorCol;
        int sx = BOARD_OFFSET_X + displayCol * SQUARE_SIZE;
        int sy = BOARD_OFFSET_Y + displayRow * SQUARE_SIZE;

        g.setColor(new Color(255, 255, 100, 100));
        g.fillRect(sx, sy, SQUARE_SIZE, SQUARE_SIZE);

        g.setColor(new Color(255, 255, 0));
        g.drawRect(sx + 1, sy + 1, SQUARE_SIZE - 2, SQUARE_SIZE - 2);
        g.drawRect(sx + 2, sy + 2, SQUARE_SIZE - 4, SQUARE_SIZE - 4);
        g.drawRect(sx + 3, sy + 3, SQUARE_SIZE - 6, SQUARE_SIZE - 6);
    }

    /**
     * Draws the blue selection highlight on the square of the currently
     * selected piece. Only draws if a piece is selected
     * @param g -- the Graphics object
     */
    private void drawSelection(Graphics g)
    {
        if (!pieceSelected)
            return;

        int displayRow = boardFlipped ? 7 - selectedRow : selectedRow;
        int displayCol = boardFlipped ? 7 - selectedCol : selectedCol;
        int sx = BOARD_OFFSET_X + displayCol * SQUARE_SIZE;
        int sy = BOARD_OFFSET_Y + displayRow * SQUARE_SIZE;

        g.setColor(new Color(0, 150, 255, 120));
        g.fillRect(sx, sy, SQUARE_SIZE, SQUARE_SIZE);

        g.setColor(new Color(0, 150, 255));
        g.drawRect(sx + 1, sy + 1, SQUARE_SIZE - 2, SQUARE_SIZE - 2);
        g.drawRect(sx + 2, sy + 2, SQUARE_SIZE - 4, SQUARE_SIZE - 4);
        g.drawRect(sx + 3, sy + 3, SQUARE_SIZE - 6, SQUARE_SIZE - 6);
    }

    /**
     * Draws the sidebar panel on the right side of the board. Shows puzzle
     * info in puzzle mode, or turn indicator, check status, captured pieces,
     * and controls in normal mode
     * @param g -- the Graphics object
     */
    private void drawSidebar(Graphics g)
    {
        int sideX = BOARD_OFFSET_X + SQUARE_SIZE * 8 + 25;
        int sideY = BOARD_OFFSET_Y;

        g.setColor(new Color(55, 55, 65));
        g.fillRoundRect(sideX, sideY, 195, SQUARE_SIZE * 8, 10, 10);
        g.setColor(new Color(80, 80, 95));
        g.drawRoundRect(sideX, sideY, 195, SQUARE_SIZE * 8, 10, 10);

        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(new Color(230, 230, 240));

        if (puzzleMode)
        {
            g.drawString("Puzzle Mode", sideX + 15, sideY + 30);
            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g.setColor(new Color(180, 180, 200));
            g.drawString("Puzzle #" + (puzzleLoader.getCurrentPuzzle() + 1), sideX + 15, sideY + 55);
            g.drawString("Solved: " + whitePlayer.getPuzzlesSolved(), sideX + 15, sideY + 75);

            g.setColor(new Color(150, 150, 170));
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.drawString("H = Hint", sideX + 15, sideY + 105);
            g.drawString("N = Next Puzzle", sideX + 15, sideY + 122);
            g.drawString("R = Retry", sideX + 15, sideY + 139);
        }
        else
        {
            String turnText = whiteToMove ? "White's Turn" : "Black's Turn";
            g.drawString(turnText, sideX + 15, sideY + 30);

            if (gameState.isCheck && !gameState.isCheckmate)
            {
                g.setColor(new Color(255, 100, 100));
                g.setFont(new Font("SansSerif", Font.BOLD, 14));
                g.drawString("CHECK!", sideX + 15, sideY + 55);
            }

            g.setColor(new Color(200, 200, 210));
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            g.drawString("Captured:", sideX + 15, sideY + 90);

            g.setFont(new Font("Serif", Font.PLAIN, 22));
            List<Piece> bCap = new ArrayList<>();
            int cx = sideX + 15;
            int cy = sideY + 115;
            for (int i = 0; i < bCap.size(); i++)
            {
                g.setColor(new Color(100, 100, 100));
                g.drawString(bCap.get(i).getSymbol(), cx, cy);
                cx += 22;
                if (cx > sideX + 175)
                {
                    cx = sideX + 15;
                    cy += 25;
                }
            }

            cy += 35;
            cx = sideX + 15;
            List<Piece> wCap = new ArrayList<>();
            for (int i = 0; i < wCap.size(); i++)
            {
                g.setColor(new Color(220, 220, 220));
                g.drawString(wCap.get(i).getSymbol(), cx, cy);
                cx += 22;
                if (cx > sideX + 175)
                {
                    cx = sideX + 15;
                    cy += 25;
                }
            }

            g.setColor(new Color(150, 150, 170));
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            int infoY = sideY + SQUARE_SIZE * 8 - 85;
            g.drawString("R = Restart", sideX + 15, infoY);
            g.drawString("ESC = Menu", sideX + 15, infoY + 17);
        }
    }

    /**
     * Draws the status bar below the board showing the current status
     * message (check, checkmate, turn info, etc.) and the cursor position
     * @param g -- the Graphics object
     */
    private void drawStatusBar(Graphics g)
    {
        int barY = BOARD_OFFSET_Y + SQUARE_SIZE * 8 + 25;

        g.setColor(new Color(50, 50, 60));
        g.fillRoundRect(BOARD_OFFSET_X, barY, SQUARE_SIZE * 8, 35, 8, 8);

        if (statusMessage.length() > 0 && statusTimer > 0)
        {
            g.setFont(new Font("SansSerif", Font.BOLD, 14));

            if (statusMessage.contains("Checkmate") || statusMessage.contains("CHECK"))
            {
                g.setColor(new Color(255, 100, 100));
            }
            else if (statusMessage.contains("Correct") || statusMessage.contains("wins"))
            {
                g.setColor(new Color(100, 255, 100));
            }
            else if (statusMessage.contains("Incorrect"))
            {
                g.setColor(new Color(255, 150, 50));
            }
            else
            {
                g.setColor(new Color(200, 200, 220));
            }

            FontMetrics fm = g.getFontMetrics();
            g.drawString(statusMessage, BOARD_OFFSET_X + (SQUARE_SIZE * 8 - fm.stringWidth(statusMessage)) / 2, barY + 23);
        }

        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.setColor(new Color(120, 120, 140));
        String pos = (char)('a' + cursorCol) + "" + (8 - cursorRow);
        g.drawString(pos, BOARD_OFFSET_X + SQUARE_SIZE * 8 - 25, barY + 23);
    }

    /**
     * Entry point for the application. Creates a new ChessGame instance
     * which opens the game window
     * @param args -- command line arguments (not used)
     */
    public static void main(String[] args)
    {
        new ChessGame();
    }
}
