/**
 * Interface that all pieces use for movement methods
 * @author ganesh
 *
 */
public interface Moveable
{

    /**
     * Checks if the piece can move to row, col
     */
    boolean canMoveTo(int row, int col, Board board);

    /**
     * Moves the piece to row, col
     */
    void moveTo(int row, int col);

    /**
     * Gets all possible moves for this piece
     */
    Point[] getPossibleMoves(Board board);
}
