package usr.cxh.canvas;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Blob extends Positional {
    double food = 0;
    double energy = 100;
    double size = 10, speed = 2, senseRadius = 100;
    Color _color;
    private final Arena _arena;
    Strat strat = Strat.ROAM;

    enum Strat {
        ROAM, HOME
    }

    Blob(final int x, final int y, final Arena arena) {
        super(x, y);
        _arena = arena;
        _color = new Color((int) speed * 10, (int) size * 10, 100);
        roamTarget = new Positional((x + 500) / 2, (y + 500) / 2);
    }

    Blob(final Blob parent) {
        this((int) parent.x, (int) parent.y, parent._arena);
        speed = parent.speed * (1.0 + (Math.random() - 0.5) * 0.2);
        size = parent.size * (1.0 + (Math.random() - 0.5) * 0.2);
        _color = new Color((int) Math.min(256, speed * 50), (int) Math.min(256, size * 10), 100);
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
            final List<Positional> sortedFood = new ArrayList<>(_arena.food);
            for (final Blob blob: _arena.blobs) {
                if (blob.size * 1.2 < size) {
                    sortedFood.add(blob);
                }
            }
            sortedFood.sort((a, b) -> (int) (a.dist(this) - b.dist(this)));
            hunt = sortedFood.get(0);
            if (hunt.dist(this) > senseRadius) {
                hunt = roamTarget;
            }
            mov = hunt.sub(this);
            break;
        case HOME:
            mov = new Positional(0, 10 - y);
            break;
        default:
            throw new RuntimeException();
        }

        final double factor = Math.min(1, 10 * speed / mov.len());
        x += mov.x * factor;
        y += mov.y * factor;
        if (mov.len() <= speed + size) {
            if (strat == Strat.ROAM) {
                boolean foundFood = false;
                if (hunt instanceof Food) {
                    _arena.food.remove(hunt);
                    foundFood = true;
                } else if (hunt instanceof Blob) {
                    System.out.println("MURDER");
                    _arena.blobs.remove(hunt);
                    foundFood = true;
                }
                else {
                    roamTarget = new Positional(Math.max(0, x + Arena.randInt(-senseRadius, senseRadius)),
                            Math.max(0, y + Arena.randInt(-senseRadius, senseRadius)));
                }
                if (foundFood) {
                    food += 1;
                    //System.out.println(food);
                    energy += 100;
                    if (food == 2) {
                        strat = Strat.HOME;
                    }
                }
            } else {
                _arena.winners.add(this);
                _arena.blobs.remove(this);
                return;
            }
        }
        energy -= speed * speed * size * size * size / 1000;
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
