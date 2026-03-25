/**
 * A support class used to help PuzzleLoader
 * @author justi
 *
 */
public class PuzzleSolver
{

    private PuzzleLoader loader;

    public PuzzleSolver(PuzzleLoader loader)
    {
        this.loader = loader;
    }

    /**
     * Checks if its the correct solution using the getSolution method from the loader
     * @param fromRow -- Original piece row
     * @param fromCol -- Original piece column
     * @param toRow -- Target row
     * @param toCol -- Target column
     * @return
     */
    public boolean checkSolution(int fromRow, int fromCol, int toRow, int toCol)
    {
        int[] solution = loader.getSolution();
        if (solution == null) return false;
        return solution[0] == fromRow && solution[1] == fromCol
                && solution[2] == toRow && solution[3] == toCol;
    }

    /**
     * Calls the Board isCheckmate method to check if checkmate
     * @param board -- the board lol
     * @param isWhite -- Side to check
     * @return
     */
    public boolean checkIfCheckmate(Board board, boolean isWhite)
    {
        return board.isCheckmate(!isWhite);
    }

    /**
     * For babies, gives the String for the correct piece to move, calls
     * puzzleLoader method getSolutionPieceType
     * @return
     */
    public String getHint()
    {
        int type = loader.getSolutionPieceType();
        switch (type)
        {
            case 0: return "Move the King";
            case 1: return "Move the Queen";
            case 2: return "Move the Rook";
            case 3: return "Move the Bishop";
            case 4: return "Move the Knight";
            case 5: return "Move the Pawn";
            default: return "Find the best move";
        }
    }
}
