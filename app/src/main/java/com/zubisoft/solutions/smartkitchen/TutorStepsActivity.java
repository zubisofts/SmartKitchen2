package com.zubisoft.solutions.smartkitchen;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.zubisoft.solutions.smartkitchen.adapters.TutorStepAdapter;
import com.zubisoft.solutions.smartkitchen.model.CookStep;
import com.zubisoft.solutions.smartkitchen.model.RecipeData;

import java.util.Locale;

public class TutorStepsActivity extends AppCompatActivity implements StepperLayout.StepperListener, TutorStepFragment.VoiceListener {

    private static int mId = 1003;
    private StepperLayout mStepperLayout;
    private RecipeData data;
    private TextToSpeech textToSpeech;
    private TextView timer_text;
    private FrameLayout timer_layout;
    private int duration;
    private boolean isTiming;
    private CircularProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private FloatingActionButton toggleVoice;
    private boolean useVoice = true;
    private int position;
    private TutorStepAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_steps);


        data = (RecipeData) getIntent().getSerializableExtra("recipe_data");
        position = getIntent().getIntExtra("position", 0);

        mStepperLayout = findViewById(R.id.stepperLayout);
        adapter = new TutorStepAdapter(getSupportFragmentManager(), this, data.getSteps());
        mStepperLayout.setAdapter(adapter);
        mStepperLayout.setListener(this);

        timer_layout = findViewById(R.id.timer_layout);
        timer_text = findViewById(R.id.timer_text);
        progressBar = findViewById(R.id.count_down_progress);

        initVoice();

        toggleVoice = findViewById(R.id.btn_toggle_voice);
        toggleVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSpeech();
            }
        });

        timer_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTiming) {
                    startTiming();
                }
            }
        });

        timer_text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });

        mStepperLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleVoice.isShown()) {
                    toggleVoice.hide();
                } else {
                    toggleVoice.show();
                }
            }
        });

    }

    private void initVoice() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setPitch(0.8f);
                    textToSpeech.setSpeechRate(1.0f);
                }
            }
        });
    }

    private void toggleSpeech() {
        if (useVoice) {
            toggleVoice.setImageResource(R.drawable.ic_mic_off);
            textToSpeech.stop();
        } else {
            toggleVoice.setImageResource(R.drawable.ic_mic_on);
        }
        useVoice = !useVoice;

    }

    private void startTiming() {

        final long milliSec = 60 * 1000 * duration;
        progressBar.setProgressMax(milliSec);

        countDownTimer = new CountDownTimer(milliSec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgressWithAnimation(progressBar.getProgress() + 1000, 1000);
                timer_text.setText(formatTime(millisUntilFinished));
//                Toast.makeText(getApplicationContext(), (millisUntilFinished/3600)+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                notifyDone();
                isTiming = false;
//                Toast.makeText(TutorStepsActivity.this, "Done!!!", Toast.LENGTH_SHORT).show();
            }
        };

        isTiming = true;


        countDownTimer.start();
    }

    private void notifyDone() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_menu_category)
                        .setContentTitle("Reminder")
                        .setContentText("Cook time elapsed! Click to check it out...")
                        .setAutoCancel(true)
                        .setSound(Uri.parse(String.format("android.resource://%s/%s/%s", this.getPackageName(), "raw", "notify_tone")));
        speak("Cook time elapsed! Click to check it out...");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, TutorStepsActivity.class);
        resultIntent.putExtra("recipe_data", data);
        resultIntent.putExtra("position", mStepperLayout.getCurrentStepPosition());
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(TutorStepsActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());

    }

    private String formatTime(long millis) {

        // convert milliseconds to seconds for easy calculation
        long secs = millis / 1000;

        int hour = (int) (secs / 3600);
        secs %= 3600;

        int mins = (int) (secs / 60);
        secs %= 60;

        int sec = (int) secs;

        String hourStr = hour < 10 ? "0" + hour : hour + "";
        String minStr = mins < 10 ? "0" + mins : mins + "";
        String secStr = sec < 10 ? "0" + sec : sec + "";

        if (hour > 0) {
            return hourStr + ":" + minStr + ":" + secStr;
        } else {
            return minStr + ":" + secStr;
        }
    }


    @Override
    public void onCompleted(View completeButton) {

    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {
        CookStep step = data.getSteps().get(newStepPosition);
        duration = step.getDuration();
//        Toast.makeText(this, ""+duration, Toast.LENGTH_SHORT).show();
        if (duration > 0) {
            step.getDuration();
            timer_layout.setVisibility(View.VISIBLE);
            timer_text.setText("START");
        } else {
            duration = 0;
            timer_layout.setVisibility(View.GONE);
        }

        isTiming = false;
        progressBar.setProgress(1);

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void onReturn() {

    }

    private void speak(String text) {
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }


    @Override
    public void onSpeak(final String text) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (useVoice) {
                    speak(text);
                }
            }
        }, 1000);


    }

    @Override
    public void onPause() {
        super.onPause();
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }


}
