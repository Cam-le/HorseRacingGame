package com.example.horseracinggame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.example.horseracinggame.R;
import com.example.horseracinggame.engine.GameEngine;
import com.example.horseracinggame.manager.SessionManager;
import com.example.horseracinggame.manager.SoundManager;
import com.example.horseracinggame.model.Racer;
import java.util.List;
import java.util.Map;

public class RaceResultsActivity extends AppCompatActivity {

    // UI Components
    private TextView tvRaceOutcome;
    private TextView tvFirstPlace, tvFirstProgress, tvFirstOdds;
    private TextView tvSecondPlace, tvSecondProgress, tvSecondOdds;
    private TextView tvThirdPlace, tvThirdProgress, tvThirdOdds;
    private TextView tvTotalBetAmount, tvTotalWinnings, tvNetResult, tvResultMessage;
    private LinearLayout layoutBetDetails;
    private TextView tvRaceAnalytics, tvStrategicInsights;
    private TextView tvUpdatedPoints, tvUpdatedHighScore;
    private Button btnRaceAgain, btnMainMenu;

    // Game Components
    private SessionManager sessionManager;
    private GameEngine gameEngine;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_results);

        initializeComponents();
        initializeViews();
        setupClickListeners();
        setupBackPressHandler();
        displayResults();
    }

    private void setupBackPressHandler() {
        // Handle back press with modern OnBackPressedCallback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Go back to main menu
                goToMainMenu();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void initializeComponents() {
        sessionManager = new SessionManager(this);
        // Use the same GameEngine instance from MainActivity
        gameEngine = MainActivity.getGameEngine();
        soundManager = new SoundManager(this);
    }

    private void initializeViews() {
        // Header
        tvRaceOutcome = findViewById(R.id.tvRaceOutcome);

        // Podium
        tvFirstPlace = findViewById(R.id.tvFirstPlace);
        tvFirstProgress = findViewById(R.id.tvFirstProgress);
        tvFirstOdds = findViewById(R.id.tvFirstOdds);
        tvSecondPlace = findViewById(R.id.tvSecondPlace);
        tvSecondProgress = findViewById(R.id.tvSecondProgress);
        tvSecondOdds = findViewById(R.id.tvSecondOdds);
        tvThirdPlace = findViewById(R.id.tvThirdPlace);
        tvThirdProgress = findViewById(R.id.tvThirdProgress);
        tvThirdOdds = findViewById(R.id.tvThirdOdds);

        // Betting summary
        tvTotalBetAmount = findViewById(R.id.tvTotalBetAmount);
        tvTotalWinnings = findViewById(R.id.tvTotalWinnings);
        tvNetResult = findViewById(R.id.tvNetResult);
        tvResultMessage = findViewById(R.id.tvResultMessage);

        // Detailed breakdown
        layoutBetDetails = findViewById(R.id.layoutBetDetails);

        // Analytics and insights
        tvRaceAnalytics = findViewById(R.id.tvRaceAnalytics);
        tvStrategicInsights = findViewById(R.id.tvStrategicInsights);

        // Updated points
        tvUpdatedPoints = findViewById(R.id.tvUpdatedPoints);
        tvUpdatedHighScore = findViewById(R.id.tvUpdatedHighScore);

        // Action buttons
        btnRaceAgain = findViewById(R.id.btnRaceAgain);
        btnMainMenu = findViewById(R.id.btnMainMenu);
    }

    private void setupClickListeners() {
        btnRaceAgain.setOnClickListener(v -> raceAgain());
        btnMainMenu.setOnClickListener(v -> goToMainMenu());
    }

    private void displayResults() {
        if (!gameEngine.isRaceFinished()) {
            // If no race finished, show default message
            tvRaceOutcome.setText("No Race Data Available");
            return;
        }

        displayPodiumResults();
        displayBettingSummary();
        displayBetBreakdown();
        displayRaceAnalytics();
        displayStrategicInsights();
        displayUpdatedPoints();

        // Play appropriate result sound
        int netResult = gameEngine.getNetResult();
        boolean isWinner = netResult > 0;
        soundManager.playResultSound(isWinner, netResult);
    }

    private void displayPodiumResults() {
        List<Racer> results = gameEngine.getRaceResults();

        if (results.size() >= 3) {
            // Winner
            Racer first = results.get(0);
            tvRaceOutcome.setText(first.getName() + " Wins! 🏆");
            tvFirstPlace.setText(first.getName());
            tvFirstProgress.setText(String.format("%.1f%% Complete", first.getProgress()));
            tvFirstOdds.setText(String.format("%.1fx", first.getOdds()));

            // Second place
            Racer second = results.get(1);
            tvSecondPlace.setText(second.getName());
            tvSecondProgress.setText(String.format("%.1f%% Complete", second.getProgress()));
            tvSecondOdds.setText(String.format("%.1fx", second.getOdds()));

            // Third place
            Racer third = results.get(2);
            tvThirdPlace.setText(third.getName());
            tvThirdProgress.setText(String.format("%.1f%% Complete", third.getProgress()));
            tvThirdOdds.setText(String.format("%.1fx", third.getOdds()));
        }
    }

    private void displayBettingSummary() {
        int totalBets = gameEngine.getTotalBetAmount();
        int totalWinnings = gameEngine.getTotalWinnings();
        int netResult = gameEngine.getNetResult();

        tvTotalBetAmount.setText(String.format("%,d", totalBets));
        tvTotalWinnings.setText(String.format("%,d", totalWinnings));

        // Format net result with + or - sign
        String netResultText = String.format("%s%,d", netResult >= 0 ? "+" : "", netResult);
        tvNetResult.setText(netResultText);
        tvNetResult.setTextColor(getResources().getColor(
                netResult >= 0 ? android.R.color.holo_green_light : android.R.color.holo_red_light));

        // Set result message
        String resultMessage;
        if (netResult > 1000) {
            resultMessage = "🎉 Incredible win! You're on fire!";
        } else if (netResult > 0) {
            resultMessage = "🎊 Great job! You made a profit!";
        } else if (netResult == 0) {
            resultMessage = "😐 Break even! Better luck next time!";
        } else if (netResult > -500) {
            resultMessage = "😔 Small loss, you'll get it next time!";
        } else {
            resultMessage = "💸 Tough race! Consider a new strategy!";
        }

        tvResultMessage.setText(resultMessage);
    }

    private void displayBetBreakdown() {
        layoutBetDetails.removeAllViews();

        Map<String, Integer> bets = gameEngine.getCurrentBets();
        Map<String, Integer> winnings = gameEngine.calculateWinnings();

        if (bets.isEmpty()) {
            TextView noBetsView = new TextView(this);
            noBetsView.setText("No bets were placed in this race.");
            noBetsView.setTextColor(getResources().getColor(android.R.color.darker_gray));
            noBetsView.setTextSize(14);
            layoutBetDetails.addView(noBetsView);
            return;
        }

        for (Map.Entry<String, Integer> bet : bets.entrySet()) {
            String racerName = bet.getKey();
            int betAmount = bet.getValue();
            int winAmount = winnings.getOrDefault(racerName, 0);
            int netForThisBet = winAmount - betAmount;

            // Create bet detail view
            LinearLayout betDetailView = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.item_bet_detail, layoutBetDetails, false);

            TextView tvRacerName = betDetailView.findViewById(R.id.tvBetRacerName);
            TextView tvBetAmount = betDetailView.findViewById(R.id.tvBetAmount);
            TextView tvWinAmount = betDetailView.findViewById(R.id.tvWinAmount);
            TextView tvBetResult = betDetailView.findViewById(R.id.tvBetResult);

            tvRacerName.setText(racerName);
            tvBetAmount.setText(String.format("Bet: %,d", betAmount));
            tvWinAmount.setText(String.format("Won: %,d", winAmount));

            String resultText = String.format("%s%,d", netForThisBet >= 0 ? "+" : "", netForThisBet);
            tvBetResult.setText(resultText);
            tvBetResult.setTextColor(getResources().getColor(
                    netForThisBet >= 0 ? android.R.color.holo_green_light : android.R.color.holo_red_light));

            layoutBetDetails.addView(betDetailView);
        }
    }

    private void displayRaceAnalytics() {
        List<Racer> results = gameEngine.getRaceResults();

        if (results.size() >= 2) {
            Racer winner = results.get(0);
            Racer second = results.get(1);

            double victoryMargin = winner.getProgress() - second.getProgress();

            // Calculate actual race duration
            long raceStartTime = System.currentTimeMillis() - 30000; // Approximate
            long raceDuration = (winner.getFinishTime() - raceStartTime) / 1000;

            String analytics = String.format(
                    "🏁 Victory Margin: %.1f%%\n" +
                            "⚡ Winner's Final Speed: %dms\n" +
                            "🕐 Race Duration: ~%.1f seconds\n" +
                            "🎯 Winning Odds: %.1fx\n" +
                            "🏃 Race Type: %s",
                    victoryMargin,
                    winner.getCurrentSpeed(),
                    Math.max(raceDuration, 1.0), // Minimum 1 second
                    winner.getOdds(),
                    victoryMargin > 10 ? "Dominant Win" :
                            victoryMargin > 5 ? "Clear Victory" :
                                    victoryMargin > 2 ? "Close Race" : "Photo Finish"
            );

            tvRaceAnalytics.setText(analytics);
        }
    }

    private void displayStrategicInsights() {
        int netResult = gameEngine.getNetResult();
        int totalBets = gameEngine.getTotalBetAmount();
        Map<String, Integer> bets = gameEngine.getCurrentBets();

        String insights;

        if (bets.size() == 1) {
            // Single bet strategy
            if (netResult > 0) {
                insights = "🎯 Excellent single-bet strategy! You picked the winner and maximized your payout. " +
                        "Consider this approach when you're confident about a favorite.";
            } else {
                insights = "🤔 Single-bet strategy didn't pay off this time. Consider spreading bets across " +
                        "multiple racers to reduce risk in future races.";
            }
        } else if (bets.size() == 2) {
            // Dual bet strategy
            if (netResult > 0) {
                insights = "⚖️ Smart dual-betting strategy! You balanced risk and reward effectively. " +
                        "This approach often provides good returns with moderate risk.";
            } else {
                insights = "💭 Your dual-betting approach shows good risk management thinking. " +
                        "Try adjusting bet amounts based on odds next time.";
            }
        } else {
            // Multi-bet strategy
            if (netResult > 0) {
                insights = "🎲 Diversified betting paid off! Spreading bets across all racers " +
                        "guaranteed a win. Great strategy for consistent, smaller profits.";
            } else {
                insights = "📊 Full diversification limits both risk and reward. Consider focusing " +
                        "more heavily on 1-2 favorites while keeping smaller hedge bets.";
            }
        }

        // Add betting amount insights
        if (totalBets > sessionManager.getUserPoints() / 2) {
            insights += "\n\n💡 Pro tip: Consider betting smaller amounts to preserve your bankroll for more races.";
        } else if (totalBets < sessionManager.getUserPoints() / 10) {
            insights += "\n\n🚀 You played it safe! Consider increasing bet sizes when you spot good opportunities.";
        }

        tvStrategicInsights.setText(insights);
    }

    private void displayUpdatedPoints() {
        int currentPoints = sessionManager.getUserPoints();
        int highScore = sessionManager.getHighScore();

        tvUpdatedPoints.setText(String.format("%,d", currentPoints));
        tvUpdatedHighScore.setText(String.format("%,d", highScore));
    }

    private void raceAgain() {
        // Generate new race and return to main menu
        gameEngine.generateNewOdds();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void goToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Go back to main menu
        super.onBackPressed();
        goToMainMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.release();
        }
    }
}