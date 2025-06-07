package com.example.horseracinggame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.horseracinggame.R;
import com.example.horseracinggame.engine.GameEngine;
import com.example.horseracinggame.manager.SessionManager;
import com.example.horseracinggame.manager.SoundManager;
import com.example.horseracinggame.model.Racer;
import com.example.horseracinggame.view.AnimatedHorseSeekBar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RACE_UPDATE_INTERVAL = 100; // 10 FPS
    private static final int REQUEST_CODE_BETTING = 1001;

    // UI Components
    private TextView tvUserInfo, tvCurrentPoints, tvHighScore;
    private TextView tvRacer1Name, tvRacer1Odds, tvRacer2Name, tvRacer2Odds, tvRacer3Name, tvRacer3Odds;
    private AnimatedHorseSeekBar seekBarRacer1, seekBarRacer2, seekBarRacer3;
    private TextView tvRaceStatus, tvQuickStats;
    private Button btnPlaceBets, btnStartRace, btnNewRace, btnLogout;

    // Game Components
    private SessionManager sessionManager;
    private static GameEngine gameEngine; // Static to maintain state across activities
    private SoundManager soundManager;
    private Handler raceHandler;
    private Runnable raceUpdateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        initializeViews();
        setupClickListeners();
        setupBackPressHandler();
        updateUI();

        // Start continuous background music
        soundManager.startMainActivityBackgroundMusic();
    }

    private void setupBackPressHandler() {
        // Handle back press with modern OnBackPressedCallback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Show logout confirmation dialog
                showLogoutConfirmation();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void initializeComponents() {
        sessionManager = new SessionManager(this);

        // Initialize GameEngine only once (static)
        if (gameEngine == null) {
            gameEngine = new GameEngine();
        }

        soundManager = new SoundManager(this);
        raceHandler = new Handler();

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }
    }

    // Static method to get the game engine from other activities
    public static GameEngine getGameEngine() {
        if (gameEngine == null) {
            gameEngine = new GameEngine();
        }
        return gameEngine;
    }

    private void initializeViews() {
        // Header views
        tvUserInfo = findViewById(R.id.tvUserInfo);
        tvCurrentPoints = findViewById(R.id.tvCurrentPoints);
        tvHighScore = findViewById(R.id.tvHighScore);
        btnLogout = findViewById(R.id.btnLogout);

        // Racer info views
        tvRacer1Name = findViewById(R.id.tvRacer1Name);
        tvRacer1Odds = findViewById(R.id.tvRacer1Odds);
        tvRacer2Name = findViewById(R.id.tvRacer2Name);
        tvRacer2Odds = findViewById(R.id.tvRacer2Odds);
        tvRacer3Name = findViewById(R.id.tvRacer3Name);
        tvRacer3Odds = findViewById(R.id.tvRacer3Odds);

        // Progress bars
        seekBarRacer1 = findViewById(R.id.seekBarRacer1);
        seekBarRacer2 = findViewById(R.id.seekBarRacer2);
        seekBarRacer3 = findViewById(R.id.seekBarRacer3);

        // Set horse colors for each SeekBar
        seekBarRacer1.setHorseColor(0); // Red
        seekBarRacer2.setHorseColor(1); // Teal
        seekBarRacer3.setHorseColor(2); // Green

        // Status and action views
        tvRaceStatus = findViewById(R.id.tvRaceStatus);
        tvQuickStats = findViewById(R.id.tvQuickStats);
        btnPlaceBets = findViewById(R.id.btnPlaceBets);
        btnStartRace = findViewById(R.id.btnStartRace);
        btnNewRace = findViewById(R.id.btnNewRace);
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> logout());
        btnPlaceBets.setOnClickListener(v -> openBettingActivity());
        btnStartRace.setOnClickListener(v -> startRace());
        btnNewRace.setOnClickListener(v -> setupNewRace());
    }

    private void updateUI() {
        updateUserInfo();
        updateRacerInfo();
        updateRaceStatus();
        updateButtonStates();
    }

    private void updateUserInfo() {
        tvUserInfo.setText(sessionManager.getUserDisplayInfo());
        tvCurrentPoints.setText(String.format("%,d", sessionManager.getUserPoints()));
        tvHighScore.setText(String.format("%,d", sessionManager.getHighScore()));
    }

    private void updateRacerInfo() {
        List<Racer> racers = gameEngine.getRacers();

        if (racers.size() >= 3) {
            // Update racer 1
            Racer racer1 = racers.get(0);
            tvRacer1Name.setText(racer1.getName());
            tvRacer1Odds.setText(String.format("%.1fx", racer1.getOdds()));
            seekBarRacer1.setProgress((int) racer1.getProgress());

            // Update racer 2
            Racer racer2 = racers.get(1);
            tvRacer2Name.setText(racer2.getName());
            tvRacer2Odds.setText(String.format("%.1fx", racer2.getOdds()));
            seekBarRacer2.setProgress((int) racer2.getProgress());

            // Update racer 3
            Racer racer3 = racers.get(2);
            tvRacer3Name.setText(racer3.getName());
            tvRacer3Odds.setText(String.format("%.1fx", racer3.getOdds()));
            seekBarRacer3.setProgress((int) racer3.getProgress());
        }
    }

    private void updateRaceStatus() {
        if (gameEngine.isRaceInProgress()) {
            tvRaceStatus.setText("🏃 Race in Progress!");
            tvQuickStats.setText("Watch the horses run!");
        } else if (gameEngine.isRaceFinished()) {
            List<Racer> results = gameEngine.getRaceResults();
            if (!results.isEmpty()) {
                String winner = results.get(0).getName();
                tvRaceStatus.setText("🏆 " + winner + " Wins!");

                int totalWinnings = gameEngine.getTotalWinnings();
                int netResult = gameEngine.getNetResult();
                String resultText = String.format("Winnings: %,d | Net: %s%,d",
                        totalWinnings, netResult >= 0 ? "+" : "", netResult);
                tvQuickStats.setText(resultText);
            }
        } else if (gameEngine.hasBets()) {
            tvRaceStatus.setText("🎯 Ready to Race!");
            int totalBets = gameEngine.getTotalBetAmount();
            tvQuickStats.setText(String.format("Total Bets: %,d points", totalBets));
        } else {
            tvRaceStatus.setText("💰 Place Your Bets!");
            tvQuickStats.setText("No bets placed yet");
        }
    }

    private void updateButtonStates() {
        boolean hasBets = gameEngine.hasBets();
        boolean raceInProgress = gameEngine.isRaceInProgress();
        boolean raceFinished = gameEngine.isRaceFinished();

        // Debug logging
        android.util.Log.d("MainActivity", "Button states - hasBets: " + hasBets +
                ", raceInProgress: " + raceInProgress +
                ", raceFinished: " + raceFinished +
                ", totalBets: " + gameEngine.getTotalBetAmount());

        btnPlaceBets.setEnabled(!raceInProgress);
        btnStartRace.setEnabled(hasBets && !raceInProgress && !raceFinished);
        btnNewRace.setEnabled(!raceInProgress);

        // Update button text based on state
        if (raceInProgress) {
            btnStartRace.setText("🏃 RACING...");
        } else if (raceFinished) {
            btnStartRace.setText("🏁 RACE FINISHED");
        } else if (hasBets) {
            btnStartRace.setText("🏁 START RACE");
        } else {
            btnStartRace.setText("🏁 START RACE");
        }

        // Debug: Log button enabled state
        android.util.Log.d("MainActivity", "Start Race button enabled: " + btnStartRace.isEnabled());
    }

    private void openBettingActivity() {
        Intent intent = new Intent(this, BettingActivity.class);
        startActivityForResult(intent, REQUEST_CODE_BETTING);
    }

    private void startRace() {
        if (!gameEngine.hasBets()) {
            return;
        }

        // Play race sequence (start sound + background music)
        soundManager.playRaceSequence();

        // Start the race
        gameEngine.startRace();

        // Update UI
        updateUI();

        // Start race animation
        startRaceAnimation();
    }

    private void startRaceAnimation() {
        raceUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (gameEngine.isRaceInProgress()) {
                    // Update race progress
                    gameEngine.updateRace();

                    // Update UI
                    updateRacerInfo();
                    updateRaceStatus();
                    updateButtonStates();

                    // Schedule next update
                    raceHandler.postDelayed(this, RACE_UPDATE_INTERVAL);
                } else if (gameEngine.isRaceFinished()) {
                    // Race finished
                    onRaceFinished();
                }
            }
        };

        raceHandler.post(raceUpdateRunnable);
    }

    private void onRaceFinished() {
        // Play race finish sound only (no background music stop)
        soundManager.playRaceFinishSound();

        // Mark all horses as finished in the UI
        seekBarRacer1.setRaceFinished();
        seekBarRacer2.setRaceFinished();
        seekBarRacer3.setRaceFinished();

        // Calculate winnings and update points
        int totalWinnings = gameEngine.getTotalWinnings();

        // ADD TOTAL WINNINGS (not net result)
        // Points were already deducted when bets were placed
        // Now we add back the full winnings amount
        if (totalWinnings > 0) {
            sessionManager.addPoints(totalWinnings);
        }

        // Update UI
        updateUI();

        // Navigate to results after a shorter delay
        raceHandler.postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, RaceResultsActivity.class);
            startActivity(intent);
        }, 2000); // Reduced delay since no extra sounds
    }

    private void setupNewRace() {
        // Stop any running race animation
        if (raceUpdateRunnable != null) {
            raceHandler.removeCallbacks(raceUpdateRunnable);
        }

        // Reset all horse animations
        seekBarRacer1.resetRace();
        seekBarRacer2.resetRace();
        seekBarRacer3.resetRace();

        // Generate new race
        gameEngine.generateNewOdds();

        // Update UI
        updateUI();

        // No click sound - removed as requested
    }

    private void logout() {
        showLogoutConfirmation();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you want to logout and exit the game?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sessionManager.logoutUser();
                    navigateToLogin();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_BETTING && resultCode == RESULT_OK) {
            // Betting activity returned, update UI
            updateUI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Debug: Log the current game state
        android.util.Log.d("MainActivity", "onResume - Has bets: " + gameEngine.hasBets() +
                ", Total bets: " + gameEngine.getTotalBetAmount() +
                ", Race in progress: " + gameEngine.isRaceInProgress() +
                ", Race finished: " + gameEngine.isRaceFinished());
        updateUI();

        // Ensure background music is playing when returning to main activity
        soundManager.startMainActivityBackgroundMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Clean up resources
        if (raceUpdateRunnable != null) {
            raceHandler.removeCallbacks(raceUpdateRunnable);
        }

        if (soundManager != null) {
            soundManager.release();
        }
    }
}