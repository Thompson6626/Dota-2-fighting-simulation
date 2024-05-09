package org.example.SwingComponents.CombatLog;

import org.example.DataFetch.Utils;
import org.example.HeroClass.Hero;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.*;


public class CombatLogF {


    private JFrame combatLogFrame;
    private JPanel combatLogPanel;
    private static int innerJLabels;
    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 250;
    private static final Dimension COMBAT_LOG_FRAME_SIZE = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);
    private static final int MIDDLE_WIDTH;
    private static final int MIDDLE_HEIGHT;
    static {
        Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
        MIDDLE_WIDTH = (int) ((SCREEN_SIZE.getWidth() - FRAME_WIDTH) / 2.0);
        MIDDLE_HEIGHT = (int) ((SCREEN_SIZE.getHeight() - FRAME_HEIGHT) / 2.0);
    }
    // Panel parts
    private static final int TEXT_LIFESPAN = 18000; // Milliseconds

    private static Timer timer;

    private static final Font LOGS_FONT = new Font("Cascadia Code", Font.PLAIN, 15);
    private static final String[] COLORS = {
            "rgb(255, 139, 0)",
            "rgb(255, 255, 102)",
            "rgb(126, 158, 210)",
            "rgb(52, 248, 255)",
            "rgb(0, 204, 255)"
    };
    private static final Color BACKGROUND_COLOR = new Color(40, 42, 44);
    private static final Map<Hero, String> HERO_COLOR = new IdentityHashMap<>();


    public CombatLogF() {
        combatLogFrame = new JFrame("Combat Log");
        combatLogFrame.setVisible(true);
        combatLogFrame.setPreferredSize(COMBAT_LOG_FRAME_SIZE);
        combatLogFrame.setTitle("Battle log");
        combatLogFrame.setResizable(false);

        combatLogPanel = new JPanel();
        combatLogPanel.setLayout(new BoxLayout(combatLogPanel, BoxLayout.Y_AXIS));

        combatLogPanel.setBackground(BACKGROUND_COLOR);
        combatLogPanel.setVisible(true);

        timer = new Timer();

        // Wrap the combatLogPanel with the JScrollPane before adding it to the frame
        JScrollPane scrollPane = new JScrollPane(combatLogPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        scrollPane.getVerticalScrollBar().setBlockIncrement(40);
        combatLogFrame.add(scrollPane);

        combatLogFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

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

        if(!HERO_COLOR.containsKey(attacker) || !HERO_COLOR.containsKey(defendant)){
            String firstHeroColor = Utils.randomChoice(COLORS);
            HERO_COLOR.put(attacker, firstHeroColor);
            String secondHeroColor;
            do{
                secondHeroColor =  Utils.randomChoice(COLORS);
            } while(secondHeroColor.equals(firstHeroColor));

            HERO_COLOR.put(defendant,secondHeroColor);
        }


        String log = switch (type){
            case PHYSICAL_HIT -> generateAttackLog(logs, attacker, defendant);
            case KILL -> generateKillLog(attacker, defendant);
            default -> "Unexpected value";
        };
        JLabel label = new JLabel(log);

        addLabelToPanel(label,combatLogPanel);
        if(type == LogTypes.KILL)
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
                if (combatLogFrame != null && innerJLabels == 0){
                    hideCombatLog();
                }
            }
        }, TEXT_LIFESPAN);
        innerJLabels++;
    }

    private String generateAttackLog(Map<String,String> logs, Hero attacker, Hero defendant){
        String damageReceivedLog = logs.get("DamageReceived");
        String transitionLog = logs.get("Transition");

        return LogMessages.ATTACK_LOG.getMessage().formatted(
                HERO_COLOR.get(attacker),
                attacker.heroName,
                HERO_COLOR.get(defendant),
                defendant.heroName,
                damageReceivedLog, transitionLog
        );
    }

    private String generateKillLog(Hero attacker, Hero defendant){
        return LogMessages.KILL_LOG.getMessage().formatted(
                HERO_COLOR.get(defendant),
                defendant.heroName,
                HERO_COLOR.get(attacker),
                attacker.heroName
        );
    }

    private void addEmptySpace(JPanel panel){
        addLabelToPanel(new JLabel("\n"),panel);
        addLabelToPanel(new JLabel("--------------------------\n"),panel);
        addLabelToPanel(new JLabel("\n"),panel);
    }
    public boolean getCombatLogVisibility() {
        // Close the frame and panel
        return combatLogFrame.isVisible();
    }
    public void makeCombatLogVisible() {
        // Close the frame and panel
        combatLogFrame.setVisible(true);
    }
    public void hideCombatLog() {
        // Close the frame and panel
        combatLogFrame.setVisible(false);
    }


    public void clear() {
        timer.cancel();
        timer = new Timer();
        Component[] components = combatLogPanel.getComponents();
        for (Component component:components){
            if(component instanceof JLabel){
                combatLogPanel.remove(component);
            }
        }

        innerJLabels = 1;
    }

    public void centerFrame() {
        combatLogFrame.toFront();
        combatLogFrame.setLocationRelativeTo(null);
        combatLogFrame.setLocation(MIDDLE_WIDTH, MIDDLE_HEIGHT);
    }
}
