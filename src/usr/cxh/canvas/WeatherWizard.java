package usr.cxh.canvas;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WeatherWizard extends JApplet implements ChangeListener {

    WeatherPainter painter;

    @Override
    public void init() {
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
    }

    @Override
    public void start() {
        initComponents();
    }

    public static void main(final String[] args) {
        final JFrame f = new JFrame("Weather Wizard");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JApplet ap = new WeatherWizard();
        ap.init();
        ap.start();
        f.add("Center", ap);
        f.pack();
        f.setVisible(true);

    }

    private BufferedImage loadImage(final String name) {
        final String imgFileName = "images/weather-" + name + ".png";
        final URL url = WeatherWizard.class.getResource(imgFileName);
        BufferedImage img = null;
        try {
            img = ImageIO.read(url);
        } catch (final Exception e) {
            System.err.println("Could not find");
        }
        return img;
    }

    public void initComponents() {

        setLayout(new BorderLayout());

        final JPanel p = new JPanel();
        p.add(new JLabel("Temperature:"));
        final JSlider tempSlider = new JSlider(20, 100, 65);
        tempSlider.setMinorTickSpacing(5);
        tempSlider.setMajorTickSpacing(20);
        tempSlider.setPaintTicks(true);
        tempSlider.setPaintLabels(true);
        tempSlider.addChangeListener(this);
        p.add(tempSlider);
        add("North", p);

        painter = new WeatherPainter();
        painter.sun = loadImage("sun");
        painter.cloud = loadImage("cloud");
        painter.rain = loadImage("rain");
        painter.snow = loadImage("snow");
        painter.setTemperature(65);
        p.add("Center", painter);

    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        final JSlider slider = (JSlider) e.getSource();
        painter.setTemperature(slider.getValue());
    }
}

class WeatherPainter extends Component {

    int temperature = 65;

    String[] conditions = { "Snow", "Rain", "Cloud", "Sun" };
    BufferedImage snow = null;
    BufferedImage rain = null;
    BufferedImage cloud = null;
    BufferedImage sun = null;
    Color textColor = Color.yellow;
    String condStr = "";
    String feels = "";

    Composite alpha0 = null, alpha1 = null;
    BufferedImage img0 = null, img1 = null;

    void setTemperature(final int temp) {
        temperature = temp;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(450, 125);
    }

    void setupText(final String s1, final String s2) {
        if (temperature <= 32) {
            textColor = Color.blue;
            feels = "Freezing";
        } else if (temperature <= 50) {
            textColor = Color.green;
            feels = "Cold";
        } else if (temperature <= 65) {
            textColor = Color.yellow;
            feels = "Cool";
        } else if (temperature <= 75) {
            textColor = Color.orange;
            feels = "Warm";
        } else {
            textColor = Color.red;
            feels = "Hot";
        }
        condStr = s1;
        if (s2 != null) {
            condStr += "/" + s2;
        }
    }

    void setupImages(final BufferedImage i0) {
        alpha0 = null;
        alpha1 = null;
        img0 = i0;
        img1 = null;
    }

    void setupImages(final int min, final int max, final BufferedImage i0, final BufferedImage i1) {
        final float alpha = (max - temperature) / (float) (max - min);
        alpha0 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        alpha1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - alpha);
        img0 = i0;
        img1 = i1;

    }

    void setupWeatherReport() {
        if (temperature <= 32) {
            setupImages(snow);
            setupText("Snow", null);
        } else if (temperature <= 40) {
            setupImages(32, 40, snow, rain);
            setupText("Snow", "Rain");
        } else if (temperature <= 50) {
            setupImages(rain);
            setupText("Rain", null);
        } else if (temperature <= 58) {
            setupImages(50, 58, rain, cloud);
            setupText("Rain", "Cloud");
        } else if (temperature <= 65) {
            setupImages(cloud);
            setupText("Cloud", null);
        } else if (temperature <= 75) {
            setupImages(65, 75, cloud, sun);
            setupText("Cloud", "Sun");
        } else {
            setupImages(sun);
            setupText("Sun", null);
        }
    }

    @Override
    public void paint(final Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        final Dimension size = getSize();
        Composite origComposite;

        setupWeatherReport();

        origComposite = g2.getComposite();
        if (alpha0 != null) {
            g2.setComposite(alpha0);
        }
        g2.drawImage(img0, 0, 0, size.width, size.height, 0, 0, img0.getWidth(null), img0.getHeight(null), null);
        if (img1 != null) {
            if (alpha1 != null) {
                g2.setComposite(alpha1);
            }
            g2.drawImage(img1, 0, 0, size.width, size.height, 0, 0, img1.getWidth(null), img1.getHeight(null), null);
        }
        g2.setComposite(origComposite);

        // Freezing, Cold, Cool, Warm, Hot,
        // Blue, Green, Yellow, Orange, Red
        final Font font = new Font("Serif", Font.PLAIN, 36);
        g.setFont(font);

        final String tempString = feels + " " + temperature + "F";
        final FontRenderContext frc = ((Graphics2D) g).getFontRenderContext();
        final Rectangle2D boundsTemp = font.getStringBounds(tempString, frc);
        final Rectangle2D boundsCond = font.getStringBounds(condStr, frc);
        final int wText = Math.max((int) boundsTemp.getWidth(), (int) boundsCond.getWidth());
        final int hText = (int) boundsTemp.getHeight() + (int) boundsCond.getHeight();
        final int rX = (size.width - wText) / 2;
        final int rY = (size.height - hText) / 2;

        g.setColor(Color.LIGHT_GRAY);
        g2.fillRect(rX, rY, wText, hText);

        g.setColor(textColor);
        final int xTextTemp = rX - (int) boundsTemp.getX();
        final int yTextTemp = rY - (int) boundsTemp.getY();
        g.drawString(tempString, xTextTemp, yTextTemp);

        final int xTextCond = rX - (int) boundsCond.getX();
        final int yTextCond = rY - (int) boundsCond.getY() + (int) boundsTemp.getHeight();
        g.drawString(condStr, xTextCond, yTextCond);

    }
}