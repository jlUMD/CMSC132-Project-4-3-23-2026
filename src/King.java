import java.util.ArrayList;

/**
 * Represents a King chess piece. Extends Piece and can move one square in
 * any direction, and can also castle with a Rook under the right conditions.
 * @author ganeshan
 *
 */
public class King extends Piece
{

    /**
     * Creates a King object at the specified board position
     * @param isWhite -- true if white, false if black
     * @param boardRow -- row position on the board
     * @param boardCol -- column position on the board
     */
    public King(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 0, "King");
    }

    /**
     * Returns the Unicode symbol for the King piece
     * @return String -- white or black king symbol
     */
    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2654" : "\u265A";
    }

    /**
     * Checks if the King can move to the target square. Allows one-square
     * moves in any direction and also checks for castling moves
     * @param row -- target row
     * @param col -- target column
     * @param board -- the current board state
     * @return boolean -- true if the move is valid
     */
    @Override
    public boolean canMoveTo(int row, int col, Board board)
    {
        int dRow = Math.abs(row - getBoardRow());
        int dCol = Math.abs(col - getBoardCol());
        // Normal king move: one square in any direction
        if (dRow <= 1 && dCol <= 1 && (dRow + dCol) > 0 && isValidTarget(row, col, board))
        {
            return true;
        }
        // Castling: king moves two squares horizontally
        if (!hasMoved() && dRow == 0 && Math.abs(col - getBoardCol()) == 2)
        {
            return canCastle(col, board);
        }
        return false;
    }

    /**
     * Returns an array of all possible moves for this King, including
     * normal one-square moves and castling moves
     * @param board -- the current board state
     * @return Point[] -- array of possible move locations
     */
    @Override
    public Point[] getPossibleMoves(Board board)
    {
        ArrayList<Point> moves = new ArrayList<>();
        // All 8 directions a king can move
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++)
        {
            int nr = getBoardRow() + dr[i];
            int nc = getBoardCol() + dc[i];
            if (nr >= 0 && nr <= 7 && nc >= 0 && nc <= 7 && isValidTarget(nr, nc, board))
            {
                moves.add(new Point(nr, nc));
            }
        }
        // Checks for castling availability on both sides
        if (!hasMoved())
        {
            if (canCastle(getBoardCol() + 2, board))
            {
                moves.add(new Point(getBoardRow(), getBoardCol() + 2));
            }
            if (canCastle(getBoardCol() - 2, board))
            {
                moves.add(new Point(getBoardRow(), getBoardCol() - 2));
            }
        }
        return moves.toArray(new Point[0]);
    }

    /**
     * Checks if the King can castle to the target column. Verifies the King
     * hasn't moved, the Rook is in place and hasn't moved, and the path
     * between them is clear
     * @param targetCol -- the column the King would land on after castling
     * @param board -- the current board state
     * @return boolean -- true if castling is possible
     */
    private boolean canCastle(int targetCol, Board board)
    {
        if (hasMoved()) return false;
        int row = getBoardRow();
        // Kingside castling
        if (targetCol == getBoardCol() + 2)
        {
            Piece rook = board.getPiece(row, 7);
            if (rook == null || !(rook instanceof Rook) || rook.hasMoved() || rook.isWhite() != isWhite())
            {
                return false;
            }
            // Checks that all squares between King and Rook are empty
            for (int c = getBoardCol() + 1; c < 7; c++)
            {
                if (board.getPiece(row, c) != null) return false;
            }
            return true;
        }
        // Queenside castling
        if (targetCol == getBoardCol() - 2)
        {
            Piece rook = board.getPiece(row, 0);
            if (rook == null || !(rook instanceof Rook) || rook.hasMoved() || rook.isWhite() != isWhite())
            {
                return false;
            }
            // Checks that all squares between King and Rook are empty
            for (int c = 1; c < getBoardCol(); c++)
            {
                if (board.getPiece(row, c) != null) return false;
            }
            return true;
        }
        return false;
    }
}
