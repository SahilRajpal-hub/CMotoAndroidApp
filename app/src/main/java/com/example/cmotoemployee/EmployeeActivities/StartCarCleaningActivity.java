package com.example.cmotoemployee.EmployeeActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.droidnet.DroidListener;
import com.droidnet.DroidNet;
import com.example.cmotoemployee.ErrorHandler.CrashHandler;
import com.example.cmotoemployee.InteriorEmployeeActivities.InteriorHomeActivity;
import com.example.cmotoemployee.Model.Car;
import com.example.cmotoemployee.R;
import com.example.cmotoemployee.RoundedTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.nostra13.universalimageloader.utils.L;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class StartCarCleaningActivity extends AppCompatActivity implements DroidListener {
    private static final int CAMERA_REQUEST_CODE = 2;

    private static final int SCANNED_ACTIVITY_RESULT = 29;

    private static final String TAG = "StartCarCleanActivity";

    private static final int VERIFY_PERMISSION_REQUEST_CODE = 1;

    private ImageView CallOwner;

    private String CarNumber = "";

    private String CarOwnerPhone;

    private boolean Connected = true;

    private ImageView OpenMap;

    public final String[] PERMISSIONS = new String[] { "android.permission.CAMERA", "android.permission.ACCESS_FINE_LOCATION", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE" };

    private TextView Scanner;

    private String area;

    private FirebaseAuth auth;

    private ImageView back;

    private TextView carCharacteristics;

    private TextView carColor;

    private TextView carLocation;

    private TextView carNumber;

    private TextView carNumberHeading;

    private ImageView carPhoto;

    private TextView car_number;

    private DroidNet droidNet;

    private IntentIntegrator integrator;

    private long lastClicked = 0L;

    private String location;

    private double photoUploadProgress = 0.0D;

    private ProgressBar progressBar;

    private DatabaseReference reference;

    private boolean scanned = false;

    private String status;

    private StorageReference storageReference;

    private int timerValue;

    private byte[] getByteFromBitmap(Bitmap paramBitmap, int paramInt) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        paramBitmap.compress(Bitmap.CompressFormat.JPEG, paramInt, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        Log.d("StartCarCleanActivity", "onActivityResult: ");
        Calendar calendar = Calendar.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.storageReference = FirebaseStorage.getInstance().getReference().child("cars/" + this.area + "/" + this.CarNumber + "/" + calendar.getTime());
        if (paramIntent != null) {
            if (paramInt1 == 29) {
                Log.d("StartCarCleanActivity", "onActivityResult: qr code is scanned");
                setTimer();
            }
            if (paramInt1 == 2) {
                Log.d("StartCarCleanActivity", "onActivityResult: image is captured by interior employee");
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                this.reference = databaseReference;
                databaseReference.child("Car Status").child(getIntent().getStringExtra(getString(R.string.carNumber))).child(this.status).setValue("scanned");
                this.reference.child("Car Status").child(getIntent().getStringExtra(getString(R.string.carNumber))).child("timeStamp").setValue(String.valueOf(System.currentTimeMillis()));
                this.reference.child("InteriorEmployee").child(FirebaseAuth.getInstance().getUid()).child("status").setValue("working");
                this.reference.child("InteriorEmployee").child(FirebaseAuth.getInstance().getUid()).child("working on").setValue(getIntent().getStringExtra(getString(R.string.carNumber)));
                this.reference.child("InteriorEmployees").child(area).child(FirebaseAuth.getInstance().getUid()).child("working on").setValue(getIntent().getStringExtra(getString(R.string.carNumber)));
                setTimer();
            }
        }
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_start_car_cleaning);
        Thread.setDefaultUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new CrashHandler(getApplicationContext()));
        if (Build.VERSION.SDK_INT > 9)
            StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder()).permitAll().build());
        Log.d("StartCarCleanActivity", "onCreate: called");
        DroidNet.init((Context)this);
        DroidNet droidNet = DroidNet.getInstance();
        this.droidNet = droidNet;
        droidNet.addInternetConnectivityListener(this);
        Scanner = findViewById(R.id.scan);
        this.back = (ImageView)findViewById(R.id.back);
        this.CarNumber = getIntent().getStringExtra(getString(R.string.carNumber));
        this.area = getIntent().getStringExtra(getString(R.string.Area));
        this.carNumber = (TextView)findViewById(R.id.carNumber);
        this.carCharacteristics = (TextView)findViewById(R.id.carModel);
        this.carLocation = (TextView)findViewById(R.id.carLocation);
        this.carPhoto = (ImageView)findViewById(R.id.carPhoto);
        this.progressBar = (ProgressBar)findViewById(R.id.progressBar);
        this.OpenMap = (ImageView)findViewById(R.id.openMap);
        this.carColor = (TextView)findViewById(R.id.carColor);
        this.car_number = (TextView)findViewById(R.id.car_Number);
        this.CallOwner = (ImageView)findViewById(R.id.callOwner);
        this.carNumberHeading = (TextView)findViewById(R.id.carNumber);
        FirebaseDatabase.getInstance().getReference().child("Car Status/" + this.CarNumber + "/doneBy").setValue(FirebaseAuth.getInstance().getUid());
        if (getIntent().hasExtra("interior")) {
            Log.d("StartCarCleanActivity", "onCreate: employeeType : interior");
            this.status = "Interior Cleaning status";
            FirebaseDatabase.getInstance().getReference().child("InteriorEmployee").child(FirebaseAuth.getInstance().getUid()).child("daysCar").setValue(getIntent().getStringExtra("daysCar"));
        } else {
            this.status = "status";
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("cars").child(this.area);
        this.reference = databaseReference;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError param1DatabaseError) {
                Log.d("StartCarCleanActivity", " snapshot not available : " + param1DatabaseError.getMessage());
                Toast.makeText((Context)StartCarCleaningActivity.this, "Barcode cannot be verified", Toast.LENGTH_SHORT).show();
            }

            public void onDataChange(DataSnapshot param1DataSnapshot) {
                Log.d("StartCarCleanActivity", "onDataChange: creating car object with snapshot : " + param1DataSnapshot);
                try {
                    if (param1DataSnapshot.hasChild(StartCarCleaningActivity.this.CarNumber)) {
                        StringBuilder stringBuilder = new StringBuilder();
//                        this();
                        Log.d("StartCarCleanActivity", stringBuilder.append("onDataChange: got the snapshot : ").append(param1DataSnapshot).toString());
                        Car car = (Car)param1DataSnapshot.child(StartCarCleaningActivity.this.CarNumber).getValue(Car.class);
//                        StartCarCleaningActivity.access$102(StartCarCleaningActivity.this, car.getLocation());
//                        StartCarCleaningActivity.access$202(StartCarCleaningActivity.this, car.getMobileNo());
                        carLocation.setText(car.getLocation());
                        CarOwnerPhone = car.getMobileNo();

                        carNumber.setText(car.getNumber());
                        carNumberHeading.setText(car.getNumber());
                        CarNumber = car.getNumber();
                        car_number.setText(car.getNumber());
                        carColor.setText(car.getColor());
                        carCharacteristics.setText(car.getModel());
                        if (car.getCategory().toLowerCase().equals("hatchback")) {
                            timerValue=1;
                        } else if (car.getCategory().toLowerCase().equals("sedan") || car.getCategory().toLowerCase().equals("luv") || car.getCategory().toLowerCase().equals("compactsedan")) {
                            timerValue=1;
                        } else if (car.getCategory().toLowerCase().equals("suv")) {
                            timerValue=1;
                        }
                        StartCarCleaningActivity.this.setTimer();

                        carLocation.setText(car.getAddress());
                        Picasso.get().load(car.getPhoto()).transform(new RoundedTransformation(30, 0)).memoryPolicy(MemoryPolicy.NO_CACHE).into(carPhoto);
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (Exception exception) {
                    Log.d("StartCarCleanActivity", "onDataChange: got error while setting car model" + exception);
                    Toast.makeText((Context)StartCarCleaningActivity.this, "Error occurred. Unable to get Cars Data. Message :" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (ActivityCompat.checkSelfPermission((Context)this, this.PERMISSIONS[0]) != 0 || ActivityCompat.checkSelfPermission((Context)this, this.PERMISSIONS[1]) != 0 || ActivityCompat.checkSelfPermission((Context)this, this.PERMISSIONS[2]) != 0 || ActivityCompat.checkSelfPermission((Context)this, this.PERMISSIONS[3]) != 0) {
            Log.d("StartCarCleanActivity", "onCreate: permission asking , if not available previously");
            ActivityCompat.requestPermissions((Activity)this, this.PERMISSIONS, 1);
        }
        if (!this.Connected) {
            Toast.makeText((Context)this, "No INTERNET connection found. Check your connection and try again.", Toast.LENGTH_SHORT).show();
            if (getIntent().hasExtra("interior")) {
                startActivity((new Intent((Context)this, InteriorHomeActivity.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            } else {
                Log.d("StartCarCleanActivity", "onCreate: employeeType : exterior");
                startActivity((new Intent((Context)this, HomeActivity.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        }
        this.back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                StartCarCleaningActivity.this.finish();
            }
        });
        this.Scanner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (!StartCarCleaningActivity.this.Connected) {
                    Toast.makeText((Context)StartCarCleaningActivity.this, "No INTERNET connection found. Check your connection and try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (SystemClock.elapsedRealtime() - StartCarCleaningActivity.this.lastClicked < 1000L)
                    return;

                lastClicked = SystemClock.elapsedRealtime();
                Log.d("StartCarCleanActivity", "onClick: launching scanner after checking permissions");
                reference = FirebaseDatabase.getInstance().getReference().child("Car Status").child(carNumber.getText().toString()).child("status");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onCancelled(DatabaseError param2DatabaseError) {}

                    public void onDataChange(DataSnapshot param2DataSnapshot) {
                        if (ActivityCompat.checkSelfPermission((Context)StartCarCleaningActivity.this, StartCarCleaningActivity.this.PERMISSIONS[0]) == 0)
                            if (StartCarCleaningActivity.this.getIntent().hasExtra("interior")) {
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                StartCarCleaningActivity.this.startActivityForResult(intent, 2);
                            } else {
                                Intent intent = new Intent((Context)StartCarCleaningActivity.this, QRCodeScanning.class);
                                intent.putExtra(StartCarCleaningActivity.this.getString(R.string.carNumber), StartCarCleaningActivity.this.CarNumber);
                                intent.putExtra(StartCarCleaningActivity.this.getString(R.string.area), StartCarCleaningActivity.this.area);
                                StartCarCleaningActivity.this.startActivityForResult(intent, 29);
                            }
                    }
                });
            }
        });
        this.CallOwner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (SystemClock.elapsedRealtime() - StartCarCleaningActivity.this.lastClicked < 1000L)
                    return;
                lastClicked = SystemClock.elapsedRealtime();
                FirebaseDatabase.getInstance().getReference().child("Employee").child(FirebaseAuth.getInstance().getUid()).child("ContactNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onCancelled(DatabaseError param2DatabaseError) {}

                    public void onDataChange(DataSnapshot param2DataSnapshot) {
                        String str1 = null;
                        String str2 = param2DataSnapshot.getValue().toString();
                        HttpRequest httpRequest = new HttpRequest();
                        param2DataSnapshot = null;
                        try {
                            StringBuilder stringBuilder = new StringBuilder();
//                            this();
                            String str = httpRequest.run(stringBuilder.append("https://us-central1-cmoto-4267a.cloudfunctions.net/callCustomer?to=").append(StartCarCleaningActivity.this.CarOwnerPhone).append("&from=").append(str2).toString());
                            str1 = str;
                        } catch (IOException iOException) {
                            iOException.printStackTrace();
                        }
                        if (str1 != null)
                            Toast.makeText((Context)StartCarCleaningActivity.this, "Calling request has sent", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        this.OpenMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (!StartCarCleaningActivity.this.Connected) {
                    Toast.makeText((Context)StartCarCleaningActivity.this, "No INTERNET connection found. Check your connection and try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (SystemClock.elapsedRealtime() - StartCarCleaningActivity.this.lastClicked < 1000L)
                    return;
                lastClicked = SystemClock.elapsedRealtime();
                Intent map = new Intent(StartCarCleaningActivity.this, MapsActivity.class);
                Double latitude = Double.valueOf(location.substring(0,location.indexOf(",")));
                Double longitude = Double.valueOf(location.substring(location.indexOf(",")+1));
                map.putExtra(getString(R.string.latitude),latitude);
                map.putExtra(getString(R.string.longitude),longitude);
                startActivity(map);
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        this.droidNet.removeInternetConnectivityChangeListener(this);
    }

    public void onInternetConnectivityChanged(boolean paramBoolean) {
        if (paramBoolean) {
            Log.d("StartCarCleanActivity", "onInternetConnectivityChanged: INTERNET connected");
            this.Connected = true;
        } else {
            Log.d("StartCarCleanActivity", "onInternetConnectivityChanged: INTERNET lost");
            this.Connected = false;
            Toast.makeText((Context)this, "Internet Connection Lost", Toast.LENGTH_SHORT).show();
        }
    }

    public void setTimer() {
        Log.d("StartCarCleanActivity", "setTimer: entered setTimer function");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Car Status").child(this.CarNumber);
        this.reference = databaseReference;
        databaseReference.addValueEventListener(new ValueEventListener() {
            public void onCancelled(DatabaseError param1DatabaseError) {}

            public void onDataChange(DataSnapshot param1DataSnapshot) {
                if (param1DataSnapshot != null)
                    try {
                        if (!StartCarCleaningActivity.this.scanned) {
                            Intent intent;
//                            StartCarCleaningActivity.access$1702(StartCarCleaningActivity.this, true);
                            scanned = true;
                            long l = System.currentTimeMillis();
                            Double double_ = Double.valueOf(param1DataSnapshot.child("timeStamp").getValue().toString());
                            if (double_.doubleValue() == 0.0D)
                                scanned = true;
//                                StartCarCleaningActivity.access$1702(StartCarCleaningActivity.this, false);
                            StringBuilder stringBuilder = new StringBuilder();
//                            this();
                            Log.d("StartCarCleanActivity", stringBuilder.append("onDataChange: got the timestamp : ").append(double_).toString());
                            stringBuilder = new StringBuilder();
//                            this();
                            Log.d("StartCarCleanActivity", stringBuilder.append("onDataChange: timerValue : ").append(StartCarCleaningActivity.this.timerValue).toString());
                            double d1 = l;
                            double d2 = double_.doubleValue();
                            int i = StartCarCleaningActivity.this.timerValue;
                            if (d1 - d2 < (i * 60000)) {
                                stringBuilder = new StringBuilder();
//                                this();
                                Log.d("StartCarCleanActivity", stringBuilder.append("onDataChange: Timer is not complete timeStamp : ").append((l - double_.doubleValue()) / 60000.0D).toString());
                                stringBuilder = new StringBuilder();
//                                this();
                                Log.d("StartCarCleanActivity", stringBuilder.append("onDataChange: difference is ").append(StartCarCleaningActivity.this.timerValue - (l - double_.doubleValue()) / 60000.0D).toString());
                                float f = (float)(StartCarCleaningActivity.this.timerValue - (l - double_.doubleValue()) / 60000.0D);
                                intent = new Intent();
//                                this(timerActivity.class);
                                intent.putExtra(StartCarCleaningActivity.this.getString(R.string.carNumber), StartCarCleaningActivity.this.CarNumber);
                                intent.putExtra(StartCarCleaningActivity.this.getString(R.string.area), StartCarCleaningActivity.this.area);
                                if (StartCarCleaningActivity.this.getIntent().hasExtra("interior"))
                                    intent.putExtra("interior", "interior");
                                intent.putExtra("timeInMinutes", f);
                                intent.putExtra("finalTimeInMinutes", StartCarCleaningActivity.this.timerValue);
//                                StartCarCleaningActivity.this.startActivity(intent.addFlags(335544320));
                                StartCarCleaningActivity.this.finish();
                            } else {
                                stringBuilder = new StringBuilder();
//                                this();
                                Log.d("StartCarCleanActivity", stringBuilder.append("onDataChange: either timer is complete or not started ").toString());
                                if (d2 != 0.0D) {
                                    Log.d("StartCarCleanActivity", "onDataChange: timer is completed and sent to timer activity");
                                    float f = (float)(StartCarCleaningActivity.this.timerValue - (System.currentTimeMillis() - d2) / 60000.0D);
                                    intent = new Intent();
//                                    this(timerActivity.class);
                                    intent.putExtra("carNumber", StartCarCleaningActivity.this.CarNumber);
                                    intent.putExtra("area", StartCarCleaningActivity.this.area);
                                    intent.putExtra("timeInMinutes", f);
                                    intent.putExtra("finalTimeInMinutes", StartCarCleaningActivity.this.timerValue);
                                    if (StartCarCleaningActivity.this.getIntent().hasExtra("interior"))
                                        intent.putExtra("interior", "interior");
                                    StartCarCleaningActivity.this.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    StartCarCleaningActivity.this.finish();
                                }
                            }
                        }
                    } catch (Error error) {
                        Log.d("StartCarCleanActivity", "onDataChange: got the error while setting timer : " + error.getMessage());
                        Toast.makeText((Context)StartCarCleaningActivity.this, "Got the timer connection error. Contact Your Manager", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }
}
