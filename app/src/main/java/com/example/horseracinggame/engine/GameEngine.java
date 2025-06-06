package com.example.horseracinggame.engine;

import android.graphics.Color;
import com.example.horseracinggame.model.Racer;
import java.util.*;

public class GameEngine {
    private List<Racer> racers;
    private Map<String, Integer> bets; // racer name -> bet amount
    private boolean raceInProgress;
    private boolean raceFinished;
    private long raceStartTime;
    private static final int RACE_DURATION_MS = 15000; // 15 seconds

    public GameEngine() {
        initializeRacers();
        bets = new HashMap<>();
        raceInProgress = false;
        raceFinished = false;
    }

    private void initializeRacers() {
        racers = new ArrayList<>();
        // Adjust base speeds to be more balanced (lower number = faster)
        racers.add(new Racer("Thunder Bolt", generateRandomOdds(), 90, "Moderate", Color.RED));
        racers.add(new Racer("Speed Demon", generateRandomOdds(), 85, "High", Color.parseColor("#008080"))); // Teal
        racers.add(new Racer("Dark Horse", generateRandomOdds(), 95, "Low", Color.GREEN));

        // Sort by odds to determine risk categories
        updateRiskCategories();
    }

    private double generateRandomOdds() {
        // Generate odds between 1.1x and 3.5x
        return Math.round((1.1 + Math.random() * 2.4) * 10.0) / 10.0;
    }

    private void updateRiskCategories() {
        // Sort racers by odds (lower odds = favorite = safer bet)
        List<Racer> sortedByOdds = new ArrayList<>(racers);
        sortedByOdds.sort((r1, r2) -> Double.compare(r1.getOdds(), r2.getOdds()));

        sortedByOdds.get(0).setRiskCategory("Low Risk"); // Favorite
        sortedByOdds.get(1).setRiskCategory("Moderate Risk");
        sortedByOdds.get(2).setRiskCategory("High Risk"); // Underdog
    }

    // Generate new odds for next race
    public void generateNewOdds() {
        for (Racer racer : racers) {
            racer.setOdds(generateRandomOdds());
            racer.resetForRace();
        }
        updateRiskCategories();
        bets.clear();
        raceInProgress = false;
        raceFinished = false;
    }

    // Place a bet on a racer
    public boolean placeBet(String racerName, int amount) {
        if (raceInProgress || amount <= 0) {
            return false;
        }

        bets.put(racerName, bets.getOrDefault(racerName, 0) + amount);

        // Update racer's total bet amount for crowd penalty calculation
        for (Racer racer : racers) {
            if (racer.getName().equals(racerName)) {
                racer.addBetAmount(amount);
                break;
            }
        }

        return true;
    }

    // Get total bet amount for a racer
    public int getBetAmount(String racerName) {
        return bets.getOrDefault(racerName, 0);
    }

