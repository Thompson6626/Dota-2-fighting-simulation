package org.example;

import javax.swing.*;
import org.example.SwingComponents.MenuFrame;
public class Main  {

    public static void main(String[] args) {

        //MenuFrame frame = new MenuFrame();
        SwingUtilities.invokeLater(MenuFrame::new);
    }
}