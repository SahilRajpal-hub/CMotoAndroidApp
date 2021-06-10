package com.example.cmotoemployee.EmployeeActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cmotoemployee.InteriorEmployeeActivities.InteriorHomeActivity;
import com.example.cmotoemployee.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

public class ImageUploadedActivity extends AppCompatActivity {
    private static final String TAG = "ImageUploadedActivity";
    private FirebaseAuth auth;

    private TextView carsDone;

    private TextView carsRemaining;

    private String employee;

    private Button goToList;

    private DatabaseReference reference;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_image_uploaded);
        if (getIntent().hasExtra("interior")) {
            this.employee = "InteriorEmployee";
        } else {
            this.employee = "Employee";
        }
        this.auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        this.goToList = (Button)findViewById(R.id.gotToCarList);
        this.carsRemaining = (TextView)findViewById(R.id.carsRemaining);
        this.carsDone = (TextView)findViewById(R.id.carsDone);
        reference.child(this.employee).child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError param1DatabaseError) {}

            public void onDataChange(DataSnapshot param1DataSnapshot) {
                Log.d(TAG, "onDataChange: " + param1DataSnapshot.getValue());
                if (param1DataSnapshot.getValue() != null) {
                    ArrayList<String> arrayList = new ArrayList(Arrays.asList((Object[])((String)param1DataSnapshot.child("todaysCars").getValue()).trim().split("\\s*,\\s*")));
                    HashSet<String> hashSet = new HashSet(arrayList);
                    arrayList.clear();
                    arrayList.addAll(hashSet);
                    arrayList.remove("");
                    ImageUploadedActivity.this.carsRemaining.setText(arrayList.size() + "");
                    int i = 0;
                    String str = (new SimpleDateFormat("yyyy-MM-dd")).format(Calendar.getInstance().getTime());
                    if (param1DataSnapshot.child("Work History").child(str).child("paidOn").exists()) {
                        i = (int)param1DataSnapshot.child("Work History").child(str).getChildrenCount() - 2;
                    } else if (param1DataSnapshot.child("Work History").child(str).exists()) {
                        i = (int)param1DataSnapshot.child("Work History").child(str).getChildrenCount() - 1;
                    }
                    ImageUploadedActivity.this.carsDone.setText(i + "");
                }
            }
        });
        this.goToList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (ImageUploadedActivity.this.getIntent().hasExtra("interior")) {
                    ImageUploadedActivity.this.startActivity(new Intent((Context)ImageUploadedActivity.this, InteriorHomeActivity.class));
                } else {
                    ImageUploadedActivity.this.startActivity(new Intent((Context)ImageUploadedActivity.this, HomeActivity.class));
                }
//                ImageUploadedActivity.this.finish();
            }
        });
    }
}
