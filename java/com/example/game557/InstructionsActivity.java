package com.example.game557;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class InstructionsActivity extends AppCompatActivity {

    private TextView instructionsText;
    private String instructions = "1. It is a color memorizing game application developed using Android with Java.\n" +
            "2. There are 16 cards with 8 different colors.\n" +
            "3. Each pair of cards has the same color.\n" +
            "4. There are 8 pairs of cards with 8 different colors.\n" +
            "5. All cards are always reversed with a grey coloured question mark card.\n" +
            "6. The arrangement of the card positions is randomized.\n" +
            "7. The player selects one card, and it will be flipped open.\n" +
            "8. The player must find and select the matching card.\n" +
            "9. If the cards match, they remain open. otherwise, they flip back after 2 seconds.\n" +
            "10. The player needs to reveal all cards as quickly as possible.\n" +
            "11. The timer starts when the Play button is clicked and stops when all cards are revealed.\n" +
            "12. The fastest player will be ranked at the top of the high-score leaderboard.";


    private int index = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        instructionsText = findViewById(R.id.instructionsText);

        // Apply fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        instructionsText.startAnimation(fadeIn);

        // Start typewriting effect
        startTypewriterEffect();
    }

    private void startTypewriterEffect() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < instructions.length()) {
                    instructionsText.setText(instructionsText.getText().toString() + instructions.charAt(index));
                    index++;
                    handler.postDelayed(this, 50);
                }
            }
        }, 50);
    }
}
