package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Visual grid representation of analyzed files. Each file is shown
 * as a colored square whose opacity reflects line count and whose color
 * reflects complexity. Clicking a square updates the selected file.
 *
 * @author Cory Cowden
 * @author Xiomara Alcala
 */

public class GridPanel extends JPanel {
    private final JPanel grid;
    private final JLabel selectedFileLabel;

    public GridPanel() {
        setLayout(new BorderLayout());

        grid = new JPanel(new GridLayout(0, 6, 5, 5));
        JScrollPane scroll = new JScrollPane(grid);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("Selected File Name: "));
        selectedFileLabel = new JLabel("None");
        bottom.add(selectedFileLabel);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    public void refresh() {
        grid.removeAll();
        List<FileData> files = Blackboard.getInstance().getFiles();

        if (files == null || files.isEmpty()) {
            revalidate();
            repaint();
            return;
        }

        int maxLines = 1;
        for (FileData f : files) {
            if (f.getLines() > maxLines) maxLines = f.getLines();
        }

        for (FileData data : files) {
            double alphaRatio = (double) data.getLines() / maxLines;
            if (alphaRatio > 1.0) alphaRatio = 1.0;
            int alpha = (int) (alphaRatio * 255);
            if (alpha < 30) alpha = 30; // ensure visible

            Color baseColor;
            if (data.getComplexity() > 10) baseColor = Color.RED;
            else if (data.getComplexity() > 5) baseColor = Color.YELLOW;
            else baseColor = Color.GREEN;

            Color color = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);

            JPanel square = new JPanel();
            square.setOpaque(true);
            square.setBackground(color);
            square.setPreferredSize(new Dimension(60, 60));
            square.setToolTipText(data.getName() + " (" + data.getLines() + " lines, complexity " + data.getComplexity() + ")");

            square.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Blackboard.getInstance().setSelectedFileName(data.getName());
                    selectedFileLabel.setText(data.getName());
                }
            });

            grid.add(square);
        }

        revalidate();
        repaint();
    }
}
