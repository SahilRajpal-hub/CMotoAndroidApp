package com.example.cmotoemployee.EmployeeActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.cmotoemployee.InteriorEmployeeActivities.InteriorHomeActivity;
import com.example.cmotoemployee.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import java.io.IOException;

public class QRCodeScanning extends AppCompatActivity {
    private static final String TAG = "QRCodeScanning";

    private static final int requestPermissionID = 101;

    private CompoundBarcodeView barcodeView;

    private FirebaseDatabase database;

    private String employeeType;

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
                public void receiveDetections(Detector.Detections<TextBlock> param1Detections) {
                    final SparseArray sparseArray = param1Detections.getDetectedItems();
                    if (sparseArray.size() != 0)
                        QRCodeScanning.this.mTextView.post(new Runnable() {
                            public void run() {

                            }
                        });
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


