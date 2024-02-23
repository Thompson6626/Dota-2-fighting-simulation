package org.example.SwingComponents.CombatLog;

import org.example.HeroClass.Hero;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.Timer;

public class CombatLogF {


    private JFrame combatLogFrame;
    private JPanel combatLogPanel;

    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 250;
    private static final Dimension SCREEN_SIZE = new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT);

    //Panel parts
    private static final int TEXT_LIFESPAN = 18000; // Milliseconds

    private final Timer timer;

    private static final Font LOGS_FONT = new Font("Cascadia Code",Font.PLAIN,15);
    private static final String[] COLORS = {
            "rgb(255, 139, 0)",
            "rgb(255, 255, 102)",
            "rgb(126, 158, 210)",
            "rgb(52, 248, 255)",
            "rgb(0, 204, 255)"
    };

    private final Random random;
    Map<Hero,String> heroColor = new IdentityHashMap<>();

    public CombatLogF(){
        combatLogFrame = new JFrame("Combat Log");
        combatLogFrame.setPreferredSize(SCREEN_SIZE);
        combatLogFrame.setTitle("Battle log");
        combatLogFrame.setResizable(false);
        combatLogPanel = new JPanel();

        random = new Random();
        combatLogPanel.setLayout(new BoxLayout(combatLogPanel, BoxLayout.Y_AXIS));
        // Add JScrollPane to enable scrolling
        JScrollPane scrollPane = new JScrollPane(combatLogPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        combatLogPanel.setBackground(Color.DARK_GRAY);

        timer = new Timer();

        combatLogPanel.setVisible(true);

        combatLogFrame.add(combatLogPanel);
        combatLogFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        combatLogFrame.pack();
        combatLogFrame.setVisible(true);
    }

    public JPanel getConsoleLogPanel() {
        return combatLogPanel;
    }

    public void addString(Map<String,String> logs, Hero attacker, Hero defendant){

        if(heroColor.isEmpty()){
            String firstHeroColor = COLORS[random.nextInt(COLORS.length)];
            heroColor.put(attacker, firstHeroColor);
            String secondHeroColor;
            do{
                secondHeroColor = COLORS[random.nextInt(COLORS.length)];
            }while(secondHeroColor.equals(firstHeroColor));

            heroColor.put(defendant,secondHeroColor);
        }

        String attackerLog = logs.get("Attacker");
        String defendantLog = logs.get("Attacked");
        String damageReceivedLog = logs.get("DamageReceived");
        String transitionLog = logs.get("Transition");

        String log = """
             <html>
                <font color='%s'>%s</font> hits <font color='%s'>%s</font> for <font color='rgb(250, 4, 10)'>%s</font> <font color='rgb(30, 255, 0)'>%s</font>
             </html>
             """.formatted(heroColor.get(attacker), attackerLog,
                heroColor.get(defendant), defendantLog,
                damageReceivedLog, transitionLog);

        JLabel label = new JLabel(log);

        label.setFont(LOGS_FONT);
        label.setForeground(Color.WHITE);

        combatLogPanel.add(label);
        combatLogPanel.revalidate();

        // Schedule task to remove label after lifespan expires
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                combatLogPanel.remove(label);
                combatLogPanel.revalidate(); // Revalidate the panel after removing label
                combatLogPanel.repaint(); // Repaint the panel to reflect changes
            }
        }, TEXT_LIFESPAN);
    }
}
