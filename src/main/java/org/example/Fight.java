package org.example;


import org.example.HeroClass.Hero;

import java.util.concurrent.*;

public class Fight {


    public static int fightHeroes(Hero firstHero, Hero secondHero) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
        CompletableFuture<Void> fightCompleted = new CompletableFuture<>();


        // Schedule HP and mana regeneration task
        ScheduledFuture<?> hpAndManaFuture = executor.scheduleAtFixedRate(() -> {
            firstHero.applyHpAndManaRegen();
            secondHero.applyHpAndManaRegen();
        }, 0, 1000, TimeUnit.MILLISECONDS);

        // Schedule first hero's attack
        long firstHeroDelay = Math.round(firstHero.currentAttackSpeed * 1000);
        long firstHeroCooldown = Math.round(firstHero.currentAttackRate * 1000);

        ScheduledFuture<?> firstHeroAttackFuture = executor.scheduleAtFixedRate(() -> {
            firstHero.attackEnemyHero(secondHero);
        }, firstHeroDelay, firstHeroCooldown, TimeUnit.MILLISECONDS);

        // Schedule second hero's attack
        long secondHeroDelay = Math.round(secondHero.currentAttackSpeed * 1000);
        long secondHeroCooldown = Math.round(secondHero.currentAttackRate * 1000);

        ScheduledFuture<?> secondHeroAttackFuture = executor.scheduleAtFixedRate(() -> {
            secondHero.attackEnemyHero(firstHero);
        }, secondHeroDelay, secondHeroCooldown, TimeUnit.MILLISECONDS);

        // Check for fight completion
        executor.submit(() -> {
            while (firstHero.currentHp > 0 && secondHero.currentHp > 0) {
                try {
                    Thread.sleep(10); // Add a short delay to reduce CPU load
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            fightCompleted.complete(null);
        });

        // Wait for the fight to finish
        try {
            fightCompleted.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Cancel all scheduled tasks
        hpAndManaFuture.cancel(false);
        firstHeroAttackFuture.cancel(false);
        secondHeroAttackFuture.cancel(false);

        // Shutdown the executor service
        executor.shutdown();

        // Determine the winner
        if (firstHero.currentHp <= 0) {
            return 1; // Second hero won
        }
        if(secondHero.currentHp <= 0){
            return -1; // First hero won
        }

        return 0;
    }









}
