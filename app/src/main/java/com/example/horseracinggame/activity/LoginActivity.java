package com.example.horseracinggame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.horseracinggame.R;
import com.example.horseracinggame.manager.SessionManager;
import com.example.horseracinggame.manager.SoundManager;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin, btnPlayer1, btnPlayer2, btnAdmin, btnGuest, btnRegister;
    private SessionManager sessionManager;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        soundManager = new SoundManager(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnPlayer1 = findViewById(R.id.btnPlayer1);
        btnPlayer2 = findViewById(R.id.btnPlayer2);
        btnAdmin = findViewById(R.id.btnAdmin);
        btnGuest = findViewById(R.id.btnGuest);
    }

    private void setupClickListeners() {
        // Main login button
        btnLogin.setOnClickListener(v -> performLogin());

        // Register button
        btnRegister.setOnClickListener(v -> openRegisterActivity());

        // Quick login buttons
        btnPlayer1.setOnClickListener(v -> quickLogin("player1", "pass1"));
        btnPlayer2.setOnClickListener(v -> quickLogin("player2", "pass2"));
        btnAdmin.setOnClickListener(v -> quickLogin("admin", "admin123"));
        btnGuest.setOnClickListener(v -> quickLogin("guest", "guest"));
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        // Attempt authentication
        if (sessionManager.authenticateUser(username, password)) {
            // No click sound - removed as requested
            Toast.makeText(this, "Welcome " + username + "! 🏆", Toast.LENGTH_SHORT).show();
            navigateToMainActivity();
        } else {
            soundManager.playErrorSound();
            Toast.makeText(this, "Invalid credentials! Try demo accounts below. ❌",
                    Toast.LENGTH_LONG).show();

            // Clear password field on failed login
            etPassword.setText("");
            etPassword.requestFocus();
        }
    }

    private void quickLogin(String username, String password) {
        if (sessionManager.authenticateUser(username, password)) {
            // No click sound - removed as requested
            Toast.makeText(this, "Quick login as " + username + "! 🚀", Toast.LENGTH_SHORT).show();
            navigateToMainActivity();
        } else {
            soundManager.playErrorSound();
            Toast.makeText(this, "Quick login failed! ❌", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Prevent going back to login screen
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Exit app instead of going to previous activity
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.release();
        }
    }
}