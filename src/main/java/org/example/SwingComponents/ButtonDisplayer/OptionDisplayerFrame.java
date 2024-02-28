package org.example.SwingComponents.ButtonDisplayer;

import org.example.HeroClass.Hero;
import org.example.SwingComponents.HeroUpdateListener;
import org.example.SwingComponents.ItemUpdateListener;
import org.example.SwingComponents.NeutralUpdateListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OptionDisplayerFrame extends JFrame {

    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private final OptionDisplayerPanel chooserPanel;
    private static final Dimension FRAME_SIZE = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);

    public OptionDisplayerFrame(HeroUpdateListener listener, Hero hero,List<String> heroNames) {
        chooserPanel = new OptionDisplayerPanel(listener, hero, heroNames);
        this.add(chooserPanel);
        initializeGUI("a hero");
    }

    public OptionDisplayerFrame(
            ItemUpdateListener listener,
            Hero hero,
            List<String> itemNames,
            JButton buttonClicked ,
            int inventorySlot
    ) {
        chooserPanel = new OptionDisplayerPanel(listener,hero,itemNames,buttonClicked,inventorySlot);
        this.add(chooserPanel);
        initializeGUI("an item");
    }
    public OptionDisplayerFrame(
            NeutralUpdateListener listener,
            Hero hero,
            List<String> itemNames,
            JButton buttonClicked
    ) {
        chooserPanel = new OptionDisplayerPanel(listener,hero,itemNames,buttonClicked);
        this.add(chooserPanel);
        initializeGUI("a neutral item");
    }

    private void initializeGUI(String text){
        this.setPreferredSize(FRAME_SIZE);
        this.setTitle("Choose " + text);
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
}