package com.example.horseracinggame.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.example.horseracinggame.R;
import com.example.horseracinggame.manager.SoundManager;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etRegUsername, etRegEmail, etRegPassword, etRegConfirmPassword;
    private CheckBox cbTerms;
    private Button btnCreateAccount;
    private TextView tvBackToLogin;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeComponents();
        initializeViews();
        setupClickListeners();
        setupBackPressHandler();
    }

    private void initializeComponents() {
        soundManager = new SoundManager(this);
    }

    private void initializeViews() {
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }

    private void setupClickListeners() {
        btnCreateAccount.setOnClickListener(v -> attemptRegistration());
        tvBackToLogin.setOnClickListener(v -> goBackToLogin());
    }

    private void setupBackPressHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goBackToLogin();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void attemptRegistration() {
        // Get form data
        String username = etRegUsername.getText().toString().trim();
        String email = etRegEmail.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();
        String confirmPassword = etRegConfirmPassword.getText().toString().trim();
        boolean termsAccepted = cbTerms.isChecked();

        // Validate form
        if (!validateForm(username, email, password, confirmPassword, termsAccepted)) {
            return;
        }

        // Simulate registration process
        simulateRegistration(username, email);
    }

    private boolean validateForm(String username, String email, String password,
                                 String confirmPassword, boolean termsAccepted) {

        // Username validation
        if (username.isEmpty()) {
            etRegUsername.setError("Username is required");
            etRegUsername.requestFocus();
            soundManager.playErrorSound();
            return false;
        }

        if (username.length() < 3) {
            etRegUsername.setError("Username must be at least 3 characters");
            etRegUsername.requestFocus();
            soundManager.playErrorSound();
            return false;
        }

        // Email validation
        if (email.isEmpty()) {
            etRegEmail.setError("Email is required");
            etRegEmail.requestFocus();
            soundManager.playErrorSound();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegEmail.setError("Please enter a valid email address");
            etRegEmail.requestFocus();
            soundManager.playErrorSound();
            return false;
        }

        // Password validation
        if (password.isEmpty()) {
            etRegPassword.setError("Password is required");
            etRegPassword.requestFocus();
            soundManager.playErrorSound();
            return false;
        }

        if (password.length() < 6) {
            etRegPassword.setError("Password must be at least 6 characters");
            etRegPassword.requestFocus();
            soundManager.playErrorSound();
            return false;
        }

        // Confirm password validation
        if (!password.equals(confirmPassword)) {
            etRegConfirmPassword.setError("Passwords do not match");
            etRegConfirmPassword.requestFocus();
            soundManager.playErrorSound();
            return false;
        }

        // Terms validation
        if (!termsAccepted) {
            Toast.makeText(this, "Please accept the Terms of Service to continue",
                    Toast.LENGTH_LONG).show();
            soundManager.playErrorSound();
            return false;
        }

        return true;
    }

    private void simulateRegistration(String username, String email) {
        // Disable form during "registration"
        setFormEnabled(false);
        btnCreateAccount.setText("🔄 Creating Account...");

        // Simulate network delay
        new android.os.Handler().postDelayed(() -> {
            // Show success message
            showRegistrationSuccess(username);
        }, 2000); // 2 second delay
    }

    private void showRegistrationSuccess(String username) {
        // Re-enable form
        setFormEnabled(true);
        btnCreateAccount.setText("🎯 CREATE ACCOUNT");

        // Show success dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("🎉 Registration Successful!")
                .setMessage("Welcome to Horse Racing Game, " + username + "!\n\n" +
                        "In the full version, your account would be created and you could login with your credentials.\n\n" +
                        "For now, please use the demo accounts to continue.")
                .setPositiveButton("Go to Login", (dialog, which) -> goBackToLogin())
                .setNegativeButton("Stay Here", null)
                .setCancelable(false)
                .show();

        // Play success sound (using bet placed sound as substitute)
        soundManager.playBetPlacedSound();
    }

    private void setFormEnabled(boolean enabled) {
        etRegUsername.setEnabled(enabled);
        etRegEmail.setEnabled(enabled);
        etRegPassword.setEnabled(enabled);
        etRegConfirmPassword.setEnabled(enabled);
        cbTerms.setEnabled(enabled);
        btnCreateAccount.setEnabled(enabled);
    }

    private void goBackToLogin() {
        finish(); // Return to LoginActivity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.release();
        }
    }
}