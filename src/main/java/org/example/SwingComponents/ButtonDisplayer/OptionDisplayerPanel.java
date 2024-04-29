package org.example.SwingComponents.ButtonDisplayer;

import org.example.ItemClass.Item;
import org.example.SwingComponents.HeroUpdateListener;
import org.example.SwingComponents.ItemUpdateListener;
import org.example.SwingComponents.NeutralUpdateListener;
import org.example.DataFetch.DataFetcher;
import org.example.HeroClass.Hero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class OptionDisplayerPanel extends JPanel implements ActionListener {

    private HeroUpdateListener heroListener = null;
    private ItemUpdateListener itemListener = null;
    private NeutralUpdateListener neutralListener = null;

    private final List<String> options;
    private static final Font NAME_FONT = new Font("Optimus Princeps", Font.PLAIN, 20);
    private static final int COLUMNS = 10;
    private static final int ROWS = 10;
    private final List<JButton> buttons = new ArrayList<>();
    private final Hero hero;
    private JButton buttonClicked;
    private int inventorySlot;

    // Display for heroes
    public OptionDisplayerPanel(
            HeroUpdateListener heroListener,
            Hero hero,
            List<String> heroNames
    ) {
        this.hero = hero;
        this.heroListener = heroListener;
        this.options = heroNames;
        initializeGUI();
    }
    // Display for normal items
    public OptionDisplayerPanel(
            ItemUpdateListener itemListener,
            Hero hero,
            List<String> itemNames,
            JButton buttonClicked ,
            int itemSlot
    ){
        this.itemListener = itemListener;
        this.hero = hero;
        this.options = itemNames;
        this.buttonClicked = buttonClicked;
        this.inventorySlot = itemSlot;
        initializeGUI();
    }

    //Display for neutral items
    public OptionDisplayerPanel(
            NeutralUpdateListener neutralListener,
            Hero hero,
            List<String> neutralNames,
            JButton buttonClicked
    ){
        this.neutralListener = neutralListener;
        this.hero = hero;
        this.options = neutralNames;
        this.buttonClicked = buttonClicked;
        initializeGUI();
    }


    private void initializeGUI() {
        this.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(ROWS,COLUMNS));
        JScrollPane scrollPane = new JScrollPane(buttonPanel);

        scrollPane.getHorizontalScrollBar().setUnitIncrement(50);
        scrollPane.getHorizontalScrollBar().setBlockIncrement(40);




        for (String name : options) {
            JButton button = new JButton(name);
            button.setFocusable(false);
            button.setFont(NAME_FONT);
            button.addActionListener(this);
            buttonPanel.add(button);
            buttons.add(button);
        }
        if(itemListener != null || neutralListener != null){
            JButton deleteButton = new JButton("X");
            deleteButton.setForeground(Color.RED);
            deleteButton.setFont(new Font(NAME_FONT.getFontName(),Font.BOLD,30));
            deleteButton.setFocusable(false);
            deleteButton.addActionListener(this);
            buttonPanel.add(deleteButton);
            buttons.add(deleteButton);
        }


        this.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        // If the hero buttons was clicked
        for (JButton button : buttons) {
            if(source == button){
                if(heroListener != null){

                    // Removing all items
                    for(int slotIndex:hero.items.keySet()){
                        hero.updateHerosItem(hero.items.get(slotIndex),false,slotIndex);
                    }
                    // -1 or 0 because the neutral item has its own space
                    hero.updateHerosItem(hero.neutralItem,false,-1);

                    DataFetcher.fillHeroStats(hero,button.getText());

                    notifyHeroUpdated(hero);
                }
                if(itemListener != null || neutralListener != null){

                    boolean deletes = button.getText().equals("X");

                    Item item = new Item();
                    if(!deletes){
                        item = DataFetcher.getItem(button.getText());

                        hero.updateHerosItem(item,true,inventorySlot);
                    }else{
                        hero.updateHerosItem(hero.items.get(inventorySlot),false,inventorySlot);
                    }

                    if(itemListener != null ) notifyItemUpdated(item);
                    else if(neutralListener != null) notifyNeutralUpdated(item);
                }

                // Dispose the window
                if (SwingUtilities.getWindowAncestor(OptionDisplayerPanel.this) instanceof JFrame frame) {
                    frame.dispose();
                }
            }
        }

    }
    private void notifyHeroUpdated(Hero hero) {
        heroListener.onHeroUpdated(hero);
    }
    private void notifyItemUpdated(Item item){
        itemListener.onItemUpdate(item);
    }
    private void notifyNeutralUpdated(Item item){
        neutralListener.onNeutralUpdate(item);
    }
}
