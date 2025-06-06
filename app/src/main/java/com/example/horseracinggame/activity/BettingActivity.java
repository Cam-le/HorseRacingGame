package com.example.horseracinggame.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.example.horseracinggame.R;
import com.example.horseracinggame.engine.GameEngine;
import com.example.horseracinggame.manager.SessionManager;
import com.example.horseracinggame.manager.SoundManager;
import com.example.horseracinggame.model.Racer;
import java.util.List;
import java.util.Map;

public class BettingActivity extends AppCompatActivity {

    // UI Components
    private TextView tvAvailablePoints;
    private TextView tvRacer1Name, tvRacer1Risk, tvRacer1Odds, tvRacer1Potential;
    private TextView tvRacer2Name, tvRacer2Risk, tvRacer2Odds, tvRacer2Potential;
    private TextView tvRacer3Name, tvRacer3Risk, tvRacer3Odds, tvRacer3Potential;
    private EditText etRacer1Bet, etRacer2Bet, etRacer3Bet;
    private Button btnRacer1_100, btnRacer1_500, btnRacer1_1000;
    private Button btnRacer2_100, btnRacer2_500, btnRacer2_1000;
    private Button btnRacer3_100, btnRacer3_500, btnRacer3_1000;
    private TextView tvStrategyTips, tvTotalBets, tvMaxPotential;
    private Button btnClearBets, btnConfirmBets;

