package org.example.SwingComponents.Buttons;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class RoundButton extends JButton {

    public RoundButton(String label) {
        super(label);
        setBackground(new Color(47,53,56)); // Set background color
        setFocusPainted(false); // Remove default focus border
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add some padding
        setContentAreaFilled(false); // Set content area filled to false for custom painting
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.DARK_GRAY);
        } else {
            g.setColor(getBackground());
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        g2.fill(new Ellipse2D.Double(0, 0, width, height));
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        ((Graphics2D) g).draw(new Ellipse2D.Double(0, 0, getSize().width, getSize().height));
    }

    @Override
    public boolean contains(int x, int y) {
        Shape shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        return shape.contains(x, y);
    }
}
