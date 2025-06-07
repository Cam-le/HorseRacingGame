package com.example.horseracinggame.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.util.Log;
import android.view.SoundEffectConstants;
import android.view.View;
import com.example.horseracinggame.R;

public class SoundManager {
    private static final String TAG = "SoundManager";
    private Context context;
    private MediaPlayer mediaPlayer;
    private MediaPlayer backgroundMusicPlayer;
    private ToneGenerator toneGenerator;
    private boolean soundEnabled;

    public SoundManager(Context context) {
        this.context = context;
        this.soundEnabled = true; // Enable by default

        try {
            // Initialize ToneGenerator for built-in sounds
            toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 70); // 70% volume
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to initialize ToneGenerator: " + e.getMessage());
            toneGenerator = null;
        }
    }

    // Toggle sound on/off
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopCurrentSound();
            stopBackgroundMusic();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    // Play race start fanfare
    public void playRaceStartSound() {
        if (!soundEnabled) return;

        // Use custom sound file
        playSound(R.raw.race_start_fanfare);

        // Built-in alternative (commented out)
        // playBuiltInTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);

        Log.d(TAG, "🎺 Race Start Fanfare");
    }

    // Play race finish sound
    public void playRaceFinishSound() {
        if (!soundEnabled) return;

        // Use custom sound file
        playSound(R.raw.race_finish);

        // Built-in alternative (commented out)
        // playBuiltInTone(ToneGenerator.TONE_PROP_ACK, 800);

        Log.d(TAG, "🏁 Race Finish Sound");
    }

    // Play winning celebration sound
    public void playWinningSound() {
        if (!soundEnabled) return;

        // Use custom sound file
        playSound(R.raw.winning_celebration);

        // Built-in alternative (commented out)
        // playBuiltInTone(ToneGenerator.TONE_PROP_BEEP2, 1200);

        Log.d(TAG, "🎉 Winning Celebration");
    }

    // Play losing sound effect
    public void playLosingSound() {
        if (!soundEnabled) return;

        // Use custom sound file
        playSound(R.raw.losing_sound);

        // Built-in alternative (commented out)
        // playBuiltInTone(ToneGenerator.TONE_PROP_NACK, 800);

        Log.d(TAG, "😞 Losing Sound");
    }

    // Play bet placement confirmation
    public void playBetPlacedSound() {
        if (!soundEnabled) return;

        // Use custom sound file
        playSound(R.raw.bet_placed);

        // Built-in alternative (commented out)
        // playBuiltInTone(ToneGenerator.TONE_PROP_BEEP, 300);

        Log.d(TAG, "💰 Bet Placed Confirmation");
    }

    // Play UI click sound
    public void playClickSound() {
        if (!soundEnabled) return;


        Log.d(TAG, "🔘 UI Click Sound");
    }

    // Play error sound
    public void playErrorSound() {
        if (!soundEnabled) return;

        // Use custom sound file
        playSound(R.raw.error_sound);

        // Built-in alternative (commented out)
        // playBuiltInTone(ToneGenerator.TONE_PROP_NACK, 500);

        Log.d(TAG, "❌ Error Sound");
    }

    // Start continuous background music for main activity
    public void startMainActivityBackgroundMusic() {
        if (!soundEnabled) return;

        // Use race background music for main activity
        playLoopingSound(R.raw.race_background_music);

        Log.d(TAG, "🎵 Main Activity Background Music Started");
    }

    // Play background music during race (same as main activity)
    public void playRaceBackgroundMusic() {
        if (!soundEnabled) return;

        // Continue the same background music during race
        // No need to restart if already playing
        if (backgroundMusicPlayer == null || !backgroundMusicPlayer.isPlaying()) {
            playLoopingSound(R.raw.race_background_music);
        }

        Log.d(TAG, "🎵 Race Background Music Continued");
    }

    // Stop background music
    public void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            try {
                if (backgroundMusicPlayer.isPlaying()) {
                    backgroundMusicPlayer.stop();
                }
                backgroundMusicPlayer.release();
                backgroundMusicPlayer = null;
                Log.d(TAG, "🎵 Background Music Stopped");
            } catch (Exception e) {
                Log.e(TAG, "Error stopping background music: " + e.getMessage());
            }
        }
    }

    // Generic method to play sound from resource
    private void playSound(int soundResourceId) {
        try {
            // Stop any currently playing sound effect
            stopCurrentSound();

            // Create new MediaPlayer instance
            mediaPlayer = MediaPlayer.create(context, soundResourceId);

            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    mediaPlayer = null;
                });

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "MediaPlayer error: what=" + what + ", extra=" + extra);
                    mp.release();
                    mediaPlayer = null;
                    return true;
                });

                mediaPlayer.start();
            } else {
                Log.e(TAG, "Failed to create MediaPlayer for resource: " + soundResourceId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing sound: " + e.getMessage());
        }
    }

    // Generic method to play looping background sound
    private void playLoopingSound(int soundResourceId) {
        try {
            // Stop any currently playing background music
            stopBackgroundMusic();

            // Create new MediaPlayer instance for background music
            backgroundMusicPlayer = MediaPlayer.create(context, soundResourceId);

            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.setLooping(true);
                backgroundMusicPlayer.setVolume(0.4f, 0.4f); // Lower volume for background music

                backgroundMusicPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "Background music error: what=" + what + ", extra=" + extra);
                    mp.release();
                    backgroundMusicPlayer = null;
                    return true;
                });

                backgroundMusicPlayer.start();
            } else {
                Log.e(TAG, "Failed to create MediaPlayer for background music: " + soundResourceId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing background music: " + e.getMessage());
        }
    }

    // Built-in tone generator method (fallback)
    private void playBuiltInTone(int toneType, int durationMs) {
        if (toneGenerator != null) {
            try {
                toneGenerator.startTone(toneType, durationMs);
            } catch (Exception e) {
                Log.e(TAG, "Error playing built-in tone: " + e.getMessage());
            }
        }
    }

    // Stop currently playing sound effect
    public void stopCurrentSound() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                Log.e(TAG, "Error stopping sound: " + e.getMessage());
            }
        }
    }

    // Stop all sounds and release resources
    public void release() {
        stopCurrentSound();
        stopBackgroundMusic();

        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
        }

        Log.d(TAG, "SoundManager resources released");
    }

    // Play appropriate sound based on race result - SIMPLIFIED
    public void playResultSound(boolean isWinner, int netResult) {
        // Removed individual win/lose sounds
        // Race finish sound is sufficient feedback
        Log.d(TAG, isWinner ? "🎉 Race Won" : "😞 Race Lost");
    }

    // Play sound sequence for race progression
    public void playRaceSequence() {
        playRaceStartSound();

        // Background music should already be playing from main activity
        // Just ensure it continues during race
        playRaceBackgroundMusic();

        Log.d(TAG, "🏃 Race Sequence Started");
    }

    // Convenience method to play system UI sounds via View
    public void playSystemClickSound(View view) {
        if (soundEnabled && view != null) {
            view.playSoundEffect(SoundEffectConstants.CLICK);
        }
    }

    // Volume control
    public void setVolume(float volume) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.setVolume(volume, volume);
        }
        if (backgroundMusicPlayer != null && backgroundMusicPlayer.isPlaying()) {
            backgroundMusicPlayer.setVolume(volume * 0.3f, volume * 0.3f); // Background music stays quieter
        }
    }

    // Check if any sound is currently playing
    public boolean isPlaying() {
        return (mediaPlayer != null && mediaPlayer.isPlaying()) ||
                (backgroundMusicPlayer != null && backgroundMusicPlayer.isPlaying());
    }

}