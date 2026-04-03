/**
 * Represents a 2D point with x and y coordinates. Used for board positions
 * and screen positions throughout the project.
 * @author ganeshan
 *
 */
public class Point
{

    public double x;
    public double y;

    /**
     * Creates a Point with the given x and y coordinates
     * @param x -- the x coordinate (column or horizontal position)
     * @param y -- the y coordinate (row or vertical position)
     */
    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x coordinate as an integer
     * @return int -- the x value cast to int
     */
    public int getX()
    {
        return (int) x;
    }

    /**
     * Returns the y coordinate as an integer
     * @return int -- the y value cast to int
     */
    public int getY()
    {
        return (int) y;
    }

    /**
     * Returns a string representation of the point as "(x, y)"
     * @return String -- formatted point string
     */
    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Checks equality between two Points by comparing their integer
     * x and y values
     * @param obj -- the object to compare to
     * @return boolean -- true if the points have the same coordinates
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof Point)) return false;
        Point other = (Point) obj;
        return this.getX() == other.getX() && this.getY() == other.getY();
    }

    /**
     * Returns a hash code based on the integer x and y values
     * @return int -- the hash code
     */
    @Override
    public int hashCode()
    {
        return 31 * getX() + getY();
    }
}
