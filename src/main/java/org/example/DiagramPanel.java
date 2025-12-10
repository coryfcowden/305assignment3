package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

/**
 * Diagram panel using PlantUML to render UML diagrams inside the project.
 *
 * Now supports dynamic UML text updates via setUml(String).
 */
public class DiagramPanel extends JPanel {

    private BufferedImage image;
    private String lastUml;

    public DiagramPanel() {
        setBackground(Color.WHITE);
        lastUml = null;
    }

    /**
     * Set PlantUML source text. This will render it and repaint the panel.
     */
    public void setUml(String uml) {
        if (uml == null) return;
        // avoid re-rendering same UML repeatedly
        if (uml.equals(lastUml)) return;
        lastUml = uml;
        loadDiagram(uml);
        revalidate();
        repaint();
    }

    // convert UML text --> PNG --> BufferedImage
    private void loadDiagram(String uml) {
        try {
            SourceStringReader reader = new SourceStringReader(uml);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            reader.outputImage(os, new FileFormatOption(FileFormat.PNG));
            os.close();
            image = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
            image = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            // draw at top-left; you could center or scale if desired
            g.drawImage(image, 0, 0, this);
        } else {
            // placeholder text
            String s = "UML diagram will appear here after loading a repository";
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(s)) / 2;
            int y = (getHeight() / 2) - (fm.getHeight() / 2) + fm.getAscent();
            g.setColor(Color.DARK_GRAY);
            g.drawString(s, x, y);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (image != null) {
            return new Dimension(image.getWidth(), image.getHeight());
        }
        return super.getPreferredSize();
    }
}
