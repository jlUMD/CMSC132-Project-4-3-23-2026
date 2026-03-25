import java.util.ArrayList;

/**
 * The rook piece, moves in straight lines only (up down left right)
 * @author ganesh
 *
 */
public class Rook extends Piece
{

    /**
     * Creates rook object
     * @param isWhite
     * @param boardRow
     * @param boardCol
     */
    public Rook(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 5, "Rook");
    }

    /**
     * Gets the unicode symbol for rook
     */
    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2656" : "\u265C";
    }

    /**
     * Checks if rook can move to target, has to be a straight line
     * and cant be blocked
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
        // cant move diagonally
        if (dRow != 0 && dCol != 0) return false;
        if (dRow == 0 && dCol == 0) return false;
        int stepR = Integer.signum(dRow);
        int stepC = Integer.signum(dCol);
        int r = getBoardRow() + stepR;
        int c = getBoardCol() + stepC;
        while (r != row || c != col)
        {
            if (board.getPiece(r, c) != null) return false;
            r += stepR;
            c += stepC;
        }
        return true;
    }

    /**
     * Gets all possible straight line moves
     * @param board
     * @return
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
