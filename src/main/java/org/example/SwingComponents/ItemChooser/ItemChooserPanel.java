package org.example.SwingComponents.ItemChooser;


import org.example.WebScrape.DataFetcher;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ItemChooserPanel extends JPanel {

    List<String> itemNames = DataFetcher.getAllItems();
    private static final Font NAME_FONT = new Font("Optimus Princeps", Font.PLAIN, 20);
    private static final int COLUMNS = 10;
    private static final int ROWS = 10;
    private List<JButton> heroButtons = new ArrayList<>();





}
