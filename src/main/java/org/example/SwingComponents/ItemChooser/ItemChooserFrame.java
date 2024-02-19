package org.example.SwingComponents.ItemChooser;

import javax.swing.*;
import java.awt.*;

public class ItemChooserFrame extends JFrame {


    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;

    private ItemChooserPanel itemChooserPanel;

    private static final Dimension FRAME_SIZE = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);

    public ItemChooserFrame(){
        this.setPreferredSize(FRAME_SIZE);
        this.setTitle("Choose an item");

        itemChooserPanel = new ItemChooserPanel();
        this.add(itemChooserPanel);

        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setLocationRelativeTo(null);


    }




}
