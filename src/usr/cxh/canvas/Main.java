package usr.cxh.canvas;

import static usr.cxh.utils.CollectionUtils.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main extends JApplet implements ChangeListener {
    ArenaCanvas _arenaCanvas;
    int arenaWidth = 1200, arenaHeight = 700;
    final BlobData _blobData = new BlobData();

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

    Timer timer = null;
    private void initComponents() {
        setLayout(new BorderLayout());
        final JPanel p = new JPanel();
        add("North", p);
        _arenaCanvas = new ArenaCanvas(arena, _blobData);
        p.add("Center", _arenaCanvas);

        final JPanel p2 = new JPanel();
        final JSlider speedSlider = new JSlider(0, 100, 65);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setMajorTickSpacing(20);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(this);
        p2.add(speedSlider);
        final List<BarGraph> graphs = new ArrayList<>();
        graphs.add(new BarGraph(() -> arena.getBlobData(b -> b.size), "size"));
        graphs.add(new BarGraph(() -> arena.getBlobData(b -> b.speed), "speed"));
        graphs.add(new BarGraph(() -> arena.getBlobData(b -> b.senseRadius), "sense radius"));
        graphs.add(new BarGraph(() -> arena.getBlobData(b -> b.algae), "algae"));
        graphs.forEach(g -> p2.add(g));
        p2.add(_blobData);
        add("South", p2);

        final ActionListener arenaListener = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                arena.advanceState();
                _arenaCanvas.repaint();
                _blobData.repaint();
            }
        };
        timer = new Timer(100, arenaListener);
        timer.start();

        final ActionListener graphListener = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                graphs.forEach(g -> g.repaint());
            }
        };
        final Timer graphTimer = new Timer(1000, graphListener);
        graphTimer.start();
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        final JSlider slider = (JSlider) e.getSource();
        final int delay = slider.getValue();
        timer.setDelay(delay);
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
        food.add(new Food(randInt(0, _arenaWidth), randInt(0, _arenaHeight)));
    }

    public void reset() {
        blobs.clear();
        food.clear();
        System.out.println(winners.size());
        if (winners.isEmpty()) {
            // Add normal blobs
            for (int i = 0; i < 20; i++) {
                blobs.add(new Blob(10, 10 + i * 20, this));
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

class Food extends Positional {
    public Food(final double x, final double y) {
        super(x, y);
    }
}

class BlobData extends JPanel {
    public Blob selectedBlob = new Blob(100, 100, null);
    JLabel blobDataLabel = new JLabel("<html><br><br><br><br><br>");

    BlobData() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(blobDataLabel);
        updateLabels();
    }

    @Override
    public void paint(final Graphics g) {
        updateLabels();
        super.paint(g);
    }

    private void updateLabels() {
        blobDataLabel.setText(convertToMultiline(String.format("Blob speed:         %.2f\n", selectedBlob.speed)
                + String.format("Blob sense radius:  %.0f\n", selectedBlob.senseRadius)
                + String.format("Blob algae:         %.2f\n", selectedBlob.algae)
                + String.format("Blob energy:        %.0f\n", selectedBlob.energy)));
    }

    public static String convertToMultiline(final String orig) {
        return "<html>" + orig.replaceAll("\n", "<br>");
    }

    void trackBlob(final Blob blob) {
        selectedBlob = blob;
    }
}

class ArenaCanvas extends Component implements MouseListener {
    private final Arena _arena;
    final BlobData _blobData;

    public ArenaCanvas(final Arena arena, final BlobData blobData) {
        _arena = arena;
        _blobData = blobData;
        addMouseListener(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(_arena._arenaWidth, _arena._arenaHeight);
    }

    @Override
    public void paint(final Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 1200, 700);
        g2.setColor(Color.BLACK);
        Blob deadliestBlob = _arena.blobs.get(0);
        for (final Blob blob : _arena.blobs) {
            g2.setColor(blob._color);
            g2.fillOval((int) (blob.x - blob.size / 2), (int) (blob.y - blob.size / 2), (int) blob.size,
                    (int) blob.size);
            if (deadliestBlob.kills < blob.kills) {
                deadliestBlob = blob;
            }
        }
        for (final Food food : _arena.food) {
            g2.setColor(Color.GREEN);
            g2.fillOval(food.getX(), food.getY(), 4, 4);
        }
        g2.setColor(Color.BLACK);
        g2.drawString(_arena.blobs.size() + "", _arena._arenaWidth - 50, _arena._arenaHeight - 20);
        g2.drawString(deadliestBlob.kills + "", (int) deadliestBlob.x - 10, (int) deadliestBlob.y - 10);
        g2.setColor(Color.RED);
        g2.drawString(String.format("%.0f", _blobData.selectedBlob.energy), (int) _blobData.selectedBlob.x - 10,
                (int) _blobData.selectedBlob.y - 10);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(final MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        final Point p = e.getPoint();
        final ArrayList<Blob> blobs = new ArrayList<>(_arena.blobs);
        blobs.sort((a, b) -> Double.compare(a.dist(p), b.dist(p)));
        _blobData.trackBlob(blobs.get(0));
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(final MouseEvent e) {
        // TODO Auto-generated method stub

    }
}
