package usr.cxh.canvas;

import javax.swing.*;
import java.awt.*;

class BlobData extends JPanel {
    public Blob selectedBlob = new Blob(100, 100, null);
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