    // Game Components
    private SessionManager sessionManager;
    private GameEngine gameEngine;
    private SoundManager soundManager;
    private List<Racer> racers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_betting);

        initializeComponents();
        initializeViews();
        setupClickListeners();
        setupTextWatchers();
        setupBackPressHandler();
        updateUI();
    }

    private void setupBackPressHandler() {
        // Handle back press with modern OnBackPressedCallback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Return to main activity without placing bets
                setResult(RESULT_CANCELED);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void initializeComponents() {
        sessionManager = new SessionManager(this);
        // Use the same GameEngine instance from MainActivity
        gameEngine = MainActivity.getGameEngine();
        soundManager = new SoundManager(this);
        racers = gameEngine.getRacers();
    }

    private void initializeViews() {
        // Header
        tvAvailablePoints = findViewById(R.id.tvAvailablePoints);

        // Racer 1
        tvRacer1Name = findViewById(R.id.tvRacer1Name);
        tvRacer1Risk = findViewById(R.id.tvRacer1Risk);
        tvRacer1Odds = findViewById(R.id.tvRacer1Odds);
        tvRacer1Potential = findViewById(R.id.tvRacer1Potential);
        etRacer1Bet = findViewById(R.id.etRacer1Bet);
        btnRacer1_100 = findViewById(R.id.btnRacer1_100);
        btnRacer1_500 = findViewById(R.id.btnRacer1_500);
        btnRacer1_1000 = findViewById(R.id.btnRacer1_1000);

        // Racer 2
        tvRacer2Name = findViewById(R.id.tvRacer2Name);
        tvRacer2Risk = findViewById(R.id.tvRacer2Risk);
        tvRacer2Odds = findViewById(R.id.tvRacer2Odds);
        tvRacer2Potential = findViewById(R.id.tvRacer2Potential);
        etRacer2Bet = findViewById(R.id.etRacer2Bet);
        btnRacer2_100 = findViewById(R.id.btnRacer2_100);
        btnRacer2_500 = findViewById(R.id.btnRacer2_500);
        btnRacer2_1000 = findViewById(R.id.btnRacer2_1000);

        // Racer 3
        tvRacer3Name = findViewById(R.id.tvRacer3Name);
        tvRacer3Risk = findViewById(R.id.tvRacer3Risk);
        tvRacer3Odds = findViewById(R.id.tvRacer3Odds);
        tvRacer3Potential = findViewById(R.id.tvRacer3Potential);
        etRacer3Bet = findViewById(R.id.etRacer3Bet);
        btnRacer3_100 = findViewById(R.id.btnRacer3_100);
        btnRacer3_500 = findViewById(R.id.btnRacer3_500);
        btnRacer3_1000 = findViewById(R.id.btnRacer3_1000);

        // Summary and actions
        tvStrategyTips = findViewById(R.id.tvStrategyTips);
        tvTotalBets = findViewById(R.id.tvTotalBets);
        tvMaxPotential = findViewById(R.id.tvMaxPotential);
        btnClearBets = findViewById(R.id.btnClearBets);
        btnConfirmBets = findViewById(R.id.btnConfirmBets);
    }

    private void setupClickListeners() {
        // Quick bet buttons for Racer 1
        btnRacer1_100.setOnClickListener(v -> setQuickBet(etRacer1Bet, 100));
        btnRacer1_500.setOnClickListener(v -> setQuickBet(etRacer1Bet, 500));
        btnRacer1_1000.setOnClickListener(v -> setQuickBet(etRacer1Bet, 1000));

        // Quick bet buttons for Racer 2
        btnRacer2_100.setOnClickListener(v -> setQuickBet(etRacer2Bet, 100));
        btnRacer2_500.setOnClickListener(v -> setQuickBet(etRacer2Bet, 500));
        btnRacer2_1000.setOnClickListener(v -> setQuickBet(etRacer2Bet, 1000));

        // Quick bet buttons for Racer 3
        btnRacer3_100.setOnClickListener(v -> setQuickBet(etRacer3Bet, 100));
        btnRacer3_500.setOnClickListener(v -> setQuickBet(etRacer3Bet, 500));
        btnRacer3_1000.setOnClickListener(v -> setQuickBet(etRacer3Bet, 1000));

        // Action buttons
        btnClearBets.setOnClickListener(v -> clearAllBets());
        btnConfirmBets.setOnClickListener(v -> confirmBets());
    }

    private void setupTextWatchers() {
        etRacer1Bet.addTextChangedListener(new BetTextWatcher(0));
        etRacer2Bet.addTextChangedListener(new BetTextWatcher(1));
        etRacer3Bet.addTextChangedListener(new BetTextWatcher(2));
    }

    private void updateUI() {
        updateAvailablePoints();
        updateRacerInfo();
        updateStrategyTips();
        updateBettingSummary();
    }

    private void updateAvailablePoints() {
        int points = sessionManager.getUserPoints();
        tvAvailablePoints.setText(String.format("Points: %,d", points));
    }

    private void updateRacerInfo() {
        if (racers.size() >= 3) {
            // Racer 1
            Racer racer1 = racers.get(0);
            tvRacer1Name.setText("🐎 " + racer1.getName());
            tvRacer1Risk.setText(racer1.getRiskCategory());
            tvRacer1Odds.setText(String.format("%.1fx", racer1.getOdds()));

            // Racer 2
            Racer racer2 = racers.get(1);
            tvRacer2Name.setText("🐎 " + racer2.getName());
            tvRacer2Risk.setText(racer2.getRiskCategory());
            tvRacer2Odds.setText(String.format("%.1fx", racer2.getOdds()));

            // Racer 3
            Racer racer3 = racers.get(2);
            tvRacer3Name.setText("🐎 " + racer3.getName());
            tvRacer3Risk.setText(racer3.getRiskCategory());
            tvRacer3Odds.setText(String.format("%.1fx", racer3.getOdds()));
        }
    }

    private void updateStrategyTips() {
        String tips = gameEngine.getStrategicRecommendation();
        tvStrategyTips.setText(tips);
    }

    private void updateBettingSummary() {
        int totalBets = getTotalCurrentBets();
        int maxPotential = getMaxPotentialWinnings();

        tvTotalBets.setText(String.format("%,d", totalBets));
        tvMaxPotential.setText(String.format("%,d", maxPotential));

        // Update individual potential winnings
        updatePotentialWinnings();
    }

    private void updatePotentialWinnings() {
        // Racer 1
        int bet1 = getBetAmount(etRacer1Bet);
        if (bet1 > 0 && racers.size() > 0) {
            int potential1 = (int)(bet1 * racers.get(0).getOdds());
            tvRacer1Potential.setText(String.format("Potential Winnings: %,d", potential1));
        } else {
            tvRacer1Potential.setText("Potential Winnings: 0");
        }

        // Racer 2
        int bet2 = getBetAmount(etRacer2Bet);
        if (bet2 > 0 && racers.size() > 1) {
            int potential2 = (int)(bet2 * racers.get(1).getOdds());
            tvRacer2Potential.setText(String.format("Potential Winnings: %,d", potential2));
        } else {
            tvRacer2Potential.setText("Potential Winnings: 0");
        }

        // Racer 3
        int bet3 = getBetAmount(etRacer3Bet);
        if (bet3 > 0 && racers.size() > 2) {
            int potential3 = (int)(bet3 * racers.get(2).getOdds());
            tvRacer3Potential.setText(String.format("Potential Winnings: %,d", potential3));
        } else {
            tvRacer3Potential.setText("Potential Winnings: 0");
        }
    }

    private void setQuickBet(EditText editText, int amount) {
        int availablePoints = sessionManager.getUserPoints();
        int currentTotalBets = getTotalCurrentBets();
        int currentBetForThisRacer = getBetAmount(editText);

        // Check if user can afford this bet
        int additionalBetNeeded = amount - currentBetForThisRacer;
        if (currentTotalBets + additionalBetNeeded > availablePoints) {
            soundManager.playErrorSound();
            Toast.makeText(this, "Insufficient points! Available: " +
                    String.format("%,d", availablePoints), Toast.LENGTH_SHORT).show();
            return;
        }

        editText.setText(String.valueOf(amount));
        // No click sound - removed as requested
    }

    private void clearAllBets() {
        etRacer1Bet.setText("");
        etRacer2Bet.setText("");
        etRacer3Bet.setText("");
        updateBettingSummary();
        soundManager.playClickSound();
    }

    private void confirmBets() {
        int bet1 = getBetAmount(etRacer1Bet);
        int bet2 = getBetAmount(etRacer2Bet);
        int bet3 = getBetAmount(etRacer3Bet);
        int totalBets = bet1 + bet2 + bet3;

        // Validate bets
        if (totalBets == 0) {
            soundManager.playErrorSound();
            Toast.makeText(this, "Please place at least one bet! 🎯", Toast.LENGTH_SHORT).show();
            return;
        }

        if (totalBets > sessionManager.getUserPoints()) {
            soundManager.playErrorSound();
            Toast.makeText(this, "Insufficient points! 💸", Toast.LENGTH_SHORT).show();
            return;
        }

        // Place bets in game engine
        boolean success = true;
        if (bet1 > 0) {
            success &= gameEngine.placeBet(racers.get(0).getName(), bet1);
            android.util.Log.d("BettingActivity", "Placed bet on " + racers.get(0).getName() + ": " + bet1);
        }
        if (bet2 > 0) {
            success &= gameEngine.placeBet(racers.get(1).getName(), bet2);
            android.util.Log.d("BettingActivity", "Placed bet on " + racers.get(1).getName() + ": " + bet2);
        }
        if (bet3 > 0) {
            success &= gameEngine.placeBet(racers.get(2).getName(), bet3);
            android.util.Log.d("BettingActivity", "Placed bet on " + racers.get(2).getName() + ": " + bet3);
        }

        // Debug: Check final state
        android.util.Log.d("BettingActivity", "After placing bets - Total: " + gameEngine.getTotalBetAmount() +
                ", Has bets: " + gameEngine.hasBets());

        if (success) {
            // Deduct points from user account
            sessionManager.subtractPoints(totalBets);

            // Play bet placed sound
            soundManager.playBetPlacedSound();

            // Show success message
            Toast.makeText(this, String.format("Bets placed! Total: %,d points 🎲", totalBets),
                    Toast.LENGTH_SHORT).show();

            // Return to main activity
            setResult(RESULT_OK);
            finish();
        } else {
            soundManager.playErrorSound();
            Toast.makeText(this, "Failed to place bets! Try again. ❌", Toast.LENGTH_SHORT).show();
        }
    }

    private int getBetAmount(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.isEmpty()) return 0;

        try {
            int amount = Integer.parseInt(text);
            return Math.max(0, amount);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int getTotalCurrentBets() {
        return getBetAmount(etRacer1Bet) + getBetAmount(etRacer2Bet) + getBetAmount(etRacer3Bet);
    }

    private int getMaxPotentialWinnings() {
        int potential1 = getBetAmount(etRacer1Bet) > 0 && racers.size() > 0 ?
                (int)(getBetAmount(etRacer1Bet) * racers.get(0).getOdds()) : 0;
        int potential2 = getBetAmount(etRacer2Bet) > 0 && racers.size() > 1 ?
                (int)(getBetAmount(etRacer2Bet) * racers.get(1).getOdds()) : 0;
        int potential3 = getBetAmount(etRacer3Bet) > 0 && racers.size() > 2 ?
                (int)(getBetAmount(etRacer3Bet) * racers.get(2).getOdds()) : 0;

        return Math.max(potential1, Math.max(potential2, potential3));
    }

    @Override
    public void onBackPressed() {
        // Return to main activity without placing bets
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.release();
        }
    }

    // TextWatcher class for real-time bet validation
    private class BetTextWatcher implements TextWatcher {
        private int racerIndex;

        public BetTextWatcher(int racerIndex) {
            this.racerIndex = racerIndex;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            updateBettingSummary();

            // Validate individual bet amount
            String text = s.toString().trim();
            if (!text.isEmpty()) {
                try {
                    int amount = Integer.parseInt(text);
                    int availablePoints = sessionManager.getUserPoints();

                    if (amount > availablePoints) {
                        // Show warning but don't prevent input
                        Toast.makeText(BettingActivity.this,
                                "Warning: Bet exceeds available points!",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    // Invalid number format
                }
            }
        }
    }
}