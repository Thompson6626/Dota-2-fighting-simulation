package org.example.SwingComponents;

import javax.swing.*;
import java.awt.*;

public class MenuFrame extends JFrame {

    MainPanel mainPanel;
    public MenuFrame(){
        mainPanel = new MainPanel();
        this.add(mainPanel);
        this.setTitle("Hero fight simulator");
        this.setResizable(false);
        this.setBackground(Color.WHITE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
