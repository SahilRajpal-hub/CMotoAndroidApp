package com.example.cmotoemployee.ErrorHandler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cmotoemployee.Authentication.StartActivity;
import com.example.cmotoemployee.R;

public class ExceptionDisplay extends AppCompatActivity {
    public void intentData() {
        Log.d("CDA", "onBackPressed Called");
        Intent intent = new Intent((Context)this, StartActivity.class);
        intent.addCategory("android.intent.category.HOME");
//        intent.setFlags(268435456);
        startActivity(intent);
    }

    public void onBackPressed() {
        intentData();
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_exception_display);
        TextView textView = (TextView)findViewById(R.id.exception_text);
        textView.setMovementMethod((MovementMethod)new ScrollingMovementMethod());
        Button button = (Button)findViewById(R.id.btnBack);
        textView.setText(getIntent().getExtras().getString("error"));
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                ExceptionDisplay.this.intentData();
            }
        });
    }
}

