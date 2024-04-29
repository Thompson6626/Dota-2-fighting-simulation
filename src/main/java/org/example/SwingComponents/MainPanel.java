package org.example.SwingComponents;

import org.example.Fight;
import org.example.HeroClass.Hero;
import org.example.ItemClass.Item;
import org.example.ItemClass.ItemTypes;
import org.example.SwingComponents.ButtonDisplayer.OptionDisplayerFrame;
import org.example.SwingComponents.Buttons.RoundButton;
import org.example.DataFetch.DataFetcher;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static org.example.ItemClass.ItemTypes.NEUTRAL;
import static org.example.ItemClass.ItemTypes.PURCHASABLE;
import static org.example.DataFetch.DataFetcher.MAXIMUM_HERO_LEVEL;


public class MainPanel extends JPanel implements ActionListener,HeroUpdateListener,ItemUpdateListener,NeutralUpdateListener{

    private static final List<String> HERO_NAMES;
    static {
        HERO_NAMES = DataFetcher.getAllDotaHeroNames();
    }
    private static final Map<ItemTypes,List<String>> ALL_ITEMS;
    static {
        ALL_ITEMS = DataFetcher.getItemsGroupedByType();
    }
    private static final int SCREEN_WIDTH = 1300;
    private static final int SCREEN_HEIGHT = 700;
    private static final Dimension SCREEN_SIZE = new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT);

    private final Hero[] heroes = {
            new Hero(),
            new Hero()
    };

    private final JButton[] heroChooseButtons = {
            new JButton(heroes[0].heroName),
            new JButton(heroes[1].heroName)
    };

    private JButton fightButton ;

    private JButton lastButtonClicked = null;

    private final JLabel[] levelText = new JLabel[2];
    private final JComboBox<Integer>[] comboBoxes = new JComboBox[2];
    private JFormattedTextField numberOfFights;

    JButton[][] itemsButtons = new JButton[2][6];
    private final JButton[] neutralButtons = new JButton[2];
    private final int itemsButtonWidth = 100;
    private final int itemsButtonHeight = 60;

    JLabel timesLabel;
    JLabel[] warningLabels = new JLabel[2];
    JLabel fightWarningLabel = new JLabel("Select 2 heroes first");

    JLabel[] heroWins = new JLabel[2];

    MainPanel(){
        initializeGui();
    }

    private void initializeGui() {
        this.setPreferredSize(SCREEN_SIZE);
        this.setLayout(null);

        for(JButton button: heroChooseButtons){
            button.setFocusable(false);
            button.setFont(new Font("Work Sans",Font.BOLD,20));
            button.setBackground(Color.cyan);
            button.addActionListener(this);
            this.add(button);
        }

        heroChooseButtons[0].setBounds(100,100,300,60);
        heroChooseButtons[1].setBounds(SCREEN_WIDTH - 400,100,300,60);

        Integer[] numbers = new Integer[MAXIMUM_HERO_LEVEL];

        for (int i = 0 , len = numbers.length; i < len; i++) {
            numbers[i] = i + 1;
        }

        comboBoxes[0] = new JComboBox<>(numbers);
        comboBoxes[1] = new JComboBox<>(numbers);

        for(JComboBox<Integer> comboBox:comboBoxes){
            comboBox.addActionListener(this);
            comboBox.setFocusable(false);
            comboBox.setFont(new Font("Work Sans",Font.PLAIN,25));
            this.add(comboBox);
        }

        comboBoxes[0].setBounds(
                heroChooseButtons[0].getX() + 330,
                112 ,
                100,
                40
        );

        comboBoxes[1].setBounds(
                heroChooseButtons[1].getX() - 120,
                112 ,
                100,
                40
        );

        for(int i=0;i<levelText.length;i++){
            JLabel label = new JLabel("Level");
            label.setFont(new Font("Work Sans",Font.PLAIN,20));
            this.add(label);
            levelText[i] = label;
        }
        int levelTextWidth = 80;
        int levelTextHeight = 50;
        levelText[0].setBounds(
                comboBoxes[0].getX() + comboBoxes[0].getWidth()/4,
                comboBoxes[0].getY() - levelTextHeight,
                levelTextWidth,
                levelTextHeight
        );

        levelText[1].setBounds(
                comboBoxes[1].getX() + comboBoxes[1].getWidth()/4,
                comboBoxes[1].getY() - levelTextHeight,
                levelTextWidth,
                levelTextHeight
        );




        // Button that has the number of times the fight between the selected heroes will be run
        numberOfFights = createPositiveIntegerField(20);

        numberOfFights.setCaretPosition(0);
        numberOfFights.setText("1");
        numberOfFights.setFont(new Font("Optimus Princeps",Font.BOLD,30));
        numberOfFights.setHorizontalAlignment(JFormattedTextField.CENTER);

        timesLabel = new JLabel("Times");
        timesLabel.setFont(new Font("Optimus Princeps",Font.PLAIN,30));
        timesLabel.setBounds((SCREEN_WIDTH/2)-150 ,400,100,50);
        this.add(timesLabel);

        numberOfFights.setBounds((SCREEN_WIDTH/2)-50 ,400,100,50);
        this.add(numberOfFights);

        // Button to start fight
        fightButton = new JButton("Fight");
        fightButton.setFont(new Font("Verdana",Font.PLAIN,26));
        fightButton.setBounds((SCREEN_WIDTH/2)-50 ,500,100,50);
        fightButton.setFocusable(false);
        fightButton.addActionListener(this);
        this.add(fightButton);


        //Initializing and adding the item buttons for both heroes
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 6; col++) {
                // Determine the starting position based on the column index
                int startX = (row < 1) ? 100 : SCREEN_WIDTH - 400;
                int startY = 200;

                // Calculate the actual position of the button
                int x = startX + (col % 3) * itemsButtonWidth;
                int y = startY + (col < 3 ? 0 : itemsButtonHeight);

                // Create and configure the button
                JButton button = new JButton();
                button.setForeground(Color.white);
                button.setFocusable(false);
                button.setBounds(x, y, itemsButtonWidth, itemsButtonHeight);
                button.addActionListener(this);
                button.setBackground(new Color(47,53,56));
                this.add(button);

                // Add the button to the array
                itemsButtons[row][col] = button;
            }
        }

        for (int i = 0; i < warningLabels.length; i++) {
            JLabel label = new JLabel("Select a hero first");
            label.setVisible(false);
            label.setForeground(Color.red);
            label.setFont(new Font("Verdana",Font.BOLD,20));
            label.setBounds(
                    heroChooseButtons[i].getX() + 50,
                    heroChooseButtons[i].getY() - 80,
                    200,
                    100
            );
            warningLabels[i] = label;
            this.add(label);
        }

        for (int in = 0; in < neutralButtons.length; in++) {
            JButton button = new RoundButton("");
            button.setForeground(Color.white);
            button.setFocusable(false);
            button.addActionListener(this);
            this.add(button);
            neutralButtons[in]=button;
        }





        neutralButtons[0].setBounds(
                itemsButtons[0][2].getX() + 110 ,
                itemsButtons[0][2].getY() + itemsButtons[0][2].getHeight()/2,
                50,
                50
        );

        neutralButtons[1].setBounds(
                itemsButtons[1][0].getX() - 60 ,
                itemsButtons[1][0].getY() + itemsButtons[1][0].getHeight()/2,
                50,
                50
        );

        fightWarningLabel.setForeground(Color.red);
        fightWarningLabel.setBounds(fightButton.getX() - 50, fightButton.getY() + 30,300,100 );
        fightWarningLabel.setFont(new Font("Verdana",Font.BOLD,20));
        fightWarningLabel.setVisible(false);
        this.add(fightWarningLabel);

        for (int i = 0; i < heroWins.length ; i++) {
            JLabel label = new JLabel("0");
            label.setFont(new Font("Cascadia Code",Font.PLAIN,50));
            this.add(label);
            heroWins[i] = label;
        }
        heroWins[0].setBounds(350,500,100,100);

        heroWins[1].setBounds(SCREEN_WIDTH - 400, 500 , 100 , 100);

    }


    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        // Any of the hero buttons
        for (int i = 0; i < heroChooseButtons.length; i++) {
            if(source == heroChooseButtons[i]){
                new OptionDisplayerFrame(
                        this,
                        heroes[i],
                        HERO_NAMES
                );

                lastButtonClicked = heroChooseButtons[i];
                break;
            }
        }
        // Any of the item buttons
        for(int i = 0 ; i < itemsButtons.length; i++){
            for(int j = 0 ; j < itemsButtons[i].length ; j++){
                if(source == itemsButtons[i][j]){
                    if(heroChooseButtons[i].getText().equals("Choose a hero")){
                        showWarningLabel(warningLabels[i]);
                    }else{
                        new OptionDisplayerFrame(
                                this,
                                heroes[i],
                                ALL_ITEMS.get(PURCHASABLE),
                                itemsButtons[i][j],
                                j + 1
                        );
                        lastButtonClicked = itemsButtons[i][j];
                    }
                    break;
                }
            }
        }

        for(int i = 0; i < neutralButtons.length; i++){
            if(source == neutralButtons[i]){
                if(heroChooseButtons[i].getText().equals("Choose a hero")){
                    showWarningLabel(warningLabels[i]);
                }else{
                    new OptionDisplayerFrame(
                            this,
                            heroes[i],
                            ALL_ITEMS.get(NEUTRAL),
                            neutralButtons[i]
                    );
                    lastButtonClicked = neutralButtons[i];
                }
                break;
            }
        }

        //Any of the level comboboxes
        for (int j = 0; j < comboBoxes.length; j++) {
            if(source  == comboBoxes[j]){
                handleLevelSelection(j);
            }
        }

        if(source == fightButton){
            if(!heroChooseButtons[0].getText().equals("Choose a hero")
                    && !heroChooseButtons[1].getText().equals("Choose a hero")
            ){
                handleFightButton();
            }else{
                showWarningLabel(fightWarningLabel);
            }

        }

    }
    private void showWarningLabel(JLabel label) {
        label.setVisible(true);

        // Use a Timer to hide the label after 3 seconds
        Timer timer = new Timer(2000, e -> label.setVisible(false));
        timer.setRepeats(false);
        timer.start();
    }
    private void handleLevelSelection(int index) {
        int selectedLevel = (int) comboBoxes[index].getSelectedItem();

        if (!heroes[index].heroName.equals("Choose a hero")) {
            heroes[index].updateToMatchLevel(selectedLevel);
        }
    }
    private void handleFightButton(){
        int times = Integer.parseInt(numberOfFights.getText().trim());

        Fight.fightAsync(heroes[0], heroes[1], times, result -> SwingUtilities.invokeLater(() -> {
            heroWins[0].setText(String.valueOf(result[0]));
            heroWins[1].setText(String.valueOf(result[1]));
        }));


    }

    private JFormattedTextField createPositiveIntegerField(int width) {
        JFormattedTextField textField = null;
        try {
            MaskFormatter formatter = new MaskFormatter("######"); // 6 digits
            formatter.setAllowsInvalid(false);
            formatter.setCommitsOnValidEdit(true);
            textField = new JFormattedTextField(formatter);
            textField.setColumns(width);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return textField;
    }

    @Override
    public void onHeroUpdated(Hero hero) {
        lastButtonClicked.setText(hero.heroName);

        int i = 0;
        for (; i < comboBoxes.length; i++) {
            if (comboBoxes[i] != null && lastButtonClicked == heroChooseButtons[i]) {
                comboBoxes[i].setSelectedIndex(0);
                break;
            }
        }
        // Resetting buttons text
        JButton[] buttons = itemsButtons[i];

        for(JButton itemButton:buttons)
            itemButton.setText("");

        neutralButtons[i].setText("");

        for(JLabel label:heroWins)
            label.setText("0");

    }
    @Override
    public void onItemUpdate(Item item) {
        lastButtonClicked.setText(item.name);
    }

    @Override
    public void onNeutralUpdate(Item item) {
        lastButtonClicked.setText(item.name);
    }
}
