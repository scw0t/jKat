/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

public class TreeWithCheck {

    public static Component getTree(int selectionMode) {
        JTree tree = new JTree();
        tree.getSelectionModel().setSelectionMode(selectionMode);
        tree.setVisibleRowCount(8);
        SelectableTreeCellRenderer renderer =
            new SelectableTreeCellRenderer();
        tree.setCellRenderer(renderer);
        JScrollPane scroll = new JScrollPane(tree);
        Dimension d = scroll.getPreferredSize();
        scroll.setPreferredSize(
            new Dimension((int)d.getWidth()*2, (int)d.getHeight()));

        return scroll;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JPanel trees = new JPanel(new GridLayout(0,2,5,5));

                trees.add(
                    getTree(TreeSelectionModel.SINGLE_TREE_SELECTION));
                trees.add(
                    getTree(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION));

                JOptionPane.showMessageDialog(null, trees);
            }
        });
    }
}

class SelectableTreeCellRenderer extends DefaultTreeCellRenderer {

    private JCheckBox selected;
    private JPanel renderComponent;

    public SelectableTreeCellRenderer() {
        selected = new JCheckBox();
        renderComponent = new JPanel(new BorderLayout());
        renderComponent.add(selected,BorderLayout.WEST);

        selected.setOpaque(false);
        renderComponent.setOpaque(false);
    }

    public Component getTreeCellRendererComponent(
        JTree tree,
        Object value,
        boolean sel,
        boolean expanded,
        boolean leaf,
        int row,
        boolean hasFocus) {

        Component c = super.getTreeCellRendererComponent(
            tree,
            value,
            false, // we pass 'false' rather than 'sel'
            expanded,
            leaf,
            row,
            hasFocus);

        selected.setSelected(sel);
        renderComponent.add(c,BorderLayout.CENTER);

        return renderComponent;
    }
}
