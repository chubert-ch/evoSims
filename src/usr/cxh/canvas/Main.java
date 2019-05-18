package usr.cxh.canvas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main extends JApplet implements ChangeListener {
    ArenaCanvas _arenaCanvas;
    int arenaWidth = 1200, arenaHeight = 700;
    Arena arena = new Arena(arenaWidth, arenaHeight);
    public static void main(final String[] args) throws Exception {
        final JFrame f = new JFrame("Weather Wizard");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JApplet ap = new Main();
        ap.init();
        ap.start();
        f.add("Center", ap);
        f.pack();
        f.setVisible(true);

    }

    @Override
    public void start() {
        initComponents();
        arena.reset();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        final JPanel p = new JPanel();
        add("North", p);
        _arenaCanvas = new ArenaCanvas(arena);
        p.add("Center", _arenaCanvas);
        final ActionListener listener = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                arena.advanceState();
                _arenaCanvas.repaint();
            }
        };
        final Timer timer = new Timer(100, listener);
        timer.start();
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        // TODO Auto-generated method stub

    }
}

class Arena {
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
            blob.move();
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
    }

    public void reset() {
        blobs.clear();
        food.clear();
        System.out.println(winners.size());
        if (winners.isEmpty()) {
            for (int i = 0; i < 50; i++) {
                blobs.add(new Blob(10, 10 + i * 10, this));
            }
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
}

class Food extends Positional {
    public Food(final double x, final double y) {
        super(x, y);
    }
}

class ArenaCanvas extends Component {
    private final Arena _arena;

    public ArenaCanvas(final Arena arena) {
        _arena = arena;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(_arena._arenaWidth, _arena._arenaHeight);
    }

    @Override
    public void paint(final Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        final Dimension size = getSize();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 1200, 700);
        g2.setColor(Color.BLACK);
        g2.drawString("Hello", 0, 0);
        for (final Blob blob : _arena.blobs) {
            g2.setColor(blob._color);
            g2.fillOval((int) (blob.x - blob.size / 2), (int) (blob.y - blob.size / 2), (int) blob.size,
                    (int) blob.size);
        }
        for (final Food food : _arena.food) {
            g2.setColor(Color.GREEN);
            g2.fillOval(food.getX(), food.getY(), 4, 4);
        }
    }
}
