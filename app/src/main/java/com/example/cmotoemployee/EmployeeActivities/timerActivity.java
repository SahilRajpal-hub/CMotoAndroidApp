package com.example.cmotoemployee.EmployeeActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmotoemployee.R;

public class timerActivity extends AppCompatActivity {
    private static final String TAG = "timerActivity";

    private ProgressBar barTimer;

    private CountDownTimer countDownTimer;

    private TextView textTimer;

    private void startTimer(final float minutes, float paramFloat2) {
        this.countDownTimer = (new CountDownTimer((long)(60 * minutes * 1000), 500) {
            float progress = 0;

            public void onFinish() {
                if(!textTimer.getText().toString().equals("00")){
                    timerActivity.this.textTimer.setText("00");
                    timerActivity.this.barTimer.setProgress(100);
                    Intent intent = new Intent((Context)timerActivity.this, UploadImagesActivity.class);
                    intent.putExtra("carNumber", timerActivity.this.getIntent().getStringExtra("carNumber"));
                    intent.putExtra("area", timerActivity.this.getIntent().getStringExtra("area"));
                    if (timerActivity.this.getIntent().hasExtra("interior"))
                        intent.putExtra("interior", "interior");
                    timerActivity.this.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }

            }

            public void onTick(long param1Long) {
                long seconds = param1Long / 1000;
                progress = (((float)seconds/(minutes*60))*100);
                Log.d(TAG, "onTick: seconds : " + seconds + " progress " + progress);
                barTimer.setProgress((int)this.progress);
                textTimer.setText(String.format("%02d", seconds/60) + ":" + String.format("%02d", seconds%60));
            }
        }).start();
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_timer);
        this.barTimer = (ProgressBar)findViewById(R.id.barTimer);
        this.textTimer = (TextView)findViewById(R.id.textTimer);
        Intent intent = getIntent();
        if (intent.hasExtra("timeInMinutes")) {
            Log.d("timerActivity", "onCreate: time remaining : " + intent.getFloatExtra("finalTimeInMinutes", 0.0F));
            startTimer(intent.getFloatExtra("timeInMinutes", 0.0F), intent.getFloatExtra("finalTimeInMinutes", 0.0F));
        }
    }
}


