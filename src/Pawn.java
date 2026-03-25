import java.util.ArrayList;

/**
 * The pawn piece. Moves forward, captures diagonal, can double move
 * from start, and has en passant
 * @author ganesh
 *
 */
public class Pawn extends Piece
{

    private boolean justDoubleMoved;

    /**
     * Creates pawn object
     * @param isWhite
     * @param boardRow
     * @param boardCol
     */
    public Pawn(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 1, "Pawn");
        this.justDoubleMoved = false;
    }

    /**
     * Did this pawn just double move (for en passant)
     * @return
     */
    public boolean justDoubleMoved()
    {
        return justDoubleMoved;
    }

    /**
     * Setter for the double move flag
     * @param val
     */
    public void setJustDoubleMoved(boolean val)
    {
        this.justDoubleMoved = val;
    }

    /**
     * Gets the unicode symbol for pawn
     */
    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2659" : "\u265F";
    }

    /**
     * Checks if pawn can move to target. Forward move, double move,
     * diagonal capture, and en passant are all handled here
     * @param row
     * @param col
     * @param board
     * @return
     */
    @Override
    public boolean canMoveTo(int row, int col, Board board)
    {
        int direction = isWhite() ? -1 : 1;
        int dRow = row - getBoardRow();
        int dCol = col - getBoardCol();

        // single forward
        if (dCol == 0 && dRow == direction && board.getPiece(row, col) == null)
        {
            return true;
        }

        // double forward from start
        int startRow = isWhite() ? 6 : 1;
        if (dCol == 0 && dRow == 2 * direction && getBoardRow() == startRow
                && board.getPiece(getBoardRow() + direction, col) == null
                && board.getPiece(row, col) == null)
                {
            return true;
        }

        // diagonal capture or en passant
        if (Math.abs(dCol) == 1 && dRow == direction)
        {
            Piece target = board.getPiece(row, col);
            if (target != null && target.isWhite() != isWhite())
            {
                return true;
            }
            // en passant
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
     * Gets all possible pawn moves
     * @param board
     * @return
     */
    @Override
    public Point[] getPossibleMoves(Board board)
    {
        ArrayList<Point> moves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1;
        int r = getBoardRow();
        int c = getBoardCol();

        // forward moves
        if (r + direction >= 0 && r + direction <= 7 && board.getPiece(r + direction, c) == null)
        {
            moves.add(new Point(r + direction, c));
            int startRow = isWhite() ? 6 : 1;
            if (r == startRow && board.getPiece(r + 2 * direction, c) == null)
            {
                moves.add(new Point(r + 2 * direction, c));
            }
        }

        // diagonal captures + en passant
        for (int dc = -1; dc <= 1; dc += 2)
        {
            int nc = c + dc;
            int nr = r + direction;
            if (nc >= 0 && nc <= 7 && nr >= 0 && nr <= 7)
            {
                Piece target = board.getPiece(nr, nc);
                if (target != null && target.isWhite() != isWhite())
                {
                    moves.add(new Point(nr, nc));
                }
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
     * Moves pawn and tracks if it double moved for en passant
     */
    @Override
    public void moveTo(int row, int col)
    {
        justDoubleMoved = (Math.abs(row - getBoardRow()) == 2);
        super.moveTo(row, col);
    }
}
