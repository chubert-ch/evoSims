package usr.cxh.canvas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class ArenaCanvas extends Component implements MouseListener {
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
        g2.fillRect(0, 0, _arena._arenaWidth, _arena._arenaHeight);
        g2.setColor(Color.BLACK);
        Blob deadliestBlob = _arena.blobs.get(0);
        for (final Blob blob : _arena.blobs) {
            g2.setColor(blob._color);
            g2.fillOval((int) ((int) blob.x - blob.size / 2), (int) ((int) blob.y - blob.size / 2), (int) blob.size,
                    (int) blob.size);
            if (blob.eConsumption() < blob.algae) {
                g2.setColor(blob._secondaryColor);
                g2.drawArc((int) ((int) blob.x - blob.size / 4), (int) ((int) blob.y - blob.size / 4),
                        (int) blob.size / 2,
                        (int) blob.size / 2, 0, 360);
            }
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
    }

    @Override
    public void mousePressed(final MouseEvent e) {
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
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }
}