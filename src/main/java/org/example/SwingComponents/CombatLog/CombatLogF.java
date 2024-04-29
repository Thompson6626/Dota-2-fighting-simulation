package org.example.SwingComponents.CombatLog;

import org.example.HeroClass.Hero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.*;
import java.util.Timer;
import java.awt.event.WindowEvent;

import static org.example.SwingComponents.CombatLog.LogMessages.ATTACK_LOG;
import static org.example.SwingComponents.CombatLog.LogMessages.KILL_LOG;
import static org.example.SwingComponents.CombatLog.LogTypes.KILL;

public class CombatLogF {


    private JFrame combatLogFrame;
    private JPanel combatLogPanel;
    private static int innerJLabels;
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
    private static final Color BACKGROUND_COLOR = new Color(40, 42, 44);

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

        combatLogPanel.setBackground(BACKGROUND_COLOR);

        timer = new Timer();

        // Wrap the combatLogPanel with the JScrollPane before adding it to the frame
        JScrollPane scrollPane = new JScrollPane(combatLogPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        scrollPane.getVerticalScrollBar().setBlockIncrement(40);
        combatLogFrame.add(scrollPane);

        combatLogPanel.setVisible(true);

        combatLogFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
    public void addLog(Hero attacker, Hero defendant, LogTypes type){
        addLog(Collections.emptyMap(),attacker,defendant,type);
    }
    public void addLog(
            Map<String,String> logs,
            Hero attacker,
            Hero defendant,
            LogTypes type
    ){
        if (logs.isEmpty() && attacker.isDead) return;

        if(heroColor.isEmpty()){
            String firstHeroColor = COLORS[random.nextInt(COLORS.length)];
            heroColor.put(attacker, firstHeroColor);
            String secondHeroColor;
            do{
                secondHeroColor = COLORS[random.nextInt(COLORS.length)];
            }while(secondHeroColor.equals(firstHeroColor));

            heroColor.put(defendant,secondHeroColor);
        }


        String log = switch (type){
            case PHYSICAL_HIT -> generateAttackLog(logs, attacker, defendant);
            case KILL -> generateKillLog(attacker, defendant);
            default -> "Unexpected value";
        };
        System.out.println(log);
        JLabel label = new JLabel(log);

        addLabelToPanel(label,combatLogPanel);
        if(type == KILL)
            addEmptySpace(combatLogPanel);

    }
    private void addLabelToPanel(JLabel log,JPanel panel){
        log.setFont(LOGS_FONT);
        log.setForeground(Color.WHITE);

        panel.add(log);
        panel.revalidate();

        // Schedule task to remove label after lifespan expires
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                panel.remove(log);
                panel.revalidate(); // Revalidate the panel after removing label
                panel.repaint(); // Repaint the panel to reflect changes
                innerJLabels--;
                if (innerJLabels == 0)
                    closeFrameAndPanel();

            }
        }, TEXT_LIFESPAN);
        innerJLabels++;
    }

    private String generateAttackLog(Map<String,String> logs, Hero attacker, Hero defendant){
        String damageReceivedLog = logs.get("DamageReceived");
        String transitionLog = logs.get("Transition");

        return ATTACK_LOG.getMessage().formatted(
                heroColor.get(attacker),
                attacker.heroName,
                heroColor.get(defendant),
                defendant.heroName,
                damageReceivedLog, transitionLog
        );
    }

    private String generateKillLog(Hero attacker, Hero defendant){
        return KILL_LOG.getMessage().formatted(
                heroColor.get(defendant),
                defendant.heroName,
                heroColor.get(attacker),
                attacker.heroName
        );
    }

    private void addEmptySpace(JPanel panel){
        for (int i = 0; i < 7; i++)
            addLabelToPanel(new JLabel(""),panel);
    }


    public void closeFrameAndPanel() {
        // Close the frame and panel
        combatLogFrame.dispose();
    }


}
