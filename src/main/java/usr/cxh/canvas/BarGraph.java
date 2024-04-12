package usr.cxh.canvas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class BarGraph extends Component {

    private final Supplier<List<Double>> _dataSource;
    private double lowestEver = 999999, highestEver = 0;
    private final String _name;

    BarGraph(final Supplier<List<Double>> dataSource, final String name) {
        _dataSource = dataSource;
        _name = name;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 100);
    }

    @Override
    public void paint(final Graphics g) {
        final List<Double> data = _dataSource.get();
        final Dimension actualSize = getSize();
        data.sort(Double::compareTo);
        final Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, actualSize.width, actualSize.height);
        g2.setColor(Color.BLACK);
        final double currMax = Collections.max(data) + 0.01;
        highestEver = Math.max(highestEver, currMax);
        final double currMin = Collections.min(data) - 0.01;
        lowestEver = Math.min(lowestEver, currMin);
        final double barInc = (highestEver - lowestEver) / 20;
        final int barWidth = actualSize.width / 20;
        final Iterator<Double> it = data.iterator();
        double currDatum = it.next();
        int category = 0;
        for (double currentBar = lowestEver; currentBar < highestEver; currentBar += barInc) {
            int numInCategory = 0;
            while (currDatum < currentBar + barInc && currDatum >= currentBar && it.hasNext()) {
                currDatum = it.next();
                numInCategory++;
            }
            final int barHeight = numInCategory * 2 * actualSize.height / data.size();
            g2.drawRect(category * barWidth, actualSize.height - barHeight - 1, barWidth, barHeight);
            category++;
        }
        g2.setColor(Color.RED);
        g2.drawString(String.format("%.2f", lowestEver), 5, 20);
        g2.drawString(String.format("%.2f", highestEver), actualSize.width - 50, 20);
        g2.drawString(_name, actualSize.width / 2 - 50, 20);
    }
}