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

    private void startTimer(final float minuti, float paramFloat2) {
        this.countDownTimer = (new CountDownTimer((long)(60.0F * minuti * 1000.0F), 500L) {
            float progress = 0.0F;

            public void onFinish() {
                timerActivity.this.textTimer.setText("00:00");
                timerActivity.this.barTimer.setProgress(100);
                Intent intent = new Intent((Context)timerActivity.this, UploadImagesActivity.class);
                intent.putExtra("carNumber", timerActivity.this.getIntent().getStringExtra("carNumber"));
                intent.putExtra("area", timerActivity.this.getIntent().getStringExtra("area"));
                if (timerActivity.this.getIntent().hasExtra("interior"))
                    intent.putExtra("interior", "interior");
                timerActivity.this.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }

            public void onTick(long param1Long) {
                param1Long /= 1000L;
                this.progress = (float)param1Long / minuti * 60.0F * 100.0F;
                timerActivity.this.barTimer.setProgress((int)this.progress);
                timerActivity.this.textTimer.setText(String.format("%02d", new Object[] { Long.valueOf(param1Long / 60L) }) + ":" + String.format("%02d", new Object[] { Long.valueOf(param1Long % 60L) }));
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


