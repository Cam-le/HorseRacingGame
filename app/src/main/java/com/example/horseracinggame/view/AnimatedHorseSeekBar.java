package com.example.horseracinggame.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatSeekBar;
import com.example.horseracinggame.R;

import java.util.Objects;

public class AnimatedHorseSeekBar extends AppCompatSeekBar {

    private AnimationDrawable horseAnimation;
    private boolean isAnimating = false;
    private int horseColor = 0; // 0=red, 1=teal, 2=green

    public AnimatedHorseSeekBar(Context context) {
        super(context);
        init();
    }

    public AnimatedHorseSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedHorseSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Set the race track style
        setProgressDrawable(getContext().getDrawable(R.drawable.seekbar_race_track_style));

        // Initialize horse animation
        setupHorseAnimation();

        // Disable user interaction (this is for display only)
        setEnabled(false);
        setClickable(false);
        setFocusable(false);
    }

    private void setupHorseAnimation() {
        // Create the animation drawable
        horseAnimation = (AnimationDrawable) getContext().getDrawable(R.drawable.horse_animation);

        if (horseAnimation != null) {
            // Set as thumb
            setThumb(horseAnimation);

            // Configure animation timing
            updateAnimationSpeed(150); // Default 150ms per frame
        }
    }

    public void setHorseColor(int color) {
        this.horseColor = color;
        updateProgressTrackColor();
    }

    private void updateProgressTrackColor() {
        // Apply different tints based on horse color
        switch (horseColor) {
            case 0: // Red horse
                setProgressTintList(getContext().getColorStateList(android.R.color.holo_red_dark));
                break;
            case 1: // Teal horse
                setProgressTintList(getContext().getColorStateList(android.R.color.holo_blue_dark));
                break;
            case 2: // Green horse
                setProgressTintList(getContext().getColorStateList(android.R.color.holo_green_dark));
                break;
        }
    }

    public void startHorseAnimation() {
        if (horseAnimation != null && !isAnimating) {
            horseAnimation.start();
            isAnimating = true;
        }
    }

    public void stopHorseAnimation() {
        if (horseAnimation != null && isAnimating) {
            horseAnimation.stop();
            isAnimating = false;
        }
    }

    public void updateAnimationSpeed(int millisecondsPerFrame) {
        if (horseAnimation != null) {
            // AnimationDrawable doesn't have setDuration(), so we need to recreate it
            stopHorseAnimation();

            // Create new animation with updated timing
            AnimationDrawable newAnimation = new AnimationDrawable();
            newAnimation.setOneShot(false);

            // Add all frames with new duration
            try {
                newAnimation.addFrame(Objects.requireNonNull(getContext().getDrawable(R.drawable.horse_thumb_frame1)), millisecondsPerFrame);
                newAnimation.addFrame(Objects.requireNonNull(getContext().getDrawable(R.drawable.horse_thumb_frame2)), millisecondsPerFrame);
                newAnimation.addFrame(Objects.requireNonNull(getContext().getDrawable(R.drawable.horse_thumb_frame3)), millisecondsPerFrame);
                newAnimation.addFrame(Objects.requireNonNull(getContext().getDrawable(R.drawable.horse_thumb_frame4)), millisecondsPerFrame);
                newAnimation.addFrame(Objects.requireNonNull(getContext().getDrawable(R.drawable.horse_thumb_frame5)), millisecondsPerFrame);
                newAnimation.addFrame(Objects.requireNonNull(getContext().getDrawable(R.drawable.horse_thumb_frame6)), millisecondsPerFrame);

                // Replace the old animation
                horseAnimation = newAnimation;
                setThumb(horseAnimation);

            } catch (Exception e) {
                android.util.Log.e("AnimatedHorseSeekBar", "Error updating animation speed: " + e.getMessage());
            }
        }
    }

    public void setRaceProgress(double progress, boolean isMoving) {
        // Update progress (0-100)
        setProgress((int) Math.round(progress));

        // Control animation based on movement
        if (isMoving && progress < 100.0) {
            startHorseAnimation();
        } else {
            stopHorseAnimation();
        }
    }

    public void setRaceFinished() {
        stopHorseAnimation();
        // Set to first frame as a "finished" state
        if (getContext().getDrawable(R.drawable.horse_thumb_frame1) != null) {
            setThumb(getContext().getDrawable(R.drawable.horse_thumb_frame1));
        }
    }

    public void resetRace() {
        setProgress(0);
        stopHorseAnimation();
        setupHorseAnimation(); // Reset to animated thumb
    }

    public void setHorseSpeed(int speedMs) {
        // Faster horses = faster animation
        // Map speed (50-150ms) to animation speed (100-200ms)
        int animSpeed = Math.max(100, Math.min(200, speedMs + 50));
        updateAnimationSpeed(animSpeed);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Clean up animation to prevent memory leaks
        stopHorseAnimation();
    }
}