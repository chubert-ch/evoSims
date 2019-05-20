package usr.cxh.canvas;

import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BlobData extends JPanel {
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