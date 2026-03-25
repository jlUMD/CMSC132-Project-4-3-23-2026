/**
 * Keeps track of a player's info like name, color, and stats
 * @author ganesh
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
     * Creates player with name and color
     * @param name
     * @param isWhite
     */
    public Player(String name, boolean isWhite)
    {
        this.name = name;
        this.isWhite = isWhite;
        this.wins = 0;
        this.losses = 0;
        this.puzzlesSolved = 0;
    }

    // Getters
    public String getName()
    {
        return name;
    }

    public boolean isWhite()
    {
        return isWhite;
    }

    public int getWins()
    {
        return wins;
    }

    public int getLosses()
    {
        return losses;
    }

    public int getPuzzlesSolved()
    {
        return puzzlesSolved;
    }

    /** Adds a win */
    public void addWin()
    {
        wins++;
    }

    /** Adds a loss */
    public void addLoss()
    {
        losses++;
    }

    /** Adds a puzzle solved */
    public void addPuzzleSolved()
    {
        puzzlesSolved++;
    }

    /**
     * toString for player
     */
    @Override
    public String toString()
    {
        return name + " (" + (isWhite ? "White" : "Black") + ")";
    }
}
