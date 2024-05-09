package org.example.SwingComponents;

import org.example.DataFetch.DataFetcher;
import org.example.Fight;
import org.example.HeroClass.Hero;
import org.example.ItemClass.Item;
import org.example.ItemClass.ItemTypes;
import org.example.SwingComponents.ButtonDisplayer.OptionDisplayerFrame;
import org.example.SwingComponents.Buttons.RoundButton;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

    private static final Hero[] HEROES = {
            new Hero(),
            new Hero()
    };

    private static final JButton[] HERO_CHOOSE_BUTTONS = {
            new JButton(HEROES[0].heroName),
            new JButton(HEROES[1].heroName)
    };

    private static JButton fightButton;

    private static JButton lastButtonClicked = null;

    private static final JLabel[] LEVEL_TEXT = new JLabel[2];
    private static final JComboBox<Integer>[] J_COMBO_BOXES = new JComboBox[2];
    private JFormattedTextField numberOfFights;
    private static final List<JComponent> ALL_INTERACTABLE = new ArrayList<>();
    private static final JButton[][] ITEMS_BUTTONS = new JButton[2][6];
    private static final JButton[] NEUTRAL_ITEM_BUTTONS = new JButton[2];
    private static final int ITEMS_BUTTON_WIDTH = 100;
    private static final int ITEMS_BUTTON_HEIGHT = 60;
    private static JLabel timesLabel;
    private static final JLabel[] WARNING_LABELS = new JLabel[2];
    private static final JLabel FIGHT_WARNING_LABEL = new JLabel("Select 2 heroes first");

    private static final JLabel[] HERO_WINS = new JLabel[2];

    MainPanel(){
        initializeGui();
    }

    private void initializeGui() {
        this.setPreferredSize(SCREEN_SIZE);
        this.setLayout(null);

        for(JButton button: HERO_CHOOSE_BUTTONS){
            button.setFocusable(false);
            button.setFont(new Font("Work Sans",Font.BOLD,20));
            button.setBackground(Color.cyan);
            button.addActionListener(this);
            ALL_INTERACTABLE.add(button);
            this.add(button);
        }

        HERO_CHOOSE_BUTTONS[0].setBounds(100,100,300,60);
        HERO_CHOOSE_BUTTONS[1].setBounds(SCREEN_WIDTH - 400,100,300,60);



        Integer[] numbers = new Integer[DataFetcher.MAXIMUM_HERO_LEVEL];

        for (int i = 1 , len = numbers.length; i <= len; i++) {
            numbers[i - 1] = i;
        }

        J_COMBO_BOXES[0] = new JComboBox<>(numbers);
        J_COMBO_BOXES[1] = new JComboBox<>(numbers);

        for(JComboBox<Integer> comboBox : J_COMBO_BOXES){
            comboBox.addActionListener(this);
            comboBox.setFocusable(false);
            comboBox.setFont(new Font("Work Sans",Font.PLAIN,25));
            ALL_INTERACTABLE.add(comboBox);
            this.add(comboBox);
        }

        J_COMBO_BOXES[0].setBounds(
                HERO_CHOOSE_BUTTONS[0].getX() + 330,
                112 ,
                100,
                40
        );

        J_COMBO_BOXES[1].setBounds(
                HERO_CHOOSE_BUTTONS[1].getX() - 120,
                112 ,
                100,
                40
        );

        for(int i = 0; i < LEVEL_TEXT.length; i++){
            JLabel label = new JLabel("Level");
            label.setFont(new Font("Work Sans",Font.PLAIN,20));
            this.add(label);
            LEVEL_TEXT[i] = label;
        }
        int levelTextWidth = 80;
        int levelTextHeight = 50;
        LEVEL_TEXT[0].setBounds(
                J_COMBO_BOXES[0].getX() + J_COMBO_BOXES[0].getWidth() / 4,
                J_COMBO_BOXES[0].getY() - levelTextHeight,
                levelTextWidth,
                levelTextHeight
        );

        LEVEL_TEXT[1].setBounds(
                J_COMBO_BOXES[1].getX() + J_COMBO_BOXES[1].getWidth() / 4,
                J_COMBO_BOXES[1].getY() - levelTextHeight,
                levelTextWidth,
                levelTextHeight
        );




        // Button that has the number of times the fight between the selected heroes will be run
        numberOfFights = createPositiveIntegerField(20);

        numberOfFights.setCaretPosition(0);
        numberOfFights.setText("1");
        numberOfFights.setFont(new Font("Optimus Princeps",Font.BOLD,30));
        numberOfFights.setHorizontalAlignment(JFormattedTextField.CENTER);
        ALL_INTERACTABLE.add(numberOfFights);

        timesLabel = new JLabel("Times");
        timesLabel.setFont(new Font("Optimus Princeps",Font.PLAIN,30));
        timesLabel.setBounds((SCREEN_WIDTH/2)-150 ,400,100,50);
        this.add(timesLabel);

        numberOfFights.setBounds((SCREEN_WIDTH/2)-50 ,400,100,50);
        this.add(numberOfFights);

        // Button to start fight
        fightButton = new JButton("Fight");
        fightButton.setFont(new Font("Verdana",Font.PLAIN,26));
        fightButton.setBounds((SCREEN_WIDTH / 2) - 50 ,500,100,50);
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
                int x = startX + (col % 3) * ITEMS_BUTTON_WIDTH;
                int y = startY + (col < 3 ? 0 : ITEMS_BUTTON_HEIGHT);

                // Create and configure the button
                JButton button = new JButton();
                button.setForeground(Color.white);
                button.setFocusable(false);
                button.setBounds(x, y, ITEMS_BUTTON_WIDTH, ITEMS_BUTTON_HEIGHT);
                button.addActionListener(this);
                button.setBackground(new Color(47,53,56));
                ALL_INTERACTABLE.add(button);
                this.add(button);

                // Add the button to the array
                ITEMS_BUTTONS[row][col] = button;
            }
        }

        for (int i = 0; i < WARNING_LABELS.length; i++) {
            JLabel label = new JLabel("Select a hero first");
            label.setVisible(false);
            label.setForeground(Color.red);
            label.setFont(new Font("Verdana",Font.BOLD,20));
            label.setBounds(
                    HERO_CHOOSE_BUTTONS[i].getX() + 50,
                    HERO_CHOOSE_BUTTONS[i].getY() - 80,
                    200,
                    100
            );
            WARNING_LABELS[i] = label;
            this.add(label);
        }

        for (int in = 0; in < NEUTRAL_ITEM_BUTTONS.length; in++) {
            JButton button = new RoundButton("");
            button.setForeground(Color.white);
            button.setFocusable(false);
            button.addActionListener(this);
            ALL_INTERACTABLE.add(button);
            this.add(button);
            NEUTRAL_ITEM_BUTTONS[in] = button;
        }






        NEUTRAL_ITEM_BUTTONS[0].setBounds(
                ITEMS_BUTTONS[0][2].getX() + 110 ,
                ITEMS_BUTTONS[0][2].getY() + ITEMS_BUTTONS[0][2].getHeight()/2,
                50,
                50
        );

        NEUTRAL_ITEM_BUTTONS[1].setBounds(
                ITEMS_BUTTONS[1][0].getX() - 60 ,
                ITEMS_BUTTONS[1][0].getY() + ITEMS_BUTTONS[1][0].getHeight()/2,
                50,
                50
        );

        FIGHT_WARNING_LABEL.setForeground(Color.red);
        FIGHT_WARNING_LABEL.setBounds(fightButton.getX() - 50, fightButton.getY() + 30,300,100 );
        FIGHT_WARNING_LABEL.setFont(new Font("Verdana",Font.BOLD,20));
        FIGHT_WARNING_LABEL.setVisible(false);
        this.add(FIGHT_WARNING_LABEL);

        for (int i = 0; i < HERO_WINS.length ; i++) {
            JLabel label = new JLabel("0");
            label.setFont(new Font("Cascadia Code",Font.PLAIN,50));
            this.add(label);
            HERO_WINS[i] = label;
        }
        HERO_WINS[0].setBounds(350,500,100,100);

        HERO_WINS[1].setBounds(SCREEN_WIDTH - 400, 500 , 100 , 100);

    }


    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        // Any of the hero buttons
        for (int i = 0; i < HERO_CHOOSE_BUTTONS.length; i++) {
            if(source == HERO_CHOOSE_BUTTONS[i]){
                new OptionDisplayerFrame(
                        (HeroUpdateListener) this,
                        HEROES[i],
                        HERO_NAMES
                );
                lastButtonClicked = HERO_CHOOSE_BUTTONS[i];
                break;
            }
        }
        // Any of the item buttons
        for(int i = 0; i < ITEMS_BUTTONS.length; i++){
            for(int j = 0; j < ITEMS_BUTTONS[i].length ; j++){
                if(source == ITEMS_BUTTONS[i][j]){
                    if(HERO_CHOOSE_BUTTONS[i].getText().equals(Hero.PLACEHOLDER_NAME)){
                        showWarningLabel(WARNING_LABELS[i]);
                    }else{
                        new OptionDisplayerFrame(
                                this,
                                HEROES[i],
                                ALL_ITEMS.get(ItemTypes.PURCHASABLE),
                                j + 1
                        );
                        lastButtonClicked = ITEMS_BUTTONS[i][j];
                    }
                    break;
                }
            }
        }

        for(int i = 0; i < NEUTRAL_ITEM_BUTTONS.length; i++){
            if(source == NEUTRAL_ITEM_BUTTONS[i]){
                if(HERO_CHOOSE_BUTTONS[i].getText().equals(Hero.PLACEHOLDER_NAME)){
                    showWarningLabel(WARNING_LABELS[i]);
                }else{
                    new OptionDisplayerFrame(
                            (NeutralUpdateListener) this,
                            HEROES[i],
                            ALL_ITEMS.get(ItemTypes.NEUTRAL)
                    );
                    lastButtonClicked = NEUTRAL_ITEM_BUTTONS[i];
                }
                break;
            }
        }

        //Any of the level comboboxes
        for (int j = 0; j < J_COMBO_BOXES.length; j++) {
            if(source  == J_COMBO_BOXES[j]){
                if(HERO_CHOOSE_BUTTONS[j].getText().equals(Hero.PLACEHOLDER_NAME)){
                    J_COMBO_BOXES[j].setSelectedIndex(0);
                    showWarningLabel(WARNING_LABELS[j]);
                }else{
                    handleLevelSelection(j);
                }
            }
        }


        if(source == fightButton){
            if(!HERO_CHOOSE_BUTTONS[0].getText().equals(Hero.PLACEHOLDER_NAME)
                    && !HERO_CHOOSE_BUTTONS[1].getText().equals(Hero.PLACEHOLDER_NAME)
            ){
                handleFightButton();
            }else{
                showWarningLabel(FIGHT_WARNING_LABEL);
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
        int selectedLevel = (int) J_COMBO_BOXES[index].getSelectedItem();

        if (!HEROES[index].heroName.equals(Hero.PLACEHOLDER_NAME)) {
            HEROES[index].updateToMatchLevel(selectedLevel);
        }
    }
    private void handleFightButton(){
        if (!Fight.hasOngoingFight()){
            Fight.clearCombatLog();
            for(JLabel label: HERO_WINS)
                label.setText("0");
        }

        int times = Integer.parseInt(numberOfFights.getText().trim());
        if(!Fight.hasOngoingFight()){
            Fight.fightAsync(HEROES[0], HEROES[1], times);
            ALL_INTERACTABLE.forEach(jComponent -> jComponent.setEnabled(false));
        }else if(!Fight.getCombatLogVisibility()){
            Fight.makeCombatLogVisible();
        }
        Fight.centerCombatLog();
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
        for (; i < J_COMBO_BOXES.length; i++) {
            if (J_COMBO_BOXES[i] != null && lastButtonClicked == HERO_CHOOSE_BUTTONS[i]) {
                J_COMBO_BOXES[i].setSelectedIndex(0);
                break;
            }
        }
        HEROES[i] = hero;

        // Resetting buttons text
        JButton[] buttons = ITEMS_BUTTONS[i];



        for(JButton itemButton:buttons)
            itemButton.setText("");

        NEUTRAL_ITEM_BUTTONS[i].setText("");

        for(JLabel label: HERO_WINS)
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

    public static JLabel[] getHeroWins() {
        return HERO_WINS;
    }

    public static List<JComponent> getAllInteractable() {
        return ALL_INTERACTABLE;
    }
}
