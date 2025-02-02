package com.example.game557;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CreditActivity extends AppCompatActivity {

    private TextView creditsText;
    private TextView projectManagerText;
    private TextView developerText;
    private TextView qaTesterText;
    private TextView documentationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        creditsText = findViewById(R.id.creditsText);
        projectManagerText = findViewById(R.id.projectManagerText);
        developerText = findViewById(R.id.developerText);
        qaTesterText = findViewById(R.id.qaTesterText);
        documentationText = findViewById(R.id.documentationText);

        // Apply fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        creditsText.startAnimation(fadeIn);

        // Set the text for each role
        setCreditsText();
    }

    private void setCreditsText() {
        // Set the credits title
        creditsText.setText("These are the people who have participate during this app development");

        // Set the details for each representative with bold names
        projectManagerText.setText(getSpannableText("MOHAMAD DANISH HAMDANI BIN MOHAMAD MAJIDI",
                "Student ID: 2024568613\n" +
                        "Task: Oversees development, assigns tasks, manages communication, and handles documentation."));

        developerText.setText(getSpannableText("ALMAS FARID BIN MOHAMAD FARID",
                "Student ID: 2024926979\n" +
                        "Task: Develops game logic, UI, and mechanics in Java, integrates SQLite, and optimizes performance."));

        qaTesterText.setText(getSpannableText("MUHAMMAD AZIM AZRAEI BIN AZHA@AZHAR",
                "Student ID: 2024905131\n" +
                        "Task: Tests manually and automatically, ensuring logic, UI, and performance across devices."));

        documentationText.setText(getSpannableText("MUHAMMAD NUZUL AMRI BIN ZULKIFLI",
                "Student ID: 2024916973\n" +
                        "Task: Writes guides, documentation, and supports bug reports and feedback."));
    }

    private SpannableStringBuilder getSpannableText(String name, String details) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        // Create a SpannableString for the name and apply bold style
        SpannableString nameSpannable = new SpannableString(name);
        nameSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, name.length(), 0);

        // Append the name and details to the SpannableStringBuilder
        spannableStringBuilder.append(nameSpannable);
        spannableStringBuilder.append("\n").append(details);

        return spannableStringBuilder;
    }
}