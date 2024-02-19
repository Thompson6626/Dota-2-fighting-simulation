package org.example.SwingComponents.HeroChooser;

import org.example.SwingComponents.HeroUpdateListener;
import org.example.WebScrape.DataFetcher;
import org.example.HeroClass.Hero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class HeroChooserPanel extends JPanel implements ActionListener {

    private final HeroUpdateListener listener;
    public static final List<String> HERO_NAMES = DataFetcher.getAllDotaHeroNames();
    private static final Font NAME_FONT = new Font("Optimus Princeps", Font.PLAIN, 20);
    private static final int COLUMNS = 10;
    private static final int ROWS = 10;
    private static final ArrayList<JButton> heroButtons = new ArrayList<>();
    Hero hero;

    public HeroChooserPanel(HeroUpdateListener listener,Hero hero) {
        this.hero = hero;
        this.listener = listener;
        initializeUI();
    }

    private void initializeUI() {
        this.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(ROWS,COLUMNS));
        JScrollPane scrollPane = new JScrollPane(buttonPanel);

        scrollPane.getHorizontalScrollBar().setUnitIncrement(50);
        scrollPane.getHorizontalScrollBar().setBlockIncrement(40);

        for (String heroName : HERO_NAMES) {
            JButton button = new JButton(heroName);
            button.setFocusable(false);
            button.setFont(NAME_FONT);
            button.addActionListener(this);
            buttonPanel.add(button);
            heroButtons.add(button);
        }

        this.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        for (JButton heroButton : heroButtons) {
            if(e.getSource() == heroButton){

                DataFetcher.fillHeroStats(hero,heroButton.getText());

                notifyHeroUpdated(hero);
                // Dispose the window
                if (SwingUtilities.getWindowAncestor(HeroChooserPanel.this) instanceof JFrame frame) {
                    frame.dispose();
                }
            }
        }
    }
    private void notifyHeroUpdated(Hero hero) {
        listener.onHeroUpdated(hero);
    }
}
