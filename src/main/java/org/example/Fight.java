package org.example;


import org.example.HeroClass.Hero;
import org.example.SwingComponents.CombatLog.CombatLogF;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.*;

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
        for (int i = 0; i < times ; i++) {
            int res = fightHeroes(firstHero, secHero);
            switch (res){
                case -1 -> leftWon++;
                case 1 -> rightWon++;
                case 0 -> leftWon += 0;
            }
        }

        return new int[]{leftWon,rightWon};
    }


    private static int fightHeroes(Hero firstHero, Hero secondHero) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
        CompletableFuture<Integer> fightCompleted = new CompletableFuture<>();

        if(combatLogF == null) combatLogF = new CombatLogF();

        // Schedule HP and mana regeneration task
        ScheduledFuture<?> hpAndManaFuture = executor.scheduleAtFixedRate(() -> {
            firstHero.regenerateHpAndMana();
            secondHero.regenerateHpAndMana();
        }, 100, 100, TimeUnit.MILLISECONDS);

        // Schedule first hero's attack
        long firstHeroDelay = Math.round(firstHero.currentAttackSpeed * 1000);
        long firstHeroCooldown = Math.round(firstHero.currentAttackRate * 1000);

        ScheduledFuture<?> firstHeroAttackFuture = executor.scheduleAtFixedRate(() -> {
            Map<String,String> logs = firstHero.attackEnemyHero(secondHero);

            combatLogF.addString(logs,firstHero,secondHero);
        }, firstHeroDelay, firstHeroCooldown, TimeUnit.MILLISECONDS);

        // Schedule second hero's attack
        long secondHeroDelay = Math.round(secondHero.currentAttackSpeed * 1000);
        long secondHeroCooldown = Math.round(secondHero.currentAttackRate * 1000);

        ScheduledFuture<?> secondHeroAttackFuture = executor.scheduleAtFixedRate(() -> {
            Map<String,String> logs = secondHero.attackEnemyHero(firstHero);

            combatLogF.addString(logs,secondHero,firstHero);
        }, secondHeroDelay, secondHeroCooldown, TimeUnit.MILLISECONDS);

        // Check for fight completion
        executor.submit(() -> {
            while (firstHero.currentHp >= 0 && secondHero.currentHp >= 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // Determine the result based on the current HP of the heroes
            if (firstHero.currentHp <= 0) {
                fightCompleted.complete(1); // Second hero won
            } else {
                fightCompleted.complete(-1); // First hero won
            }

        });

        // Wait for the fight to finish
        int result = 0;
        try {
            result = fightCompleted.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Cancel all scheduled tasks
        hpAndManaFuture.cancel(false);
        firstHeroAttackFuture.cancel(false);
        secondHeroAttackFuture.cancel(false);

        // Shutdown the executor service
        executor.shutdown();

        firstHero.maxHpAndManaAccordingToCurrentLevel();
        secondHero.maxHpAndManaAccordingToCurrentLevel();

        return result;

    }

    public static CombatLogF getCombatLogF() {
        return combatLogF;
    }

    public static void setCombatLogF(CombatLogF combatLogF) {
        Fight.combatLogF = combatLogF;
    }
}
