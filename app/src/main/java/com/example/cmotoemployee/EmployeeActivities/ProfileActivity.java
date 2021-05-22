package com.example.cmotoemployee.EmployeeActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.cmotoemployee.Authentication.StartActivity;
import com.example.cmotoemployee.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private TextView address;

    private FirebaseAuth auth;

    private ImageView back;

    private FirebaseDatabase database;

    private TextView email;

    private AppCompatButton logout;

    private TextView name;

    private EditText phone;

    private ImageView profileImage;

    private DatabaseReference reference;

    private AppCompatButton update;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_profile);
        this.back = (ImageView)findViewById(R.id.back);
        this.profileImage = (ImageView)findViewById(R.id.profileImage);
        this.name = (TextView)findViewById(R.id.name);
        this.address = (TextView)findViewById(R.id.address);
        this.email = (TextView)findViewById(R.id.emailId);
        this.phone = (EditText)findViewById(R.id.phoneNumber);
        this.logout = (AppCompatButton) findViewById(R.id.logout);
        this.update = (AppCompatButton)findViewById(R.id.update);
        this.auth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        this.database = firebaseDatabase;
        this.reference = firebaseDatabase.getReference();
        this.back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                ProfileActivity.this.finish();
            }
        });
        if (getIntent().hasExtra("interior")) {
            this.reference.child("InteriorEmployee").child(this.auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                public void onCancelled(DatabaseError param1DatabaseError) {}

                public void onDataChange(DataSnapshot param1DataSnapshot) {
                    ProfileActivity.this.name.setText(param1DataSnapshot.child("Name").getValue().toString());
                    ProfileActivity.this.phone.setText(param1DataSnapshot.child("ContactNumber").getValue().toString());
                    ProfileActivity.this.address.setText(param1DataSnapshot.child("Working_Address").getValue().toString());
                }
            });
        } else {
            this.reference.child("Employee").child(this.auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                public void onCancelled(DatabaseError param1DatabaseError) {}

                public void onDataChange(DataSnapshot param1DataSnapshot) {
                    ProfileActivity.this.name.setText(param1DataSnapshot.child("Name").getValue().toString());
                    ProfileActivity.this.phone.setText(param1DataSnapshot.child("ContactNumber").getValue().toString());
                    ProfileActivity.this.address.setText(param1DataSnapshot.child("Working_Address").getValue().toString());
                }
            });
        }
        this.update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                FirebaseDatabase.getInstance().getReference().child("Employee").child(FirebaseAuth.getInstance().getUid()).child("ContactNumber").setValue(ProfileActivity.this.phone.getText().toString());
            }
        });
        this.email.setText(this.auth.getCurrentUser().getEmail());
        this.logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                ProfileActivity.this.auth.signOut();
                ProfileActivity.this.startActivity((new Intent((Context)ProfileActivity.this, StartActivity.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }
}

