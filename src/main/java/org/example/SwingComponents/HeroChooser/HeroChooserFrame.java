package org.example.SwingComponents.HeroChooser;

import org.example.HeroClass.Hero;
import org.example.SwingComponents.HeroUpdateListener;

import javax.swing.*;
import java.awt.*;

public class HeroChooserFrame extends JFrame {

    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private HeroChooserPanel chooserPanel;
    private static final Dimension FRAME_SIZE = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);

    public HeroChooserFrame(HeroUpdateListener listener, Hero hero) {
        this.setPreferredSize(FRAME_SIZE);
        this.setTitle("Choose a hero");

        chooserPanel = new HeroChooserPanel(listener, hero);
        this.add(chooserPanel);

        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setLocationRelativeTo(null); // Center the frame on the screen
    }

}