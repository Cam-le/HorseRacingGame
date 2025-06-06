package com.example.horseracinggame.model;

import java.io.Serializable;

public class Racer implements Serializable {
    private String name;
    private double odds;
    private int baseSpeed; // milliseconds for base movement
    private int currentSpeed;
    private double progress; // 0.0 to 100.0
    private String riskCategory;
    private int color;
    private boolean isFinished;
    private long finishTime;
    private int totalBetAmount;
    private boolean hasCrowdPenalty; // Flag for crowd betting penalty

    public Racer(String name, double odds, int baseSpeed, String riskCategory, int color) {
        this.name = name;
        this.odds = odds;
        this.baseSpeed = baseSpeed;
        this.currentSpeed = baseSpeed;
        this.riskCategory = riskCategory;
        this.color = color;
        this.progress = 0.0;
        this.isFinished = false;
        this.totalBetAmount = 0;
        this.hasCrowdPenalty = false;
    }

    // Reset racer for new race
    public void resetForRace() {
        this.progress = 0.0;
        this.currentSpeed = baseSpeed;
        this.isFinished = false;
        this.finishTime = 0;
        this.totalBetAmount = 0;
        this.hasCrowdPenalty = false;
    }

    // Update speed at checkpoints (20%, 40%, 60%, 80%)
    public void updateSpeedAtCheckpoint() {
        int minModifier, maxModifier;

        // Apply crowd betting penalty if this racer has the most bets
        if (hasCrowdPenalty) {
            minModifier = -60;
            maxModifier = 40;
        } else {
            minModifier = -50;
            maxModifier = 50;
        }

        int speedModifier = minModifier + (int)(Math.random() * (maxModifier - minModifier + 1));
        this.currentSpeed = Math.max(baseSpeed + speedModifier, 10); // Minimum speed of 10ms
    }

    // Check if this racer has reached a checkpoint
    public boolean isAtCheckpoint() {
        double currentProgress = progress;
        return (currentProgress >= 20.0 && currentProgress < 21.0) ||
                (currentProgress >= 40.0 && currentProgress < 41.0) ||
                (currentProgress >= 60.0 && currentProgress < 61.0) ||
                (currentProgress >= 80.0 && currentProgress < 81.0);
    }

    // Move racer forward
    public void moveForward() {
        if (!isFinished) {
            // Progress increment based on current speed
            // Lower speed value = faster movement (inverted for realism)
            double baseIncrement = 1.0; // Base progress per update
            double speedMultiplier = 100.0 / currentSpeed; // Convert speed to multiplier
            double increment = baseIncrement * speedMultiplier;

            // Add some randomness to make races more exciting
            double randomFactor = 0.8 + (Math.random() * 0.4); // 0.8 to 1.2 multiplier
            increment *= randomFactor;

            progress += increment;

            if (progress >= 100.0) {
                progress = 100.0;
                isFinished = true;
                finishTime = System.currentTimeMillis();
            }
        }
    }

    // Helper method to determine if this racer has the most bets (set externally)
    public boolean hasCrowdPenalty() {
        return hasCrowdPenalty;
    }

    public void setHasCrowdPenalty(boolean hasCrowdPenalty) {
        this.hasCrowdPenalty = hasCrowdPenalty;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getOdds() { return odds; }
    public void setOdds(double odds) { this.odds = odds; }

    public int getBaseSpeed() { return baseSpeed; }
    public void setBaseSpeed(int baseSpeed) { this.baseSpeed = baseSpeed; }

    public int getCurrentSpeed() { return currentSpeed; }
    public void setCurrentSpeed(int currentSpeed) { this.currentSpeed = currentSpeed; }

    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }

    public String getRiskCategory() { return riskCategory; }
    public void setRiskCategory(String riskCategory) { this.riskCategory = riskCategory; }

    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }

    public boolean isFinished() { return isFinished; }
    public void setFinished(boolean finished) { isFinished = finished; }

    public long getFinishTime() { return finishTime; }
    public void setFinishTime(long finishTime) { this.finishTime = finishTime; }

    public int getTotalBetAmount() { return totalBetAmount; }
    public void setTotalBetAmount(int totalBetAmount) { this.totalBetAmount = totalBetAmount; }

    public void addBetAmount(int amount) { this.totalBetAmount += amount; }

    @Override
    public String toString() {
        return String.format("%s (%.1fx) - %s Risk", name, odds, riskCategory);
    }
}