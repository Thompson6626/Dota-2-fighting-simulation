package org.example.SwingComponents;

import org.example.Fight;
import org.example.HeroClass.Hero;
import org.example.SwingComponents.HeroChooser.HeroChooserFrame;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import static org.example.WebScrape.DataFetcher.MAXIMUM_HERO_LEVEL;


public class MainPanel extends JPanel implements ActionListener, HeroUpdateListener {

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

    private JLabel[] levelText = new JLabel[2];
    private JComboBox<Integer>[] comboBoxes = new JComboBox[2];
    private JFormattedTextField numberOfFights;

    JButton[][] itemsButtons = new JButton[2][6];
    private final int itemsButtonWidth = 100;
    private final int itemsButtonHeight = 60;

    JLabel timesLabel;

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

        for (int j = 0; j < levelText.length; j++) {
            JLabel label = new JLabel("Level");

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
                int startX = (col < 3) ? 100 : SCREEN_WIDTH - 400;
                int startY = 200;

                // Calculate the actual position of the button
                int x = startX + (col % 3) * itemsButtonWidth;
                int y = startY + row * itemsButtonHeight;

                // Create and configure the button
                JButton button = new JButton();
                button.setFocusable(false);
                button.setBounds(x, y, itemsButtonWidth, itemsButtonHeight);
                button.addActionListener(this);
                this.add(button);

                // Add the button to the array
                itemsButtons[row][col] = button;
            }
        }

    }


    @Override
    public void actionPerformed(ActionEvent e) {

        // Any of the hero buttons
        for (int i = 0; i < heroChooseButtons.length; i++) {
            if(e.getSource() == heroChooseButtons[i]){
                new HeroChooserFrame(this,heroes[i]);
                lastButtonClicked = heroChooseButtons[i];
                break;
            }
        }

        //Any of the level comboboxes
        for (int j = 0; j < comboBoxes.length; j++) {
            if(e.getSource() == comboBoxes[j]){
                int selectedLevel = (int) comboBoxes[j].getSelectedItem();

                if(!heroes[j].heroName.equals("Choose a hero")){
                    heroes[j].heroUpdateToMatchLevel(selectedLevel);
                }
            }
        }

        if(e.getSource() == fightButton){


            int times = Integer.parseInt(numberOfFights.getText().trim());

            int leftWon = 0;
            int rightWon = 0;
            for (int i = 0; i < times ; i++) {
                int res = Fight.fightHeroes(heroes[0],heroes[1]);
                if(res==-1){
                    rightWon++;
                }else{
                    leftWon++;
                }
                heroes[0].toMaxAccordingToLevel();
                heroes[1].toMaxAccordingToLevel();
            }

        }


    }

    private static JFormattedTextField createPositiveIntegerField(int width) {
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

        for (int i = 0; i < comboBoxes.length; i++) {
            if (comboBoxes[i] != null && lastButtonClicked == heroChooseButtons[i]) {
                comboBoxes[i].setSelectedIndex(0);
                break;
            }
        }

    }


}
