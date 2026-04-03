/**
 * Represents a player in the chess game. Tracks the player's name, color,
 * win/loss record, and number of puzzles solved.
 * @author ganeshan
 *
 */
public class Player
{

    private String name;
    private boolean isWhite;
    private int wins;
    private int losses;
    private int puzzlesSolved;

    /**
     * Creates a Player with a name and color
     * @param name -- the player's display name
     * @param isWhite -- true if playing white, false if black
     */
    public Player(String name, boolean isWhite)
    {
        this.name = name;
        this.isWhite = isWhite;
        this.wins = 0;
        this.losses = 0;
        this.puzzlesSolved = 0;
    }

    /**
     * Returns the player's name
     * @return String -- the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns whether this player is playing white
     * @return boolean -- true if white
     */
    public boolean isWhite()
    {
        return isWhite;
    }

    /**
     * Returns the player's total wins
     * @return int -- number of wins
     */
    public int getWins()
    {
        return wins;
    }

    /**
     * Returns the player's total losses
     * @return int -- number of losses
     */
    public int getLosses()
    {
        return losses;
    }

    /**
     * Returns the number of puzzles the player has solved
     * @return int -- puzzles solved count
     */
    public int getPuzzlesSolved()
    {
        return puzzlesSolved;
    }

    /**
     * Increments the player's win count by one
     */
    public void addWin()
    {
        wins++;
    }

    /**
     * Increments the player's loss count by one
     */
    public void addLoss()
    {
        losses++;
    }

    /**
     * Increments the player's puzzles solved count by one
     */
    public void addPuzzleSolved()
    {
        puzzlesSolved++;
    }

    /**
     * Returns a string representation of the player with name and color
     * @return String -- formatted as "Name (White)" or "Name (Black)"
     */
    @Override
    public String toString()
    {
        return name + " (" + (isWhite ? "White" : "Black") + ")";
    }
}
