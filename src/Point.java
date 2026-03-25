/**
 * Simple point class with x and y, used for positions
 * @author ganesh
 *
 */
public class Point
{

    public double x;
    public double y;

    /**
     * Creates a point
     * @param x
     * @param y
     */
    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /** Gets x as int */
    public int getX()
    {
        return (int) x;
    }

    /** Gets y as int */
    public int getY()
    {
        return (int) y;
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Two points are equal if same x and y
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof Point)) return false;
        Point other = (Point) obj;
        return this.getX() == other.getX() && this.getY() == other.getY();
    }

    @Override
    public int hashCode()
    {
        return 31 * getX() + getY();
    }
}
