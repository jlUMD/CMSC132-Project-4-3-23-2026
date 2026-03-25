import java.awt.*;
import java.util.ArrayList;

/**
 * The parent class for all chess pieces. Has all the shared fields
 * and methods that the specific pieces inherit
 * @author ganesh
 *
 */
public abstract class Piece extends Polygon implements Moveable
{

    /**
     * Stores the result of making a move, like if it worked
     * and if something got captured
     */
    public static class MoveResult
    {
        public boolean isValid;
        public boolean isCapture;
        public Piece capturedPiece;

        /**
         * Creates MoveResult object
         */
        public MoveResult(boolean isValid, boolean isCapture, Piece capturedPiece)
        {
            this.isValid = isValid;
            this.isCapture = isCapture;
            this.capturedPiece = capturedPiece;
        }
    }

    private boolean isWhite;
    private int boardRow;
    private int boardCol;
    private int pointValue;
    private boolean hasMoved;
    private String pieceName;

    /**
     * Creates piece with all the info it needs
     * @param isWhite
     * @param boardRow
     * @param boardCol
     * @param pointValue -- how much the piece is worth
     * @param pieceName
     */
    public Piece(boolean isWhite, int boardRow, int boardCol, int pointValue, String pieceName)
    {
        super(new Point[]{new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(0, 1)},
              new Point(0, 0), 0);
        this.isWhite = isWhite;
        this.boardRow = boardRow;
        this.boardCol = boardCol;
        this.pointValue = pointValue;
        this.hasMoved = false;
        this.pieceName = pieceName;
    }

    // Getters
    public boolean isWhite()
    {
        return isWhite;
    }

    public int getBoardRow()
    {
        return boardRow;
    }

    public int getBoardCol()
    {
        return boardCol;
    }

    public int getPointValue()
    {
        return pointValue;
    }

    public boolean hasMoved()
    {
        return hasMoved;
    }

    public String getPieceName()
    {
        return pieceName;
    }

    /**
     * Moves piece to new spot and marks it as moved
     */
    @Override
    public void moveTo(int row, int col)
    {
        this.boardRow = row;
        this.boardCol = col;
        this.hasMoved = true;
    }

    // Setters (used for undoing simulated moves mostly)
    public void setBoardRow(int row)
    {
        this.boardRow = row;
    }

    public void setBoardCol(int col)
    {
        this.boardCol = col;
    }

    public void setHasMoved(boolean hasMoved)
    {
        this.hasMoved = hasMoved;
    }

    /**
     * Each piece has its own unicode symbol
     */
    public abstract String getSymbol();

    /**
     * Checks if row, col is a valid spot to move to. Has to be on
     * the board and not have a friendly piece there
     * @param row
     * @param col
     * @param board
     * @return
     */
    protected boolean isValidTarget(int row, int col, Board board)
    {
        if (row < 0 || row > 7 || col < 0 || col > 7)
        {
            return false;
        }
        Piece occupant = board.getPiece(row, col);
        return occupant == null || occupant.isWhite() != this.isWhite;
    }

    /**
     * Slides in one direction and adds all the valid spots.
     * Used by bishop, rook, and queen
     * @param moves
     * @param board
     * @param dRow -- direction for row
     * @param dCol -- direction for col
     */
    protected void addSlidingMoves(ArrayList<Point> moves, Board board, int dRow, int dCol)
    {
        int r = boardRow + dRow;
        int c = boardCol + dCol;
        while (r >= 0 && r <= 7 && c >= 0 && c <= 7)
        {
            Piece occupant = board.getPiece(r, c);
            if (occupant == null)
            {
                moves.add(new Point(r, c));
            } else if (occupant.isWhite() != this.isWhite)
            {
                // can capture then stop
                moves.add(new Point(r, c));
                break;
            } else
            {
                // friendly piece blocking
                break;
            }
            r += dRow;
            c += dCol;
        }
    }

    /**
     * Draws the piece symbol on screen, centers it in the square
     * and does a little shadow thing so it looks nice
     * @param brush
     * @param screenX
     * @param screenY
     * @param squareSize
     */
    public void drawPiece(Graphics brush, int screenX, int screenY, int squareSize)
    {
        Graphics2D g2 = (Graphics2D) brush;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Font pieceFont = new Font("Serif", Font.PLAIN, (int)(squareSize * 0.75));
        g2.setFont(pieceFont);

        String symbol = getSymbol();
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(symbol);
        int textHeight = fm.getAscent();

        int drawX = screenX + (squareSize - textWidth) / 2;
        int drawY = screenY + (squareSize + textHeight) / 2 - fm.getDescent();

        g2.setColor(isWhite ? Color.WHITE : Color.BLACK);
        g2.drawString(symbol, drawX, drawY);

        g2.setColor(isWhite ? Color.DARK_GRAY : Color.GRAY);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawString(symbol, drawX + 1, drawY + 1);

        g2.setColor(isWhite ? Color.WHITE : Color.BLACK);
        g2.drawString(symbol, drawX, drawY);
    }


}
