import java.util.ArrayList;

/**
 * The queen piece, basically a rook and bishop combined so it can go
 * straight or diagonal
 * @author ganesh
 *
 */
public class Queen extends Piece
{

    /**
     * Creates queen object
     * @param isWhite
     * @param boardRow
     * @param boardCol
     */
    public Queen(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 9, "Queen");
    }

    /**
     * Gets the unicode symbol for queen
     */
    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2655" : "\u265B";
    }

    /**
     * Checks if queen can move to target. Can go straight or diagonal
     * as long as nothing is in the way
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
        if (dRow == 0 && dCol == 0) return false;
        boolean straight = (dRow == 0 || dCol == 0);
        boolean diagonal = (Math.abs(dRow) == Math.abs(dCol));
        if (!straight && !diagonal) return false;
        int stepR = Integer.signum(dRow);
        int stepC = Integer.signum(dCol);
        int r = getBoardRow() + stepR;
        int c = getBoardCol() + stepC;
        // checks for blockers along the path
        while (r != row || c != col)
        {
            if (board.getPiece(r, c) != null) return false;
            r += stepR;
            c += stepC;
        }
        return true;
    }

    /**
     * Gets all possible moves in all 8 directions
     * @param board
     * @return
     */
    @Override
    public Point[] getPossibleMoves(Board board)
    {
        ArrayList<Point> moves = new ArrayList<>();
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1},{-1,-1},{-1,1},{1,-1},{1,1}};
        for (int[] d : dirs)
        {
            addSlidingMoves(moves, board, d[0], d[1]);
        }
        return moves.toArray(new Point[0]);
    }
}
