import java.util.ArrayList;

/**
 * Represents a Pawn chess piece. Extends Piece and moves forward one square,
 * with the option to move two squares from its starting position.
 * @author ganeshan
 *
 */
public class Pawn extends Piece
{

    private boolean justDoubleMoved;

    /**
     * Creates a Pawn object at the specified board position
     * @param isWhite -- true if white, false if black
     * @param boardRow -- row position on the board
     * @param boardCol -- column position on the board
     */
    public Pawn(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 1, "Pawn");
        this.justDoubleMoved = false;
    }

    /**
     * Returns whether this Pawn just moved two squares (used for en passant)
     * @return boolean -- true if the pawn just double moved
     */
    public boolean justDoubleMoved()
    {
        return justDoubleMoved;
    }

    /**
     * Sets the justDoubleMoved flag for en passant tracking
     * @param val -- true or false
     */
    public void setJustDoubleMoved(boolean val)
    {
        this.justDoubleMoved = val;
    }

    /**
     * Returns the Unicode symbol for the Pawn piece
     * @return String -- white or black pawn symbol
     */
    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2659" : "\u265F";
    }

    /**
     * Checks if the Pawn can move to the target square. Handles single forward
     * move, double move from starting row, diagonal captures, and en passant
     * @param row -- target row
     * @param col -- target column
     * @param board -- the current board state
     * @return boolean -- true if the move is valid
     */
    @Override
    public boolean canMoveTo(int row, int col, Board board)
    {
        // White moves up (-1), black moves down (+1)
        int direction = isWhite() ? -1 : 1;
        int dRow = row - getBoardRow();
        int dCol = col - getBoardCol();

        // Single forward move to empty square
        if (dCol == 0 && dRow == direction && board.getPiece(row, col) == null)
        {
            return true;
        }

        // Double forward move from starting position
        int startRow = isWhite() ? 6 : 1;
        if (dCol == 0 && dRow == 2 * direction && getBoardRow() == startRow
                && board.getPiece(getBoardRow() + direction, col) == null
                && board.getPiece(row, col) == null)
                {
            return true;
        }

        // Diagonal capture (normal or en passant)
        if (Math.abs(dCol) == 1 && dRow == direction)
        {
            // Normal diagonal capture
            Piece target = board.getPiece(row, col);
            if (target != null && target.isWhite() != isWhite())
            {
                return true;
            }
            // En passant capture
            Piece adjacent = board.getPiece(getBoardRow(), col);
            if (adjacent instanceof Pawn && adjacent.isWhite() != isWhite()
                    && ((Pawn) adjacent).justDoubleMoved())
                    {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns an array of all possible moves for this Pawn including forward
     * moves, diagonal captures, and en passant
     * @param board -- the current board state
     * @return Point[] -- array of possible move locations
     */
    @Override
    public Point[] getPossibleMoves(Board board)
    {
        ArrayList<Point> moves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1;
        int r = getBoardRow();
        int c = getBoardCol();

        // Forward moves (single and double)
        if (r + direction >= 0 && r + direction <= 7 && board.getPiece(r + direction, c) == null)
        {
            moves.add(new Point(r + direction, c));
            int startRow = isWhite() ? 6 : 1;
            if (r == startRow && board.getPiece(r + 2 * direction, c) == null)
            {
                moves.add(new Point(r + 2 * direction, c));
            }
        }

        // Diagonal captures and en passant on both sides
        for (int dc = -1; dc <= 1; dc += 2)
        {
            int nc = c + dc;
            int nr = r + direction;
            if (nc >= 0 && nc <= 7 && nr >= 0 && nr <= 7)
            {
                // Normal diagonal capture
                Piece target = board.getPiece(nr, nc);
                if (target != null && target.isWhite() != isWhite())
                {
                    moves.add(new Point(nr, nc));
                }
                // En passant capture
                Piece adjacent = board.getPiece(r, nc);
                if (adjacent instanceof Pawn && adjacent.isWhite() != isWhite()
                        && ((Pawn) adjacent).justDoubleMoved())
                        {
                    moves.add(new Point(nr, nc));
                }
            }
        }

        return moves.toArray(new Point[0]);
    }

    /**
     * Moves the Pawn to the target position and tracks whether it was a
     * double move (for en passant detection on the next turn)
     * @param row -- target row
     * @param col -- target column
     */
    @Override
    public void moveTo(int row, int col)
    {
        justDoubleMoved = (Math.abs(row - getBoardRow()) == 2);
        super.moveTo(row, col);
    }
}
