import java.util.ArrayList;

/**
 * Represents a Bishop chess piece. Extends Piece and moves diagonally
 * any number of squares as long as the path is clear.
 * @author ganeshan
 *
 */
public class Bishop extends Piece
{

    /**
     * Creates a Bishop object at the specified board position
     * @param isWhite -- true if white, false if black
     * @param boardRow -- row position on the board
     * @param boardCol -- column position on the board
     */
    public Bishop(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 3, "Bishop");
    }

    /**
     * Returns the Unicode symbol for the Bishop piece
     * @return String -- white or black bishop symbol
     */
    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2657" : "\u265D";
    }

    /**
     * Checks if the Bishop can move to the target square. Validates that the
     * move is diagonal and that no pieces block the path
     * @param row -- target row
     * @param col -- target column
     * @param board -- the current board state
     * @return boolean -- true if the move is valid
     */
    @Override
    public boolean canMoveTo(int row, int col, Board board)
    {
        if (!isValidTarget(row, col, board)) return false;
        int dRow = row - getBoardRow();
        int dCol = col - getBoardCol();
        // Must move diagonally (equal row and col distance)
        if (Math.abs(dRow) != Math.abs(dCol) || dRow == 0) return false;
        int stepR = Integer.signum(dRow);
        int stepC = Integer.signum(dCol);
        int r = getBoardRow() + stepR;
        int c = getBoardCol() + stepC;
        // Checks each square along the diagonal path for blocking pieces
        while (r != row || c != col)
        {
            if (board.getPiece(r, c) != null) return false;
            r += stepR;
            c += stepC;
        }
        return true;
    }

    /**
     * Returns an array of all possible moves for this Bishop by checking
     * all four diagonal directions
     * @param board -- the current board state
     * @return Point[] -- array of possible move locations
     */
    @Override
    public Point[] getPossibleMoves(Board board)
    {
        ArrayList<Point> moves = new ArrayList<>();
        addSlidingMoves(moves, board, -1, -1);
        addSlidingMoves(moves, board, -1, 1);
        addSlidingMoves(moves, board, 1, -1);
        addSlidingMoves(moves, board, 1, 1);
        return moves.toArray(new Point[0]);
    }
}
