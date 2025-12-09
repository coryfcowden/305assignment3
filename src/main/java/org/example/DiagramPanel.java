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
 * Diagram panel using PlantUML to render UML diagrams inside the Assignment 3 project.
 */
public class DiagramPanel extends JPanel {

    private BufferedImage image;

    public DiagramPanel() {
        setBackground(Color.WHITE);

        String uml = "@startuml\n" +
                "!pragma layout smetana\n" +
                "class Foo\n" +
                "class Bar\n" +
                "interface Interactive\n" +
                "abstract class Device\n" +
                "Foo ..|> Interactive\n" +
                "Bar --> Device\n" +
                "Bar ..> Foo\n" +
                "Device *-- Interactive\n" +
                "@enduml";

        loadDiagram(uml);
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
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);
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
