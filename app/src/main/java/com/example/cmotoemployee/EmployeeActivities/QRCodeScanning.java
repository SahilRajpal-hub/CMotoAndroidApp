package com.example.cmotoemployee.EmployeeActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.cmotoemployee.InteriorEmployeeActivities.InteriorHomeActivity;
import com.example.cmotoemployee.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class QRCodeScanning extends AppCompatActivity {
    private static final String TAG = "QRCodeScanning";

    private static final int requestPermissionID = 101;

    private CompoundBarcodeView barcodeView;

    private FirebaseDatabase database;

    private String employeeType;

    private Boolean launched = false;

    private String employeesType;

    private ImageView exit;

    private CameraSource mCameraSource;

    private SurfaceView mCameraView;

    private TextView mTextView;

    private DatabaseReference reference;

    private TextView scanAnotherCar;

    private CameraSettings settings;

    private String status;

    private TextView textTryAgain;

    private String workHistory;

    Handler handler = new Handler();
    Runnable runnable;

    private void startCameraSource() {
        TextRecognizer textRecognizer = (new TextRecognizer.Builder(getApplicationContext())).build();
        if (!textRecognizer.isOperational()) {
            Log.w("QRCodeScanning", "Detector dependencies not loaded yet");
        } else {
            this.mCameraSource = (new CameraSource.Builder(getApplicationContext(), (Detector)textRecognizer)).setFacing(0).setRequestedPreviewSize(1280, 1024).setAutoFocusEnabled(true).setRequestedFps(2.0F).build();
            this.mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                public void surfaceChanged(SurfaceHolder param1SurfaceHolder, int param1Int1, int param1Int2, int param1Int3) {}

                public void surfaceCreated(SurfaceHolder param1SurfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(QRCodeScanning.this.getApplicationContext(), "android.permission.CAMERA") != 0) {
                            ActivityCompat.requestPermissions((Activity)QRCodeScanning.this, new String[] { "android.permission.CAMERA" }, 101);
                            return;
                        }
                        QRCodeScanning.this.mCameraSource.start(QRCodeScanning.this.mCameraView.getHolder());
                    } catch (IOException iOException) {
                        iOException.printStackTrace();
                    }
                }

                public void surfaceDestroyed(SurfaceHolder param1SurfaceHolder) {
                    QRCodeScanning.this.mCameraSource.stop();
                }
            });
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){



                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                final String id = new String(stringBuilder.toString());
                                String carNumber = getIntent().getStringExtra(getString(R.string.carNumber));
                                Log.d(TAG, "onActivityResult: carNumber : " + carNumber + " coming out :" + id +".");
                                long timeStamp = System.currentTimeMillis();

                                if(getIntent().hasExtra(getString(R.string.carNumber))) {
                                    String substring = carNumber.substring(Math.max(carNumber.length() - 5, 0), carNumber.length() - 1);
                                    if (id.equals(getIntent().getStringExtra(getString(R.string.carNumber))) ||
                                            carNumber.contains(id) || id.contains(carNumber) || substring.equals(id) || id.contains(substring)) {

                                        handler.removeCallbacks(this);
                                        reference.child("Car Status").child(carNumber).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                if(!snapshot.getValue().toString().equals("scanned")){
                                                    reference.child("Car Status").child(carNumber).child("status").setValue("scanned");
                                                    reference.child("Car Status").child(carNumber).child("timeStamp").setValue(String.valueOf(timeStamp));
                                                    FirebaseDatabase.getInstance().getReference().child(employeeType).child(FirebaseAuth.getInstance().getUid()).child(status).setValue("working");
                                                    FirebaseDatabase.getInstance().getReference().child(employeeType).child(FirebaseAuth.getInstance().getUid()).child("working on").setValue(getIntent().getStringExtra(getString(R.string.carNumber)));
                                                    FirebaseDatabase.getInstance().getReference().child(employeesType).child(getIntent().getStringExtra(getString(R.string.area))).child(FirebaseAuth.getInstance().getUid()).child("working on").setValue(getIntent().getStringExtra(getString(R.string.carNumber)));
                                                    Intent returnIntent = new Intent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    Toast.makeText(QRCodeScanning.this, "Car Verified", Toast.LENGTH_SHORT).show();
                                                    setResult(Activity.RESULT_OK, returnIntent);
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });




                                    } else {
                                        Log.d(TAG, "barcodeResult: scanner got " + stringBuilder.toString() + " instead of " + getIntent().getStringExtra(getString(R.string.carNumber)));
//                                        Toast.makeText(QRCodeScanning.this, "QRCode didn't match ", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    if(getIntent().hasExtra("carsNumberArray")){
                                        Bundle bundle = getIntent().getExtras();
                                        ArrayList<CharSequence> carsNumbers = bundle.getCharSequenceArrayList("carsNumberArray");
                                        String areas = (String) bundle.getCharSequence("Areas");
                                        if(carsNumbers.contains(id)){
                                            Toast.makeText(QRCodeScanning.this, "Car Verified", Toast.LENGTH_SHORT).show();
                                            reference.child("Car Status").child(carNumber).child("status").setValue("scanned");
                                            reference.child("Car Status").child(carNumber).child("timeStamp").setValue(String.valueOf(timeStamp));
                                            FirebaseDatabase.getInstance().getReference().child(employeeType).child(FirebaseAuth.getInstance().getUid()).child(status).setValue("working");
                                            FirebaseDatabase.getInstance().getReference().child(employeeType).child(FirebaseAuth.getInstance().getUid()).child("working on").setValue(carNumber);
                                            FirebaseDatabase.getInstance().getReference().child(employeesType).child(areas).child(FirebaseAuth.getInstance().getUid()).child("working on").setValue(carNumber);
                                            Intent returnIntent = new Intent();
                                            setResult(Activity.RESULT_OK, returnIntent);
                                            finish();
                                        }else {
                                            Toast.makeText(QRCodeScanning.this, "You don't have this car to clean", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        };

                        handler.postDelayed(runnable,400);

                    }
                }

                public void release() {}
            });
        }
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_q_r_code_scanning);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        this.database = firebaseDatabase;
        this.reference = firebaseDatabase.getReference();
        this.exit = (ImageView)findViewById(R.id.exit);
        this.textTryAgain = (TextView)findViewById(R.id.text2);
        this.scanAnotherCar = (TextView)findViewById(R.id.scanAnotherCar);
        this.mCameraView = (SurfaceView)findViewById(R.id.surfaceView);
        this.mTextView = (TextView)findViewById(R.id.text1);
        if (getIntent().hasExtra("interior")) {
            Log.d("QRCodeScanning", "onCreate: employeeType : interior");
            this.employeeType = "InteriorEmployee";
            this.employeesType = "InteriorEmployees";
            this.status = "Interior Cleaning status";
            this.workHistory = "Interior Work History";
        } else {
            this.employeeType = "Employee";
            this.employeesType = "Employees";
            this.status = "status";
            this.workHistory = "Work History";
        }
        startCameraSource();
        this.exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                QRCodeScanning.this.finish();
            }
        });
        this.scanAnotherCar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (QRCodeScanning.this.getIntent().hasExtra("interior")) {
                    QRCodeScanning.this.startActivity(new Intent((Context)QRCodeScanning.this, InteriorHomeActivity.class));
                } else {
                    QRCodeScanning.this.startActivity(new Intent((Context)QRCodeScanning.this, HomeActivity.class));
                }
                QRCodeScanning.this.finish();
            }
        });
    }

    public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfint) {
        if (paramInt != 101) {
            Log.d("QRCodeScanning", "denied permission result: " + paramInt);
            super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfint);
            return;
        }
        if (paramArrayOfint[0] == 0)
            try {
                if (ActivityCompat.checkSelfPermission((Context)this, "android.permission.CAMERA") != 0)
                    return;
                this.mCameraSource.start(this.mCameraView.getHolder());
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
    }
}


