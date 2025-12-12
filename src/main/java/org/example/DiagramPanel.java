package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

import net.sourceforge.plantuml.*;

/**
 * Swing panel that generates and displays UML diagrams using PlantUML.
 * Converts UML source strings into PNG images and renders them in the UI.
 *
 * @author Cory Cowden
 * @author Xiomara Alcala
 */

public class DiagramPanel extends JPanel {

        private BufferedImage image;
        private String lastUml;

        public DiagramPanel() {
                setBackground(Color.WHITE);
                lastUml = null;
        }

        public void setUml(String uml) {
                if (uml == null) return;

                // avoid unnecessary re-renders
                if (uml.equals(lastUml)) return;

                lastUml = uml;
                loadDiagram(uml);
                revalidate();
                repaint();
        }

        /**
         * Turns a UML text string into a PNG image using PlantUML.
         */
        private void loadDiagram(String uml) {
                try {
                SourceStringReader reader = new SourceStringReader(uml);
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                // PlantUML renders directly into the stream
                reader.outputImage(out, new FileFormatOption(FileFormat.PNG));
                out.close();

                byte[] data = out.toByteArray();
                image = ImageIO.read(new ByteArrayInputStream(data));

                } catch (IOException e) {
                e.printStackTrace();
                image = null;
        }
        }

        @Override
        protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (image != null) {
                // Draw at (0,0); not centered or scaled
                g.drawImage(image, 0, 0, this);
                return;
                }

                // Fallback text when no UML is loaded yet
                String msg = "UML diagram will appear here after loading a repository";
                FontMetrics fm = g.getFontMetrics();

                int x = (getWidth() - fm.stringWidth(msg)) / 2;
                int y = (getHeight() / 2) + fm.getAscent() / 2;

                g.setColor(Color.DARK_GRAY);
                g.drawString(msg, x, y);
        }

        @Override
        public Dimension getPreferredSize() {
                if (image != null) {
                return new Dimension(image.getWidth(), image.getHeight());
                }
                return super.getPreferredSize();
        }
}
