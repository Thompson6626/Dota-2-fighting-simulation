package org.example;


import org.example.HeroClass.Hero;
import org.example.SwingComponents.CombatLog.CombatLogF;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.*;

import static org.example.SwingComponents.CombatLog.LogTypes.KILL;
import static org.example.SwingComponents.CombatLog.LogTypes.PHYSICAL_HIT;

public class Fight {

    private static CombatLogF combatLogF;
    public interface FightResultCallback {
        void onFightCompleted(int[] result);
    }

    public static void fightAsync(Hero firstHero, Hero secHero, int times, FightResultCallback callback) {

        SwingWorker<int[], Void> worker = new SwingWorker<>() {
            @Override
            protected int[] doInBackground() {
                return fight(firstHero, secHero, times);
            }

            @Override
            protected void done() {
                try {
                    int[] result = get();
                    callback.onFightCompleted(result);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    public static int[] fight(Hero firstHero,Hero secHero,int times){

        int leftWon = 0;
        int rightWon = 0;
        for (int i = 1; i <= times ; i++) {
            int res = fightHeroes(firstHero, secHero);
            switch (res){
                case -1 -> leftWon++;
                case 1 -> rightWon++;
            }
        }

        return new int[]{leftWon,rightWon};
    }

    private static final int INTERVAL_BETWEEN_REGENERATION = 100;
    private static final int WAIT_FOR_COMPLETION = 10;
    /**
     * @return -1 If the first hero won or 1 if the second hero won
     */
    private static int fightHeroes(Hero firstHero, Hero secondHero) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

        if (combatLogF == null) combatLogF = new CombatLogF();

        // Schedule HP and mana regeneration task
        ScheduledFuture<?> hpAndManaFuture = executor.scheduleAtFixedRate(() -> {
            firstHero.regenerateHpAndMana();
            secondHero.regenerateHpAndMana();
        }, INTERVAL_BETWEEN_REGENERATION, INTERVAL_BETWEEN_REGENERATION, TimeUnit.MILLISECONDS);

        ScheduledFuture<?> firstHeroAttack = scheduleHeroAttack(executor, firstHero, secondHero);
        ScheduledFuture<?> secondHeroAttack = scheduleHeroAttack(executor, secondHero, firstHero);

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
                combatLogF.addLog(firstHero,secondHero,KILL);
            }else if(result > 0){
                combatLogF.addLog(secondHero,firstHero,KILL);
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

        return result;

    }
    private static ScheduledFuture<?> scheduleHeroAttack(ScheduledExecutorService executor, Hero attacker, Hero target) {
        long attackCooldown = Math.round(attacker.currentAttackRate);
        return executor.scheduleAtFixedRate(() -> {
            Map<String, String> logs = attacker.attackEnemyHero(target);
            combatLogF.addLog(logs, attacker, target, PHYSICAL_HIT);
        }, attackCooldown, attackCooldown, TimeUnit.SECONDS);
    }

    public static CombatLogF getCombatLogF() {
        return combatLogF;
    }

    public static void setCombatLogF(CombatLogF combatLogF) {
        Fight.combatLogF = combatLogF;
    }
}
