package org.example.SwingComponents.ButtonDisplayer;

import org.example.ItemClass.Item;
import org.example.SwingComponents.HeroUpdateListener;
import org.example.SwingComponents.ItemUpdateListener;
import org.example.SwingComponents.NeutralUpdateListener;
import org.example.WebScrape.DataFetcher;
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
    private JButton buttonClicked = null;
    private final List<String> options;
    private static final Font NAME_FONT = new Font("Optimus Princeps", Font.PLAIN, 20);
    private static final int COLUMNS = 10;
    private static final int ROWS = 10;
    private final List<JButton> buttons = new ArrayList<>();
    Hero hero;

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
            JButton buttonClicked
    ){
        this.itemListener = itemListener;
        this.hero = hero;
        this.options = itemNames;
        this.buttonClicked = buttonClicked;
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

        this.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // If the hero buttons was clicked
        for (JButton button : buttons) {
            if(e.getSource() == button){

                if(heroListener != null){
                    DataFetcher.fillHeroStats(hero,button.getText());
                    notifyHeroUpdated(hero);
                }
                if(itemListener != null || neutralListener != null){
                    Item item = DataFetcher.updateAccordingToItem(button.getText());

                    hero.addAndUpdateItem(item);

                    if(itemListener != null ){
                        notifyItemUpdated();
                    }else if(neutralListener != null){
                        notifyNeutralUpdated();
                    }

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
    private void notifyItemUpdated(){
        itemListener.onItemUpdate();
    }
    private void notifyNeutralUpdated(){
        neutralListener.onNeutralUpdate();
    }
}
