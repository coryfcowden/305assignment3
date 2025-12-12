package org.example;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.List;

/**
 * Swing panel that displays all analyzed files in a JTree.
 * Updates file selection in the Blackboard when a node is clicked
 * and refreshes the tree dynamically when new files are loaded.
 *
 * @author Cory Cowden
 * @author Xiomara Alcala
 */

public class FilePanel extends JPanel {
    private final JTree tree;
    private final DefaultTreeModel model;

    public FilePanel() {
        setLayout(new BorderLayout());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Files");
        model = new DefaultTreeModel(root);
        tree = new JTree(model);
        JScrollPane scroll = new JScrollPane(tree);
        add(scroll, BorderLayout.CENTER);

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) return;
            Object user = node.getUserObject();
            if (user instanceof String) {
                String name = (String) user;
                Blackboard.getInstance().setSelectedFileName(name);
            }
        });
    }

    public void refresh() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Files");
        List<FileData> files = Blackboard.getInstance().getFiles();
        if (files != null) {
            for (FileData f : files) {
                root.add(new DefaultMutableTreeNode(f.getName()));
            }
        }
        model.setRoot(root);
            for (int i = 0; i < tree.getRowCount(); i++) {
                tree.expandRow(i);
            }
            repaint();
    }
}
