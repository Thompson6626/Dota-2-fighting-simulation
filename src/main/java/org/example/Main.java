package org.example;

import org.example.SwingComponents.MenuFrame;

import javax.swing.*;

public class Main  {

    public static void main(String[] args) {

        //MenuFrame frame = new MenuFrame();
        SwingUtilities.invokeLater(MenuFrame::new);
    }
}