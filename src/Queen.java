import java.util.ArrayList;

/**
 * Represents a Queen chess piece. Extends Piece and can move any number of
 * squares horizontally, vertically, or diagonally as long as the path is clear.
 * Basically a combination of Rook and Bishop.
 * @author ganeshan
 *
 */
public class Queen extends Piece
{

    /**
     * Creates a Queen object at the specified board position
     * @param isWhite -- true if white, false if black
     * @param boardRow -- row position on the board
     * @param boardCol -- column position on the board
     */
    public Queen(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 9, "Queen");
    }

    /**
     * Returns the Unicode symbol for the Queen piece
     * @return String -- white or black queen symbol
     */
    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2655" : "\u265B";
    }

    /**
     * Checks if the Queen can move to the target square. Validates that the
     * move is either straight or diagonal and that no pieces block the path
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
        if (dRow == 0 && dCol == 0) return false;
        // Queen can move straight (like Rook) or diagonal (like Bishop)
        boolean straight = (dRow == 0 || dCol == 0);
        boolean diagonal = (Math.abs(dRow) == Math.abs(dCol));
        if (!straight && !diagonal) return false;
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
     * Returns an array of all possible moves for this Queen by checking
     * all 8 directions (4 straight + 4 diagonal)
     * @param board -- the current board state
     * @return Point[] -- array of possible move locations
     */
    @Override
    public Point[] getPossibleMoves(Board board)
    {
        ArrayList<Point> moves = new ArrayList<>();
        // All 8 directions: up, down, left, right, and 4 diagonals
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1},{-1,-1},{-1,1},{1,-1},{1,1}};
        for (int[] d : dirs)
        {
            addSlidingMoves(moves, board, d[0], d[1]);
        }
        return moves.toArray(new Point[0]);
    }
}
