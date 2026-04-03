import java.util.ArrayList;

/**
 * Represents a Knight chess piece. Extends Piece and moves in an L-shape:
 * two squares in one direction and one square perpendicular. Can jump over
 * other pieces.
 * @author ganeshan
 *
 */
public class Knight extends Piece
{

    /**
     * Creates a Knight object at the specified board position
     * @param isWhite -- true if white, false if black
     * @param boardRow -- row position on the board
     * @param boardCol -- column position on the board
     */
    public Knight(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 3, "Knight");

    }

    /**
     * Returns the Unicode symbol for the Knight piece
     * @return String -- white or black knight symbol
     */
    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2658" : "\u265E";
    }

    /**
     * Checks if the Knight can move to the target square. Validates the
     * L-shape pattern (2+1 or 1+2) and that the target is not a friendly piece
     * @param row -- target row
     * @param col -- target column
     * @param board -- the current board state
     * @return boolean -- true if the move is valid
     */
    @Override
    public boolean canMoveTo(int row, int col, Board board)
    {
        if (!isValidTarget(row, col, board)) return false;
        int dRow = Math.abs(row - getBoardRow());
        int dCol = Math.abs(col - getBoardCol());
        return (dRow == 2 && dCol == 1) || (dRow == 1 && dCol == 2);
    }

    /**
     * Returns an array of all possible moves for this Knight by checking
     * all 8 possible L-shaped positions
     * @param board -- the current board state
     * @return Point[] -- array of possible move locations
     */
    @Override
    public Point[] getPossibleMoves(Board board)
    {
        ArrayList<Point> moves = new ArrayList<>();
        // All 8 possible L-shaped offsets
        int[][] offsets = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        for (int[] off : offsets)
        {
            int nr = getBoardRow() + off[0];
            int nc = getBoardCol() + off[1];
            if (nr >= 0 && nr <= 7 && nc >= 0 && nc <= 7 && isValidTarget(nr, nc, board))
            {
                moves.add(new Point(nr, nc));
            }
        }
        return moves.toArray(new Point[0]);
    }
}
