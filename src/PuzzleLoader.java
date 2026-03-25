import java.util.ArrayList;
import java.util.List;

/**
 * Handles the backend and board states of the puzzles
 * @author justi
 *
 */
public class PuzzleLoader
{
	
	// Holds different board states
    private List<int[][]> puzzlePieces;
    private List<int[]> puzzleSolutions;
    private int currentPuzzle;

    public PuzzleLoader()
    {
    	// Initialize
        puzzlePieces = new ArrayList<>();
        puzzleSolutions = new ArrayList<>();
        currentPuzzle = 0;
        initPuzzles();
    }

    /**
     * Loads in prebuilt puzzles into the ArrayList
     */
    private void initPuzzles()
    {
        int[][] puzzle1Pieces =
        {
            {0, 1, 7, 4},
            {1, 1, 5, 7},
            {2, 1, 7, 0},
            {0, 0, 0, 4},
            {2, 0, 0, 0},
        };
        puzzlePieces.add(puzzle1Pieces);
        puzzleSolutions.add(new int[]{1, 5, 7, 0, 5});

        int[][] puzzle2Pieces =
        {
            {0, 1, 7, 6},
            {1, 1, 4, 3},
            {2, 1, 6, 7},
            {0, 0, 0, 7},
            {5, 0, 1, 7},
            {2, 0, 0, 0},
        };
        puzzlePieces.add(puzzle2Pieces);
        puzzleSolutions.add(new int[]{1, 4, 3, 1, 7});

        int[][] puzzle3Pieces =
        {
            {0, 1, 7, 0},
            {2, 1, 3, 7},
            {0, 0, 0, 6},
            {5, 0, 1, 5},
            {5, 0, 1, 6},
            {5, 0, 1, 7},
        };
        puzzlePieces.add(puzzle3Pieces);
        puzzleSolutions.add(new int[]{2, 3, 7, 0, 7});

        int[][] puzzle4Pieces =
        {
            {0, 1, 7, 4},
            {4, 1, 5, 5},
            {1, 1, 6, 3},
            {0, 0, 0, 4},
            {5, 0, 1, 4},
            {3, 0, 0, 5},
        };
        puzzlePieces.add(puzzle4Pieces);
        puzzleSolutions.add(new int[]{4, 5, 5, 3, 4});

        int[][] puzzle5Pieces =
        {
            {0, 1, 7, 3},
            {1, 1, 3, 0},
            {0, 0, 0, 5},
            {5, 0, 0, 6},
            {2, 0, 0, 7},
        };
        puzzlePieces.add(puzzle5Pieces);
        puzzleSolutions.add(new int[]{1, 3, 0, 1, 6});
    }

    /**
     * Returns total number of puzzles
     * @return
     */
    public int getPuzzleCount()
    {
        return puzzlePieces.size();
    }

    /**
     * Returns current puzzle number
     * @return
     */
    public int getCurrentPuzzle()
    {
        return currentPuzzle;
    }

    /** 
     * Helper method to load a board state
     * @param board
     */
    public void loadPuzzle(Board board)
    {
        board.clearBoard();
        if (currentPuzzle >= puzzlePieces.size())
        {
            currentPuzzle = 0;
        }
        int[][] pieces = puzzlePieces.get(currentPuzzle);
        for (int[] pd : pieces)
        {
            int type = pd[0];
            boolean isWhite = pd[1] == 1;
            int row = pd[2];
            int col = pd[3];
            Piece piece = createPiece(type, isWhite, row, col);
            if (piece != null)
            {
                board.assignPiece(piece);
            }
        }
    }

    /**
     * Checks the solution using the puzzleSolver class method
     * @return
     */
    public int[] getSolution()
    {
        if (currentPuzzle >= puzzleSolutions.size())
        {
        	return null;
        }
        int[] sol = puzzleSolutions.get(currentPuzzle);
        return new int[]{sol[1], sol[2], sol[3], sol[4]};
    }

    /**
     * Gets the piece needed to move to complete puzzle
     * Method used specifically for PuzzleSolver
     * @return
     */
    public int getSolutionPieceType()
    {
        if (currentPuzzle >= puzzleSolutions.size())
        {
        	return -1;
        }
        return puzzleSolutions.get(currentPuzzle)[0];
    }

    /**
     * Gets the next puzzle, goes back to puzzle 0 if last puzzle in list
     * is finished
     */
    public void nextPuzzle()
    {
        currentPuzzle++;
        if (currentPuzzle >= puzzlePieces.size())
        {
            currentPuzzle = 0;
        }
    }

    /**
     * Creates a piece at specified location
     * @param type - What piece u talking
     * @param isWhite - duh
     * @param row - row of placed piece
     * @param col - column of placed piece
     * @return
     */
    private Piece createPiece(int type, boolean isWhite, int row, int col)
    {
        if (type == 0) {
        	return new King(isWhite, row, col);
        }
        else if (type == 1) {
        	return new Queen(isWhite, row, col);
        }
        else if (type == 2) {
        	return new Rook(isWhite, row, col);
        }
        else if (type == 3) {
        	return new Bishop(isWhite, row, col);
        }
        else if (type == 4) {
        	return new Knight(isWhite, row, col);
        }
        else if (type == 5) {
        	return new Pawn(isWhite, row, col);
        }
        else {
        	return null;
        }
    }
}
