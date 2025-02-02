package com.example.game557;

import android.content.Intent;
import android.media.MediaPlayer; // Import MediaPlayer
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvSelectedNickname, textViewGameStatus, timerValue, textScore; // Added textScore
    private Button buttonStart, buttonReset;
    private DatabaseHelper dbHelper; // Initialize DatabaseHelper

    private final int[] imageViewCardIds = {
            R.id.imageViewCard0, R.id.imageViewCard1, R.id.imageViewCard2, R.id.imageViewCard3,
            R.id.imageViewCard4, R.id.imageViewCard5, R.id.imageViewCard6, R.id.imageViewCard7,
            R.id.imageViewCard8, R.id.imageViewCard9, R.id.imageViewCard10, R.id.imageViewCard11,
            R.id.imageViewCard12, R.id.imageViewCard13, R.id.imageViewCard14, R.id.imageViewCard15
    };

    private final int[] drawableCardIds = {
            R.drawable.ic_blue1, R.drawable.ic_gray1, R.drawable.ic_green1, R.drawable.ic_orange1,
            R.drawable.ic_pink1, R.drawable.ic_purple1, R.drawable.ic_red1, R.drawable.ic_yellow1
    };

    private boolean gameStarted = false;
    private CardInfo cardInfo1 = null;
    private int matchCount = 0;
    private boolean isWaiting = false;

    private Handler timerHandler = new Handler();
    private long startTime = 0L, elapsedTime = 0L;
    private Runnable timerRunnable;

    private int score = 0; // To store the score
    private String selectedNickname; // To store the selected nickname

    private int nicknameID; // To store the nicknameID
    private MediaPlayer mediaPlayer; // Declare MediaPlayer instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        textViewGameStatus = findViewById(R.id.textViewGameStatus);
        tvSelectedNickname = findViewById(R.id.tv_selected_nickname);
        timerValue = findViewById(R.id.timerValue);
        textScore = findViewById(R.id.textScore); // Get reference to textScore TextView
        buttonStart = findViewById(R.id.buttonStart);
        buttonReset = findViewById(R.id.buttonReset);

        dbHelper = new DatabaseHelper(this); // Initialize the DatabaseHelper

        // Get nickname from intent
        Intent intent = getIntent();
        selectedNickname = intent.getStringExtra("selected_nickname");

        if (selectedNickname != null) {
            tvSelectedNickname.setText("Welcome, " + selectedNickname);
            // Retrieve nicknameID from the database using the selected nickname
            nicknameID = dbHelper.getNicknameIdByNickname(selectedNickname);
            Log.d("GameActivity", "nicknameID: " + nicknameID); // Log the nicknameID
        }

        setCardsClickable(false); // Disable cards at the start
        setupNewGame(); // Initial game setup

        // Timer Runnable (updates every 10ms)
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                elapsedTime = SystemClock.elapsedRealtime() - startTime;
                timerValue.setText(formatTime(elapsedTime));
                timerHandler.postDelayed(this, 10);
            }
        };

        // Start Button
        buttonStart.setOnClickListener(v -> {
            gameStarted = true;
            setCardsClickable(true);
            buttonStart.setEnabled(false); // Disable start button
            setupNewGame();
            startTimer();
        });

        // Reset Button
        buttonReset.setOnClickListener(v -> {
            gameStarted = false;
            setCardsClickable(false);
            buttonStart.setEnabled(true); // Re-enable start button
            resetTimer();
            setupNewGame();
        });

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.celebration);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void setupNewGame() {
        ArrayList<Integer> shuffledDrawableIds = new ArrayList<>();
        for (int drawableId : drawableCardIds) {
            shuffledDrawableIds.add(drawableId);
            shuffledDrawableIds.add(drawableId);
        }
        Collections.shuffle(shuffledDrawableIds);

        for (int i = 0; i < shuffledDrawableIds.size(); i++) {
            int imageViewId = imageViewCardIds[i];
            int drawableId = shuffledDrawableIds.get(i);
            CardInfo cardInfo = new CardInfo(imageViewId, drawableId);
            ImageView imageView = findViewById(imageViewId);
            imageView.setImageResource(R.drawable.ic_back01);
            imageView.setTag(cardInfo);
            imageView.setOnClickListener(this);
        }

        cardInfo1 = null;
        matchCount = 0;
        isWaiting = false;
        textViewGameStatus.setText("Press Start to Begin!");
        updateScoreDisplay(0); // Initialize the score display
    }

    private void setCardsClickable(boolean clickable) {
        for (int imageViewId : imageViewCardIds) {
            ImageView imageView = findViewById(imageViewId);
            imageView.setEnabled(clickable);
        }
    }

    private void startTimer() {
        startTime = SystemClock.elapsedRealtime();
        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void resetTimer() {
        stopTimer();
        elapsedTime = 0L;
        timerValue.setText("00:00:000");
    }

    private String formatTime(long timeInMillis) {
        int minutes = (int) (timeInMillis / 60000);
        int seconds = (int) ((timeInMillis % 60000) / 1000);
        int millis = (int) (timeInMillis % 1000);
        return String.format("%02d:%02d:%03d", minutes, seconds, millis);
    }

    private void coverCard(CardInfo cardInfo) {
        ImageView imageView = findViewById(cardInfo.getImageViewId());
        imageView.setImageResource(R.drawable.ic_back01);
        cardInfo.setFlipped(false);
        cardInfo.setMatched(false);
    }

    private int comboMultiplier = 1; // To store the current combo multiplier

    @Override
    public void onClick(View v) {
        if (!gameStarted || isWaiting) return; // Prevent clicking before starting

        ImageView imageView = (ImageView) v;
        CardInfo cardInfo = (CardInfo) imageView.getTag();

        if (!cardInfo.isFlipped() && !cardInfo.isMatched()) {
            imageView.setImageResource(cardInfo.getDrawableId());
            cardInfo.setFlipped(true);

            if (cardInfo1 == null) {
                cardInfo1 = cardInfo;
            } else {
                if (cardInfo.getDrawableId() == cardInfo1.getDrawableId()) {
                    matchCount++;
                    cardInfo1.setMatched(true);
                    cardInfo.setMatched(true);
                    textViewGameStatus.setText("Match Count: " + matchCount);

                    // Increase combo multiplier for consecutive matches
                    comboMultiplier++;
                    // Update score display with combo multiplier
                    updateScoreDisplay(100 * matchCount * comboMultiplier);

                    if (matchCount == 8) {
                        textViewGameStatus.setText("Game Completed!");
                        stopTimer(); // Stop timer when game completes

                        // Play celebration audio
                        if (mediaPlayer != null) {
                            mediaPlayer.start();
                        }

                        // Calculate the total score (100 points per match)
                        int baseScore = 100 * matchCount * comboMultiplier;

                        // Calculate bonus based on time
                        long timeTaken = elapsedTime / 1000; // Time in seconds
                        int bonus = calculateBonus(timeTaken);

                        // Calculate final score
                        int finalScore = baseScore + bonus;

                        // Display final score in an AlertDialog
                        showFinalScoreDialog(baseScore, bonus, finalScore);
                    }
                    cardInfo1 = null;
                } else {
                    // Reset combo multiplier on mismatch
                    comboMultiplier = 1;

                    isWaiting = true;
                    imageView.postDelayed(() -> {
                        isWaiting = false;
                        coverCard(cardInfo);
                        coverCard(cardInfo1);
                        cardInfo1 = null;
                    }, 2000);
                }
            }
        }
    }

    private int calculateBonus(long timeTaken) {
        int bonus = 0;
        if (timeTaken <= 30) {
            bonus = 20;
        } else if (timeTaken <= 45) {
            bonus = 15;
        } else if (timeTaken <= 60) {
            bonus = 10;
        } else if (timeTaken <= 75) {
            bonus = 5;
        }
        return bonus;
    }

    private void showFinalScoreDialog(int baseScore, int bonus, int finalScore) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle("Game Over")
                .setMessage("Nickname: " + selectedNickname + "\n" +
                        "Base Score: " + baseScore + "\n" +
                        "Bonus: " + bonus + "\n" +
                        "Final Score: " + finalScore)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Update the score in the leaderboard
                    if (nicknameID != -1) {
                        Log.d("GameActivity", "Updating score: " + finalScore + " for nicknameID: " + nicknameID);
                        boolean success = dbHelper.updateLeaderboardScore(finalScore, nicknameID);
                        if (success) {
                            Toast.makeText(this, "Score updated in Leaderboard", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update score in Leaderboard.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Invalid nickname ID.", Toast.LENGTH_SHORT).show();
                    }
                    finish(); // Close the activity
                })
                .show();
    }

    // Method to update score in the TextView
    private void updateScoreDisplay(int score) {
        textScore.setText("Score: " + score);
    }
}