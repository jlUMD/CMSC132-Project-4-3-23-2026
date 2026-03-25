import java.util.ArrayList;

/**
 * The bishop piece, moves diagonally only
 * @author ganesh
 *
 */
public class Bishop extends Piece
{

    /**
     * Creates bishop object
     * @param isWhite
     * @param boardRow
     * @param boardCol
     */
    public Bishop(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 3, "Bishop");
    }

    /**
     * Gets the unicode symbol for bishop
     */
    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2657" : "\u265D";
    }

    /**
     * Checks if bishop can move to target. Has to be diagonal
     * and path cant be blocked
     * @param row
     * @param col
     * @param board
     * @return
     */
    @Override
    public boolean canMoveTo(int row, int col, Board board)
    {
        if (!isValidTarget(row, col, board)) return false;
        int dRow = row - getBoardRow();
        int dCol = col - getBoardCol();
        // has to be diagonal
        if (Math.abs(dRow) != Math.abs(dCol) || dRow == 0) return false;
        int stepR = Integer.signum(dRow);
        int stepC = Integer.signum(dCol);
        int r = getBoardRow() + stepR;
        int c = getBoardCol() + stepC;
        // walks along diagonal checking for blockers
        while (r != row || c != col)
        {
            if (board.getPiece(r, c) != null) return false;
            r += stepR;
            c += stepC;
        }
        return true;
    }

    /**
     * Gets all possible diagonal moves
     * @param board
     * @return
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
