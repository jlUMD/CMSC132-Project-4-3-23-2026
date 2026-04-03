import java.awt.*;
import java.util.ArrayList;

/**
 * Abstract class representing a generic chess piece. Extends Polygon for
 * collision detection and implements Moveable for movement logic. All specific
 * pieces (King, Queen, etc.) extend this class.
 * @author ganeshan
 *
 */
public abstract class Piece extends Polygon implements Moveable
{

    /**
     * Inner class that stores the result of a move, including whether it
     * was valid, whether it was a capture, and the captured piece if any
     */
    public static class MoveResult
    {
        public boolean isValid;
        public boolean isCapture;
        public Piece capturedPiece;

        /**
         * Creates a MoveResult with the given move information
         * @param isValid -- whether the move was valid
         * @param isCapture -- whether a piece was captured
         * @param capturedPiece -- the captured piece, or null
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
     * Creates a Piece with color, position, point value, and name. Initializes
     * the Polygon superclass with a default unit square shape.
     * @param isWhite -- true if white, false if black
     * @param boardRow -- row position on the board
     * @param boardCol -- column position on the board
     * @param pointValue -- the piece's material value (e.g. 1 for pawn, 9 for queen)
     * @param pieceName -- the name of the piece (e.g. "King", "Pawn")
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

    /**
     * Returns whether this piece is white
     * @return boolean -- true if white
     */
    public boolean isWhite()
    {
        return isWhite;
    }

    /**
     * Returns the row position of this piece on the board
     * @return int -- the board row
     */
    public int getBoardRow()
    {
        return boardRow;
    }

    /**
     * Returns the column position of this piece on the board
     * @return int -- the board column
     */
    public int getBoardCol()
    {
        return boardCol;
    }

    /**
     * Returns the material point value of this piece
     * @return int -- the point value
     */
    public int getPointValue()
    {
        return pointValue;
    }

    /**
     * Returns whether this piece has moved from its starting position
     * @return boolean -- true if the piece has moved
     */
    public boolean hasMoved()
    {
        return hasMoved;
    }

    /**
     * Returns the name of this piece (e.g. "King", "Queen")
     * @return String -- the piece name
     */
    public String getPieceName()
    {
        return pieceName;
    }

    /**
     * Moves this piece to the target row and column, and marks it as having moved
     * @param row -- target row
     * @param col -- target column
     */
    @Override
    public void moveTo(int row, int col)
    {
        this.boardRow = row;
        this.boardCol = col;
        this.hasMoved = true;
    }

    /**
     * Sets the board row of this piece directly (used for reverting moves)
     * @param row -- the row to set
     */
    public void setBoardRow(int row)
    {
        this.boardRow = row;
    }

    /**
     * Sets the board column of this piece directly (used for reverting moves)
     * @param col -- the column to set
     */
    public void setBoardCol(int col)
    {
        this.boardCol = col;
    }

    /**
     * Sets the hasMoved flag directly (used for reverting simulated moves)
     * @param hasMoved -- true or false
     */
    public void setHasMoved(boolean hasMoved)
    {
        this.hasMoved = hasMoved;
    }

    /**
     * Returns the Unicode symbol representing this piece
     * @return String -- the piece's Unicode character
     */
    public abstract String getSymbol();

    /**
     * Checks if the target square is a valid destination. Returns true if the
     * square is in bounds and either empty or occupied by an enemy piece
     * @param row -- target row
     * @param col -- target column
     * @param board -- the current board state
     * @return boolean -- true if the target is valid
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
     * Helper method that adds all sliding moves in a given direction to the
     * moves list. Used by Bishop, Rook, and Queen. Slides until hitting a
     * piece or the edge of the board
     * @param moves -- the list to add valid moves to
     * @param board -- the current board state
     * @param dRow -- row direction (-1, 0, or 1)
     * @param dCol -- column direction (-1, 0, or 1)
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
                // Can capture enemy piece, but can't go further
                moves.add(new Point(r, c));
                break;
            } else
            {
                // Blocked by friendly piece
                break;
            }
            r += dRow;
            c += dCol;
        }
    }

    /**
     * Draws this piece on the screen using its Unicode symbol. Centers the
     * symbol within the square and adds a shadow effect for visibility
     * @param brush -- the Graphics object to draw with
     * @param screenX -- the x pixel position of the square
     * @param screenY -- the y pixel position of the square
     * @param squareSize -- the size of each square in pixels
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

        // Shadow effect for better visibility
        g2.setColor(isWhite ? Color.DARK_GRAY : Color.GRAY);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawString(symbol, drawX + 1, drawY + 1);

        g2.setColor(isWhite ? Color.WHITE : Color.BLACK);
        g2.drawString(symbol, drawX, drawY);
    }


}