    // Get total bet amount across all racers
    public int getTotalBetAmount() {
        return bets.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Calculate potential winnings for current bets
    public Map<String, Integer> calculatePotentialWinnings() {
        Map<String, Integer> winnings = new HashMap<>();

        for (Map.Entry<String, Integer> bet : bets.entrySet()) {
            String racerName = bet.getKey();
            int betAmount = bet.getValue();

            Racer racer = getRacerByName(racerName);
            if (racer != null) {
                int potentialWinning = (int)(betAmount * racer.getOdds());
                winnings.put(racerName, potentialWinning);
            }
        }

        return winnings;
    }

    // Start the race
    public void startRace() {
        if (raceInProgress || bets.isEmpty()) {
            return;
        }

        raceInProgress = true;
        raceFinished = false;
        raceStartTime = System.currentTimeMillis();

        // Reset all racers
        for (Racer racer : racers) {
            racer.resetForRace();
        }

        // Apply crowd betting penalty to most bet-on racer
        applyBettingPenalties();
    }

    private void applyBettingPenalties() {
        Racer mostBetRacer = null;
        int maxBets = 0;

        // Find the racer with the most bets
        for (Racer racer : racers) {
            if (racer.getTotalBetAmount() > maxBets) {
                maxBets = racer.getTotalBetAmount();
                mostBetRacer = racer;
            }
        }

        // Mark the most bet-on racer for penalty (if there are bets)
        if (mostBetRacer != null && maxBets > 0) {
            // Set a flag or modify the racer to indicate it should get penalty
            // We'll use a different approach - store the penalty info in the racer
            for (Racer racer : racers) {
                racer.setHasCrowdPenalty(racer == mostBetRacer);
            }
        }
    }

    // Update race progress (call this repeatedly during race)
    public void updateRace() {
        if (!raceInProgress || raceFinished) {
            return;
        }

        long elapsedTime = System.currentTimeMillis() - raceStartTime;

        // Update each racer
        for (Racer racer : racers) {
            if (!racer.isFinished()) {
                // Check for checkpoint and update speed
                if (racer.isAtCheckpoint()) {
                    racer.updateSpeedAtCheckpoint();
                }

                racer.moveForward();
            }
        }

        // Check if any racer finished (reached 100%)
        boolean anyFinished = racers.stream().anyMatch(Racer::isFinished);
        if (anyFinished) {
            finishRace();
            return;
        }

        // Safety timeout - only if race takes too long (prevent infinite races)
        if (elapsedTime >= 30000) { // 30 seconds maximum safety limit
            // Force finish all racers at their current positions
            forceFinishRace();
        }
    }

    private void finishRace() {
        raceInProgress = false;
        raceFinished = true;

        // Set finish times for racers who completed the race
        long currentTime = System.currentTimeMillis();
        for (Racer racer : racers) {
            if (racer.isFinished() && racer.getFinishTime() == 0) {
                racer.setFinishTime(currentTime);
            }
        }
    }

    private void forceFinishRace() {
        raceInProgress = false;
        raceFinished = true;

        // Force all unfinished racers to finish at their current progress
        long currentTime = System.currentTimeMillis();
        for (Racer racer : racers) {
            if (!racer.isFinished()) {
                racer.setFinished(true);
                racer.setFinishTime(currentTime);
            }
        }
    }

    // Get race results sorted by finish position
    public List<Racer> getRaceResults() {
        List<Racer> results = new ArrayList<>(racers);
        results.sort((r1, r2) -> {
            // First sort by progress (descending)
            int progressCompare = Double.compare(r2.getProgress(), r1.getProgress());
            if (progressCompare != 0) {
                return progressCompare;
            }
            // Then by finish time (ascending - earlier finish time wins)
            return Long.compare(r1.getFinishTime(), r2.getFinishTime());
        });
        return results;
    }

    // Calculate actual winnings based on race results
    public Map<String, Integer> calculateWinnings() {
        Map<String, Integer> winnings = new HashMap<>();

        if (!raceFinished) {
            return winnings;
        }

        List<Racer> results = getRaceResults();
        Racer winner = results.get(0);

        for (Map.Entry<String, Integer> bet : bets.entrySet()) {
            String racerName = bet.getKey();
            int betAmount = bet.getValue();

            if (racerName.equals(winner.getName())) {
                // Winner gets payout based on odds
                int winAmount = (int)(betAmount * winner.getOdds());
                winnings.put(racerName, winAmount);
            } else {
                // Loser gets 0
                winnings.put(racerName, 0);
            }
        }

        return winnings;
    }

    // Get total winnings amount
    public int getTotalWinnings() {
        return calculateWinnings().values().stream().mapToInt(Integer::intValue).sum();
    }

    // Get net profit/loss
    public int getNetResult() {
        return getTotalWinnings() - getTotalBetAmount();
    }

    // Get strategic recommendation
    public String getStrategicRecommendation() {
        List<Racer> sortedByOdds = new ArrayList<>(racers);
        sortedByOdds.sort((r1, r2) -> Double.compare(r1.getOdds(), r2.getOdds()));

        Racer favorite = sortedByOdds.get(0);
        Racer underdog = sortedByOdds.get(2);

        return String.format("💡 Strategy Tips:\n" +
                        "🏆 Safe Play: Bet on %s (%.1fx odds)\n" +
                        "⚖️ Balanced: Spread bets across multiple racers\n" +
                        "🎰 High Risk: Bet on %s (%.1fx odds) for big payout!",
                favorite.getName(), favorite.getOdds(),
                underdog.getName(), underdog.getOdds());
    }

    // Helper methods
    public List<Racer> getRacers() { return new ArrayList<>(racers); }

    public Racer getRacerByName(String name) {
        return racers.stream()
                .filter(r -> r.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public boolean isRaceInProgress() { return raceInProgress; }
    public boolean isRaceFinished() { return raceFinished; }

    public Map<String, Integer> getCurrentBets() { return new HashMap<>(bets); }

    public boolean hasBets() { return !bets.isEmpty(); }
}