package com.example.horseracinggame.manager;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final String PREF_NAME = "HorseRacingSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_POINTS = "points";
    private static final String KEY_HIGH_SCORE = "high_score";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    // Hardcoded demo accounts
    private static final Map<String, String> DEMO_ACCOUNTS = new HashMap<>();
    static {
        DEMO_ACCOUNTS.put("player1", "pass1");
        DEMO_ACCOUNTS.put("player2", "pass2");
        DEMO_ACCOUNTS.put("admin", "admin123");
        DEMO_ACCOUNTS.put("guest", "guest");
    }

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Authenticate user with hardcoded accounts
    public boolean authenticateUser(String username, String password) {
        String correctPassword = DEMO_ACCOUNTS.get(username);
        if (correctPassword != null && correctPassword.equals(password)) {
            createLoginSession(username);
            return true;
        }
        return false;
    }

    // Create login session
    public void createLoginSession(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        // Initialize points if first time login
        if (!prefs.contains(KEY_POINTS + "_" + username)) {
            editor.putInt(KEY_POINTS + "_" + username, 3000); // Starting points
            editor.putInt(KEY_HIGH_SCORE + "_" + username, 3000);
        }

        editor.apply();
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Get current username
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    // Get user points
    public int getUserPoints() {
        String username = getUsername();
        return prefs.getInt(KEY_POINTS + "_" + username, 3000);
    }

    // Update user points
    public void updateUserPoints(int points) {
        String username = getUsername();
        editor.putInt(KEY_POINTS + "_" + username, points);

        // Update high score if necessary
        int currentHighScore = getHighScore();
        if (points > currentHighScore) {
            editor.putInt(KEY_HIGH_SCORE + "_" + username, points);
        }

        editor.apply();
    }

    // Add points to user account
    public void addPoints(int pointsToAdd) {
        int currentPoints = getUserPoints();
        updateUserPoints(currentPoints + pointsToAdd);
    }

    // Subtract points from user account
    public boolean subtractPoints(int pointsToSubtract) {
        int currentPoints = getUserPoints();
        if (currentPoints >= pointsToSubtract) {
            updateUserPoints(currentPoints - pointsToSubtract);
            return true;
        }
        return false; // Not enough points
    }

    // Get high score
    public int getHighScore() {
        String username = getUsername();
        return prefs.getInt(KEY_HIGH_SCORE + "_" + username, 3000);
    }

    // Reset user points to starting amount
    public void resetUserPoints() {
        String username = getUsername();
        editor.putInt(KEY_POINTS + "_" + username, 3000);
        editor.apply();
    }

    // Logout user
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    // Get all demo accounts for testing
    public static Map<String, String> getDemoAccounts() {
        return new HashMap<>(DEMO_ACCOUNTS);
    }

    // Check if user has enough points for betting
    public boolean canBet(int betAmount) {
        return getUserPoints() >= betAmount;
    }

    // Get user display info
    public String getUserDisplayInfo() {
        return String.format("Player: %s | Points: %d | High Score: %d",
                getUsername(), getUserPoints(), getHighScore());
    }
}