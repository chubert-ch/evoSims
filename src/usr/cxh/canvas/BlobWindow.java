package usr.cxh.canvas;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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

public class BlobWindow extends JApplet implements ChangeListener {
    ArenaCanvas _arenaCanvas;
    BlobData _blobData = new BlobData();
    int arenaWidth = 1500, arenaHeight = 700;
    Arena arena = new Arena(arenaWidth, arenaHeight);
    public static void main(final String[] args) throws Exception {
        final JFrame f = new JFrame("Blobs");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JApplet ap = new BlobWindow();
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

class BlobData extends JPanel {
    public Blob selectedBlob = new Blob(100, 100);
    JLabel blobSpeedLabel = new JLabel();
    JLabel blobSizeLabel = new JLabel();
    JLabel blobSenseLabel = new JLabel();
    JLabel blobAlgaeLabel = new JLabel();
    JLabel blobEnergyLabel = new JLabel();
    JLabel blobEConLabel = new JLabel();
    BlobData() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(blobSpeedLabel);
        add(blobSizeLabel);
        add(blobSenseLabel);
        add(blobAlgaeLabel);
        add(blobEnergyLabel);
        add(blobEConLabel);
        updateLabels();
    }

    @Override
    public void paint(final Graphics g) {
        updateLabels();
        super.paint(g);
    }

    private void updateLabels() {
        blobSpeedLabel.setText(String.format("Blob speed:          %.2f", selectedBlob.speed));
        blobSizeLabel.setText(String.format("Blob size:           %.1f", selectedBlob.size));
        blobSenseLabel.setText(String.format("Blob sense radius:   %.0f", selectedBlob.senseRadius));
        blobAlgaeLabel.setText(String.format("Blob algae:          %.2f", selectedBlob.algae));
        blobEnergyLabel.setText(String.format("Blob energy/s:       %.0f", selectedBlob.eConsumption()));
    }

    void trackBlob(final Blob blob) {
        selectedBlob = blob;
    }
}
class Food extends Positional {
    public Food(final double x, final double y) {
        super(x, y);
    }
}
