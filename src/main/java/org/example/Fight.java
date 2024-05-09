package org.example;


import org.example.DataFetch.Utils;
import org.example.HeroClass.Hero;
import org.example.SwingComponents.CombatLog.CombatLogF;
import org.example.SwingComponents.CombatLog.LogTypes;
import org.example.SwingComponents.MainPanel;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.*;


public class Fight {

    private static CombatLogF combatLogF;
    private static boolean fightIsRunning;

    public static void fightAsync(Hero firstHero, Hero secHero, int times) {

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                fight(firstHero, secHero, times);
                return null;
            }

        };
        worker.execute();
    }
    public static int[] fight(Hero firstHero,Hero secHero,int times){
        fightIsRunning = true;
        firstHero.applyDebbufsToEnemy(secHero,firstHero.debuffsToApply);
        secHero.applyDebbufsToEnemy(firstHero,secHero.debuffsToApply);

        if (combatLogF == null) combatLogF = new CombatLogF();

        int leftWon = 0;
        int rightWon = 0;

        for (int i = 1; i <= times ; i++) {
            int res = fightHeroes(firstHero, secHero);
            switch (res){
                case -1 -> leftWon++;
                case 1 -> rightWon++;
            }
        }
        fightIsRunning = false;

        MainPanel.getAllInteractable().forEach(jComponent -> jComponent.setEnabled(true));

        return new int[]{leftWon,rightWon};
    }

    private static final int INTERVAL_BETWEEN_REGENERATION = 100;
    private static final int WAIT_FOR_COMPLETION = 10;
    /**
     * @return -1 If the first hero won or 1 if the second hero won
     */
    private static int fightHeroes(Hero firstHero, Hero secondHero) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

        // Schedule HP and mana regeneration task

        ScheduledFuture<?> hpAndManaFuture = executor.scheduleAtFixedRate(() -> {
            firstHero.regenerateHpAndMana();
            secondHero.regenerateHpAndMana();
        }, INTERVAL_BETWEEN_REGENERATION, INTERVAL_BETWEEN_REGENERATION, TimeUnit.MILLISECONDS);

        CountDownLatch startLatch = new CountDownLatch(2); // Create a latch with count 2

        ScheduledFuture<?> firstHeroAttack = scheduleHeroAttack(executor,firstHero,secondHero,startLatch);
        ScheduledFuture<?> secondHeroAttack = scheduleHeroAttack(executor,secondHero,firstHero,startLatch);
        startLatch.countDown();
        startLatch.countDown();


        CompletableFuture<Integer> fightCompleted = CompletableFuture.supplyAsync(() -> {
            while (firstHero.currentHp > 0 && secondHero.currentHp > 0) {
                try {
                    Thread.sleep(WAIT_FOR_COMPLETION);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            int result = Integer.compare((int) secondHero.currentHp, (int) firstHero.currentHp);
            if(result < 0){
                combatLogF.addLog(firstHero,secondHero, LogTypes.KILL);
            }else if(result > 0){
                combatLogF.addLog(secondHero,firstHero,LogTypes.KILL);
            }
            return result;
        });
        int result = 0;
        try {
            result = fightCompleted.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }finally {
            hpAndManaFuture.cancel(false);
            firstHeroAttack.cancel(false);
            secondHeroAttack.cancel(false);
            executor.shutdown();
        }


        firstHero.maxHpAndManaAccordingToCurrentLevel();
        secondHero.maxHpAndManaAccordingToCurrentLevel();

        firstHero.isDead = false;
        secondHero.isDead = false;
        updateHeroWinsLabels(result);
        return result;
    }

    private static void updateHeroWinsLabels(int result) {
        JLabel[] heroWins = MainPanel.getHeroWins();
        if (heroWins[0] != null && heroWins[1] != null){
            int heroIndx = result < 0 ? 0 : 1;
            heroWins[heroIndx].setText(
                    String.valueOf(
                            Integer.parseInt(heroWins[heroIndx].getText()) + 1
                    )
            );
        }
    }

    private static ScheduledFuture<?> scheduleHeroAttack(
            ScheduledExecutorService executor,
            Hero attacker,
            Hero target,
            CountDownLatch latch
    ) {
        long attackCooldown = Math.round(attacker.currentAttackRate * 1000);
        return executor.scheduleAtFixedRate(() -> {
            try {
                latch.await();
                Map<String, String> logs = attacker.attackEnemyHero(target);
                combatLogF.addLog(logs, attacker, target, LogTypes.PHYSICAL_HIT);
            }catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, attackCooldown, attackCooldown, TimeUnit.MILLISECONDS);
    }

    public static void clearCombatLog(){
        if (combatLogF != null){
            combatLogF.clear();
        }
    }
    public static boolean getCombatLogVisibility(){
        return combatLogF.getCombatLogVisibility();
    }
    public static void makeCombatLogVisible() {
        combatLogF.makeCombatLogVisible();
    }
    public static void centerCombatLog(){
        if (combatLogF != null){
            combatLogF.centerFrame();
        }
    }
    public static boolean hasOngoingFight() {
        return fightIsRunning;
    }
}
