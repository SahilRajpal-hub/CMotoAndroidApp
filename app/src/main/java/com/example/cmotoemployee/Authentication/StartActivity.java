package com.example.cmotoemployee.Authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.droidnet.DroidListener;
import com.droidnet.DroidNet;
import com.example.cmotoemployee.EmployeeActivities.HomeActivity;
import com.example.cmotoemployee.InteriorEmployeeActivities.InteriorHomeActivity;
import com.example.cmotoemployee.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity implements DroidListener {
    private static final String TAG = "StartActivity";

    private EditText Email;

    private EditText Password;

    private boolean admin = false;

    private ImageView back;

    private DroidNet droidNet;

    private TextView loginAsAdmin;

    private AppCompatButton loginButton;

    private RelativeLayout logo;

    private FirebaseAuth mAuth;

    private TextView mPleaseWait;

    private ProgressBar mProgressBar;

    private DatabaseReference reference;

    private void setVisibility(boolean paramBoolean) {
        if (paramBoolean) {
            this.Email.setVisibility(View.VISIBLE);
            this.loginButton.setVisibility(View.VISIBLE);
            this.loginAsAdmin.setVisibility(View.VISIBLE);
            this.Password.setVisibility(View.VISIBLE);
        } else {
            this.Email.setVisibility(View.GONE);
            this.loginButton.setVisibility(View.GONE);
            this.loginAsAdmin.setVisibility(View.GONE);
            this.Password.setVisibility(View.GONE);
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
//        intent.setFlags(268435456);
        startActivity(intent);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_start);
        Log.d("StartActivity", "onCreate: starting login activity for employee");
        DroidNet.init((Context)this);
        this.Email = (EditText)findViewById(R.id.input_email);
        this.Password = (EditText)findViewById(R.id.input_password);
//        this.loginAsAdmin = (TextView)findViewById(2131296537);
        this.back = (ImageView)findViewById(R.id.login_back);
        this.mAuth = FirebaseAuth.getInstance();
        this.loginButton = (AppCompatButton)findViewById(R.id.login_btn);
        this.mPleaseWait = (TextView)findViewById(R.id.loginPleaseWait);
        this.mProgressBar = (ProgressBar)findViewById(R.id.login_progressBar);
        this.logo = (RelativeLayout)findViewById(R.id.logo);
        this.mProgressBar.setVisibility(View.GONE);
        this.mPleaseWait.setVisibility(View.GONE);
        this.reference = FirebaseDatabase.getInstance().getReference();
//        this.loginAsAdmin.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View param1View) {
//                Log.d("StartActivity", "onClick: switching to admin login");
//                Intent intent = new Intent((Context)StartActivity.this, loginInteriorActivity.class);
//                StartActivity.this.startActivity(intent);
//                StartActivity.this.finish();
//            }
//        });
        this.back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                StartActivity.this.finish();
            }
        });
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                String str2 = StartActivity.this.Email.getText().toString();
                String str1 = StartActivity.this.Password.getText().toString();
                StartActivity.this.mProgressBar.setVisibility(View.VISIBLE);
                StartActivity.this.mPleaseWait.setVisibility(View.VISIBLE);
                if (str2.isEmpty() || str1.isEmpty()) {
                    Toast.makeText(StartActivity.this, "Please Input all the Credentials", Toast.LENGTH_SHORT).show();
                    StartActivity.this.mProgressBar.setVisibility(View.GONE);
                    StartActivity.this.mPleaseWait.setVisibility(View.GONE);
                    return;
                }
                StartActivity.this.mAuth.signInWithEmailAndPassword(str2, str1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    public void onComplete(Task<AuthResult> param2Task) {
                        if (param2Task.isSuccessful()) {
                            try {
                                StartActivity.this.mProgressBar.setVisibility(View.GONE);
                                StartActivity.this.mPleaseWait.setVisibility(View.GONE);
                                Toast.makeText(StartActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("InteriorEmployee").child(StartActivity.this.mAuth.getUid());
                                ValueEventListener valueEventListener = new ValueEventListener() {
                                    public void onCancelled(DatabaseError param3DatabaseError) {}

                                    public void onDataChange(DataSnapshot param3DataSnapshot) {
                                        if (param3DataSnapshot.exists()) {
                                            Log.d("StartActivity", "onDataChange: key = " + param3DataSnapshot);
                                            Intent intent = new Intent((Context)StartActivity.this, InteriorHomeActivity.class);
                                            StartActivity.this.startActivity(intent);
                                        } else {
                                            Intent intent = new Intent((Context)StartActivity.this, HomeActivity.class);
                                            StartActivity.this.startActivity(intent);
                                        }
                                        StartActivity.this.finish();
                                    }
                                };
//                                super(this);
                                databaseReference.addListenerForSingleValueEvent(valueEventListener);
                            } catch (NullPointerException nullPointerException) {
                                Log.d("StartActivity", "onComplete: error : " + nullPointerException);
                            }
                        } else {
                            StartActivity.this.mProgressBar.setVisibility(View.GONE);
                            StartActivity.this.mPleaseWait.setVisibility(View.GONE);
                            Toast.makeText((Context)StartActivity.this, "Task Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    public void onFailure(Exception param2Exception) {
                        Toast.makeText((Context)StartActivity.this, "Authentication failed : " + param2Exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void onInternetConnectivityChanged(boolean paramBoolean) {
        if (!paramBoolean)
            Toast.makeText((Context)this, "NO Internet Connection", Toast.LENGTH_SHORT).show();
    }

    public void onLowMemory() {
        super.onLowMemory();
        DroidNet.getInstance().removeAllInternetConnectivityChangeListeners();
    }

    public void onStart() {
        super.onStart();
        if (this.mAuth.getCurrentUser() != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("InteriorEmployee").child(this.mAuth.getUid());
            this.logo.setVisibility(View.VISIBLE);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onCancelled(DatabaseError param1DatabaseError) {}

                public void onDataChange(DataSnapshot param1DataSnapshot) {
                    if (param1DataSnapshot.exists()) {
                        Log.d("StartActivity", "onDataChange: key = " + param1DataSnapshot);
                        Intent intent = new Intent((Context)StartActivity.this, InteriorHomeActivity.class);
                        StartActivity.this.startActivity(intent);
                    } else {
                        Intent intent = new Intent((Context)StartActivity.this, HomeActivity.class);
                        StartActivity.this.startActivity(intent);
                    }
                    StartActivity.this.finish();
                }
            });
        } else {
            this.logo.setVisibility(View.GONE);
        }
    }
}

