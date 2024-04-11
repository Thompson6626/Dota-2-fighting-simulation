package org.example.SwingComponents.CombatLog;

import org.example.HeroClass.Hero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.*;
import java.util.Timer;
import java.awt.event.WindowEvent;

public class CombatLogF {


    private JFrame combatLogFrame;
    private JPanel combatLogPanel;

    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 250;
    private static final Dimension SCREEN_SIZE = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);

    // Panel parts
    private static final int TEXT_LIFESPAN = 18000; // Milliseconds

    private final Timer timer;

    private static final Font LOGS_FONT = new Font("Cascadia Code", Font.PLAIN, 15);
    private static final String[] COLORS = {
            "rgb(255, 139, 0)",
            "rgb(255, 255, 102)",
            "rgb(126, 158, 210)",
            "rgb(52, 248, 255)",
            "rgb(0, 204, 255)"
    };

    private final Random random;
    private final Map<Hero, String> heroColor;

    public CombatLogF() {
        heroColor = new IdentityHashMap<>();
        random = new Random();
        combatLogFrame = new JFrame("Combat Log");
        combatLogFrame.setPreferredSize(SCREEN_SIZE);
        combatLogFrame.setTitle("Battle log");
        combatLogFrame.setResizable(false);

        combatLogPanel = new JPanel();
        combatLogPanel.setLayout(new BoxLayout(combatLogPanel, BoxLayout.Y_AXIS));

        combatLogPanel.setBackground(new Color(40, 42, 44));

        timer = new Timer();

        // Wrap the combatLogPanel with the JScrollPane before adding it to the frame
        JScrollPane scrollPane = new JScrollPane(combatLogPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        scrollPane.getVerticalScrollBar().setBlockIncrement(40);
        combatLogFrame.add(scrollPane);

        combatLogPanel.setVisible(true);

        combatLogFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        combatLogFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                combatLogFrame.dispose();
                combatLogFrame = null;
            }
        });

        combatLogFrame.pack();
        combatLogFrame.setVisible(true);
    }

    public void addLog(Map<String,String> logs, Hero attacker, Hero defendant,String status){

        if(heroColor.isEmpty()){
            String firstHeroColor = COLORS[random.nextInt(COLORS.length)];
            heroColor.put(attacker, firstHeroColor);
            String secondHeroColor;
            do{
                secondHeroColor = COLORS[random.nextInt(COLORS.length)];
            }while(secondHeroColor.equals(firstHeroColor));

            heroColor.put(defendant,secondHeroColor);
        }



        String log = switch (status){
            case "Attack" -> generateAttackLog(logs, attacker, defendant);
            case "Kill" -> generateKillLog(attacker, defendant);
            default -> "Unexpected value";
        };


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

    private String generateAttackLog(Map<String,String> logs, Hero attacker, Hero defendant){
        String damageReceivedLog = logs.get("DamageReceived");
        String transitionLog = logs.get("Transition");

        return """
             <html>
                <font color='%s'>%s</font>\s
                hits\s
                <font color='%s'>%s</font>\s
                for\s
                <font color='rgb(250, 4, 10)'>%s</font>\s
                <font color='rgb(30, 255, 0)'>%s</font>
             </html>
             """.formatted(heroColor.get(attacker), attacker.heroName,
                heroColor.get(defendant), defendant.heroName,
                damageReceivedLog, transitionLog);
    }

    private String generateKillLog(Hero attacker, Hero defendant){

        return """
             <html>
                <font color='%s'>%s</font>\s
                is killed by\s
                <font color='%s'>%s</font>!
             </html>
             """.formatted(heroColor.get(defendant), defendant.heroName,
                heroColor.get(attacker), attacker.heroName);
    }




    public void closeFrameAndPanel() {
        // Close the frame and panel
        combatLogFrame.dispose();
    }
}
