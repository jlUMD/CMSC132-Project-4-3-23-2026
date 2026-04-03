import java.util.ArrayList;

/**
 * Represents a Rook chess piece. Extends Piece and can move any number of
 * squares horizontally or vertically as long as the path is clear.
 * @author ganeshan
 *
 */
public class Rook extends Piece
{

    /**
     * Creates a Rook object at the specified board position
     * @param isWhite -- true if white, false if black
     * @param boardRow -- row position on the board
     * @param boardCol -- column position on the board
     */
    public Rook(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 5, "Rook");
    }

    /**
     * Returns the Unicode symbol for the Rook piece
     * @return String -- white or black rook symbol
     */
    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2656" : "\u265C";
    }

    /**
     * Checks if the Rook can move to the target square. Validates that the
     * move is strictly horizontal or vertical and that no pieces block the path
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
        // Must move in a straight line (only row OR col changes, not both)
        if (dRow != 0 && dCol != 0) return false;
        if (dRow == 0 && dCol == 0) return false;
        int stepR = Integer.signum(dRow);
        int stepC = Integer.signum(dCol);
        int r = getBoardRow() + stepR;
        int c = getBoardCol() + stepC;
        // Checks each square along the path for blocking pieces
        while (r != row || c != col)
        {
            if (board.getPiece(r, c) != null) return false;
            r += stepR;
            c += stepC;
        }
        return true;
    }

    /**
     * Returns an array of all possible moves for this Rook by checking
     * all 4 straight directions (up, down, left, right)
     * @param board -- the current board state
     * @return Point[] -- array of possible move locations
     */
    @Override
    public Point[] getPossibleMoves(Board board)
    {
        ArrayList<Point> moves = new ArrayList<>();
        addSlidingMoves(moves, board, -1, 0);
        addSlidingMoves(moves, board, 1, 0);
        addSlidingMoves(moves, board, 0, -1);
        addSlidingMoves(moves, board, 0, 1);
        return moves.toArray(new Point[0]);
    }
}
