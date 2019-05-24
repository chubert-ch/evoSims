package usr.cxh.canvas;

import static usr.cxh.utils.CollectionUtils.map;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class Arena {
    List<Blob> blobs = new ArrayList<>();
    List<Food> food = new ArrayList<>();
    int _arenaWidth, _arenaHeight;
    List<Blob> winners = new ArrayList<>();

    public Arena(final int arenaWidth, final int arenaHeight) {
        _arenaWidth = arenaWidth;
        _arenaHeight = arenaHeight;
    }

    public void advanceState() {
        int totFood = 0;
        int fullFed = 0;
        for (final Blob blob : new ArrayList<>(blobs)) {
            blob.move(this);
            totFood += blob.food;
            fullFed += blob.food == 2 ? 1 : 0;
        }
        for (final Blob blob : new ArrayList<>(winners)) {
            totFood += blob.food;
            fullFed += blob.food == 2 ? 1 : 0;
        }
//        System.out.println(blobs.size() + "  : " + food.size() + " + " + totFood + "(" + fullFed + ") = "
//                + (totFood + food.size()));
        if (blobs.isEmpty()) {
            reset();
        }
        food.add(new Food(randInt(0, _arenaWidth), randInt(0, _arenaHeight)));
    }

    public void reset() {
        blobs.clear();
        food.clear();
        System.out.println(winners.size());
        if (winners.isEmpty()) {
            // Add normal blobs
            for (int i = 0; i < 20; i++) {
                blobs.add(new Blob(10, 10 + i * 20));
            }
//            // Add algae blobs
//            for (int i = 0; i < 4; i++) {
//                blobs.add(new Blob(500, 10 + i * 20, 20, 0.1, 50, 0, this));
//            }
//            // Add scavenger blobs
//            for (int i = 0; i < 20; i++) {
//                blobs.add(new Blob(200 + i * 20, 600, 5, 1.5, 100, 0, this));
//            }
        } else {
            for (final Blob blob : winners) {
                blobs.add(blob);
                if (blob.food == 2) {
                    blobs.add(new Blob(blob));
                }
                blob.reset();
            }
        }
        double size = 0, speed = 0;
        for (final Blob blob : blobs) {
            size += blob.size;
            speed += blob.speed;
        }
        size = size / blobs.size();
        speed = speed / blobs.size();
        System.out.println(blobs.size() + " blobs. Avg size: " + size + "; Avg speed: " + speed);
        for (int i = 0; i < 100; i++) {
            food.add(new Food(randInt(0, _arenaWidth), randInt(0, _arenaHeight)));
        }
        winners.clear();
    }

    static int randInt(final int min, final int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static double randInt(final double min, final double max) {
        return randInt((int) min, (int) max);
    }

    public List<Double> getBlobData(final Function<Blob, Double> dataGetter) {
        return map(blobs, dataGetter);
    }
}
