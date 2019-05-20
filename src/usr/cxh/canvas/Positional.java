package usr.cxh.canvas;

import java.awt.Point;

// This class shouldn't exist :(
public class Positional {
    public int getX() {
        return (int) x;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public int getY() {
        return (int) y;
    }

    public void setY(final double y) {
        this.y = y;
    }

    public Positional(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public double x, y;

    double dist(final Positional other) {
        return len(other.x - x, other.y - y);
    }

    double dist(final Point other) {
        return len(other.x - x, other.y - y);
    }

    Positional sub(final Positional other) {
        return new Positional(x - other.x, y - other.y);
    }

    Positional add(final Positional other) {
        return new Positional(x + other.x, y + other.y);
    }

    static double len(final double x, final double y) {
        return Math.sqrt(x * x + y * y);
    }

    double len() {
        return len(x, y);
    }

    Positional pos() {
        return new Positional(x, y);
    }

    Positional mult(final double d) {
        return new Positional(x * d, y * d);
    }

    void truncateToArena(final Arena arena) {
        x = Math.min(arena._arenaWidth, Math.max(0, x));
        y = Math.min(arena._arenaHeight, Math.max(0, y));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
