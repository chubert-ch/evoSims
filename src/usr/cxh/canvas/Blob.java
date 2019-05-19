package usr.cxh.canvas;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Blob extends Positional {

    double food = 0;
    double energy = 100;
    double size = 10, speed = 1, senseRadius = 100, algae = 10;
    Color _color;
    private final Arena _arena;
    Strat strat = Strat.ROAM;
    int kills = 0;

    enum Strat {
        ROAM, HOME
    }

    public Blob(final int x, final int y, final double size, final double speed, final double senseRadius,
            final double algae, final Arena arena) {
        this(x, y, arena);
        this.size = size;
        this.speed = speed;
        this.senseRadius = senseRadius;
        this.algae = algae;
    }

    Blob(final int x, final int y, final Arena arena) {
        super(x, y);
        _arena = arena;
        _color = getColor();
        roamTarget = new Positional((x + 500) / 2, (y + 500) / 2);
    }

    Blob(final Blob parent) {
        this((int) (parent.x + parent.size / 2), (int) (parent.y + parent.size / 2), parent._arena);
        speed = evolve(parent.speed);
        size = evolve(parent.size);
        senseRadius = evolve(parent.senseRadius);
        algae = evolve(parent.algae);
        _color = getColor();
    }

    double evolve(final double d) {
        return d * (1.0 + (Math.random() - 0.5) * 0.2);
    }

    private Color getColor() {
        return new Color((int) Math.min(255, speed * 50), (int) Math.min(255, algae * 10),
                Math.min(255, (int) senseRadius));
    }

    Positional roamTarget;
    void move() {
        final Positional mov;
        Positional hunt = null;
        switch (strat) {
        case ROAM:
            if (_arena.food.isEmpty()) {
                strat = Strat.HOME;
                return;
            }
            final List<Blob> toAvoid = new ArrayList<>();
            for (final Blob blob : _arena.blobs) {
                if (blob.size >= size * 1.2 && blob.dist(this) < blob.size * 1.3) {
                    toAvoid.add(blob);
                }
            }
            final List<Positional> sortedFood = new ArrayList<>(_arena.food);
            for (final Positional food : _arena.food) {
                for (final Blob blob : toAvoid) {
                    if (blob.dist(food) < blob.size * 2) {
                        sortedFood.remove(food);
                    }
                }
            }
            for (final Blob blob: _arena.blobs) {
                if (blob.size * 1.2 < size && (blob.speed * 1.1 <= speed || dist(blob) < size / 2)) {
                    sortedFood.add(blob);
                }
            }
            if (!toAvoid.isEmpty()) {
                toAvoid.sort((a, b) -> Double.compare(a.dist(this), b.dist(this)));
                hunt = add(sub(toAvoid.get(0)));
                hunt.truncateToArena(_arena);
            }
            else {
                sortedFood.sort((a, b) -> Double.compare(a.dist(this), b.dist(this)));
                if (sortedFood.isEmpty()) {
                    hunt = roamTarget;
                }
                else {
                    hunt = sortedFood.get(0);
                    if (hunt.dist(this) > senseRadius) {
                        hunt = roamTarget;
                    }
                }
            }
            mov = hunt.sub(this);
            break;
        case HOME:
            mov = new Positional(0, 10 - y);
            break;
        default:
            throw new RuntimeException();
        }

        final double factor = Math.min(1, 10 * (speed / (1 + algae)) / mov.len());
        x += mov.x * factor;
        y += mov.y * factor;
        if (mov.len() <= speed + size / 2) {
            if (strat == Strat.ROAM) {
                if (hunt instanceof Food || hunt instanceof Blob) {
                    if (hunt instanceof Food) {
                        _arena.food.remove(hunt);
                        food += 1;
                        energy += 100;
                    }
                    if (hunt instanceof Blob) {
                        kills++;
                        _arena.blobs.remove(hunt);
                        food += ((Blob) hunt).size / 10;
                        energy += ((Blob) hunt).size * 10 + ((Blob) hunt).energy;
                    }
//                    if (food >= size / 10 * 2) {
//                        _arena.blobs.add(new Blob(this));
//                        food -= size / 10;
//                    }
                    if (energy >= size * 10 * 2) {
                        _arena.blobs.add(new Blob(this));
                        energy -= size * 10;
                    }
                }
                else {
                    roamTarget = new Positional(x + Arena.randInt(-senseRadius, senseRadius),
                            y + Arena.randInt(-senseRadius, senseRadius));
                    roamTarget.truncateToArena(_arena);
                }
            } else {
                _arena.winners.add(this);
                _arena.blobs.remove(this);
                return;
            }
        }
        energy -= speed * speed * size * size * size / 1000;
        energy += algae; // Slight algae
        if (energy < 0) {
            _arena.blobs.remove(this);
        }
    }

    public void reset() {
        energy = 100;
        food = 0;
        strat = Strat.ROAM;
    }
}
