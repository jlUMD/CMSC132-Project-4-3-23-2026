/**
 * Interface for all moveable chess pieces. Defines the required movement
 * methods that each piece must implement.
 * @author ganeshan
 *
 */
public interface Moveable
{

    /**
     * Checks if the piece can legally move to the specified position
     * @param row -- target row
     * @param col -- target column
     * @param board -- the current board state
     * @return boolean -- true if the move is valid
     */
    boolean canMoveTo(int row, int col, Board board);

    /**
     * Moves the piece to the specified position, updating its internal
     * row and column values
     * @param row -- target row
     * @param col -- target column
     */
    void moveTo(int row, int col);

    /**
     * Returns an array of all possible moves for this piece on the board
     * @param board -- the current board state
     * @return Point[] -- array of possible move locations
     */
    Point[] getPossibleMoves(Board board);
}
