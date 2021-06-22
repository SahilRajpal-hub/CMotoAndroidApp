package com.example.cmotoemployee.EmployeeActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.droidnet.DroidListener;
import com.droidnet.DroidNet;
import com.example.cmotoemployee.ErrorHandler.CrashHandler;
import com.example.cmotoemployee.InteriorEmployeeActivities.InteriorHomeActivity;
import com.example.cmotoemployee.Model.Car;
import com.example.cmotoemployee.R;
import com.example.cmotoemployee.RoundedTransformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.utils.L;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class StartCarCleaningActivity extends AppCompatActivity implements DroidListener {
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int IMAGE_REQUEST_CODE = 21;

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

    private LinearLayout loadingEffect;

    private ImageView back;

    private TextView carCharacteristics;

    private TextView carColor;

    private TextView carLocation;

    private TextView carNumber;

    private TextView carNumberHeading;

    private ImageView carPhoto;

    private TextView car_number;

    private DroidNet droidNet;

//    private IntentIntegrator integrator;

    private long lastClicked = 0L;

    private String location;

    private double photoUploadProgress = 0.0D;

    private ProgressBar progressBar;

    private DatabaseReference reference;

    private boolean scanned = true;

    private String status;

    private StorageReference storageReference;

    private int timerValue;

    private byte[] getByteFromBitmap(Bitmap paramBitmap, int paramInt) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        paramBitmap.compress(Bitmap.CompressFormat.JPEG, paramInt, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private String getRealPathFromURI(Uri paramUri) {
        String str;
        Cursor cursor = getContentResolver().query(paramUri, null, null, null, null);
        if (cursor == null) {
            str = paramUri.getPath();
        } else {
            cursor.moveToFirst();
            str = cursor.getString(cursor.getColumnIndex("_data"));
            cursor.close();
        }
        return str;
    }

    public static Bitmap rotate(Bitmap paramBitmap, float paramFloat) {
        Matrix matrix = new Matrix();
        matrix.postRotate(paramFloat);
        return Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap paramBitmap, boolean paramBoolean1, boolean paramBoolean2) {
        float f2;
        Matrix matrix = new Matrix();
        float f1 = -1.0F;
        if (paramBoolean1) {
            f2 = -1.0F;
        } else {
            f2 = 1.0F;
        }
        if (!paramBoolean2)
            f1 = 1.0F;
        matrix.preScale(f2, f1);
        return Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), matrix, true);
    }

    public Bitmap modifyOrientation(Bitmap paramBitmap, Uri paramUri) {
        Log.d("UploadImagesActivity", "modifyOrientation: Absolute path : " + paramUri);
        try {
            ExifInterface exifInterface = new ExifInterface(getRealPathFromURI(paramUri));
//            this(getRealPathFromURI(paramUri));
            int i = exifInterface.getAttributeInt("Orientation", 1);
            StringBuilder stringBuilder = new StringBuilder();
//            this();
            Log.d("UploadImagesActivity", stringBuilder.append("modifyOrientation: orientation got:").append(i).toString());
            return (i != 2) ? ((i != 3) ? ((i != 4) ? ((i != 6) ? ((i != 8) ? paramBitmap : rotate(paramBitmap, 270.0F)) : rotate(paramBitmap, 90.0F)) : flip(paramBitmap, false, true)) : rotate(paramBitmap, 180.0F)) : flip(paramBitmap, true, false);
        } catch (Exception exception) {
            Log.d("UploadImagesActivity", "modifyOrientation: error : " + exception.toString());
            exception.printStackTrace();
            return paramBitmap;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("StartCarCleanActivity", "onActivityResult: " + requestCode + " " + resultCode + " " + data);
        Calendar calendar = Calendar.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.storageReference = FirebaseStorage.getInstance().getReference().child("cars/" + this.area + "/" + this.CarNumber + "/photo" + calendar.getTime());
        if (data != null) {
            if (requestCode == SCANNED_ACTIVITY_RESULT) {
                Log.d("StartCarCleanActivity", "onActivityResult: qr code is scanned");
                if (resultCode == Activity.RESULT_OK) {
                    scanned = true;
                    setTimer();
                }

            }
            if (requestCode == CAMERA_REQUEST_CODE) {
                Log.d("StartCarCleanActivity", "onActivityResult: image is captured by interior employee");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                reference.child("Car Status").child(getIntent().getStringExtra(getString(R.string.carNumber))).child(this.status).setValue("scanned");
                reference.child("Car Status").child(getIntent().getStringExtra(getString(R.string.carNumber))).child("timeStamp").setValue(String.valueOf(System.currentTimeMillis()));
                reference.child("InteriorEmployee").child(FirebaseAuth.getInstance().getUid()).child("status").setValue("working");
                reference.child("InteriorEmployee").child(FirebaseAuth.getInstance().getUid()).child("working on").setValue(getIntent().getStringExtra(getString(R.string.carNumber)));
                reference.child("InteriorEmployees").child(area).child(FirebaseAuth.getInstance().getUid()).child("working on").setValue(getIntent().getStringExtra(getString(R.string.carNumber)));
                scanned = true;
                setTimer();
            }
        }

            if(requestCode == IMAGE_REQUEST_CODE && resultCode!=0){
                Log.d("UploadImagesActivity", "onActivityResult: request code is for camera");
                this.progressBar.setVisibility(View.VISIBLE);
                this.loadingEffect.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Uri uri1 = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath(), "carPhoto.jpg"));

                Bitmap bitmap = null;
                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri1);
                    bitmap = modifyOrientation(MediaStore.Images.Media.getBitmap(getContentResolver(), uri1), uri1);
                    int side = Math.min(bitmap.getHeight(),bitmap.getWidth());
                    bitmap = Bitmap.createScaledBitmap(bitmap,side,side,true);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                byte[] bytes = getByteFromBitmap(bitmap, 70);
                UploadTask uploadTask = null;
                uploadTask = storageReference.putBytes(bytes);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String URI = uri.toString();
                                try {
                                    FirebaseDatabase.getInstance().getReference().child("cars/"+area+"/"+getIntent().getStringExtra(getString(R.string.carNumber))+"/photo").setValue(URI);
                                    Picasso.get().load(URI).transform(new RoundedTransformation(30, 0)).memoryPolicy(MemoryPolicy.NO_CACHE).into(carPhoto);
                                    Toast.makeText(StartCarCleaningActivity.this, "Photo updated", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Log.d(TAG, "onSuccess: got error after uploading" + e.getMessage());
                                    Toast.makeText(StartCarCleaningActivity.this, "error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {

                        progressBar.setVisibility(View.GONE);
                        loadingEffect.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                        Toast.makeText(StartCarCleaningActivity.this, "Uploading Images", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                        progressBar.setVisibility(View.GONE);
                        loadingEffect.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
            }

    }

    protected void onCreate(Bundle paramBundle) {
        StrictMode.setVmPolicy((new StrictMode.VmPolicy.Builder()).build());
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
        this.loadingEffect = (LinearLayout)findViewById(R.id.loadingEffect);
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
                        } else if(car.getCategory().toLowerCase().equals("bike/scooty")) {
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

        carPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra("output", (Parcelable) Uri.parse("file:///sdcard/carPhoto.jpg"));
                intent.putExtra(StartCarCleaningActivity.this.getString(R.string.carNumber), StartCarCleaningActivity.this.getIntent().getStringExtra("carNumber").toString());
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
                return false;
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
//                Intent map = new Intent(StartCarCleaningActivity.this, MapsActivity.class);
//                Double latitude = Double.valueOf(location.substring(0,location.indexOf(",")));
//                Double longitude = Double.valueOf(location.substring(location.indexOf(",")+1));
//                map.putExtra(getString(R.string.latitude),latitude);
//                map.putExtra(getString(R.string.longitude),longitude);
//                startActivity(map);
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



    @Override
    protected void onResume() {
        super.onResume();

    }

    public void setTimer() {
        Log.d("StartCarCleanActivity", "setTimer: entered setTimer function");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Car Status").child(getIntent().getStringExtra(getString(R.string.carNumber)));

        databaseReference.addValueEventListener(new ValueEventListener() {
            public void onCancelled(DatabaseError param1DatabaseError) {}

            public void onDataChange(DataSnapshot param1DataSnapshot) {
                if (param1DataSnapshot != null)
                    try {
                        if (StartCarCleaningActivity.this.scanned) {
                            Intent intent;
//                            scanned = true;
                            long currentTimeStamp = System.currentTimeMillis();
                            Double timeStamp = Double.valueOf(param1DataSnapshot.child("timeStamp").getValue().toString());
//                            if (timeStamp == 0)
//                                scanned = true;
                            Log.d("StartCarCleanActivity","onDataChange: got the timestamp : "+timeStamp);
                            Log.d("StartCarCleanActivity", "onDataChange: timerValue : "+timerValue);

                            int i = StartCarCleaningActivity.this.timerValue;
                            if (currentTimeStamp - timeStamp < (i * 60000)) {
                                Log.d("StartCarCleanActivity", "onDataChange: Timer is not complete timeStamp : " + (currentTimeStamp - timeStamp) / 60000);
                                Log.d("StartCarCleanActivity", "onDataChange: difference is " + (timerValue - (currentTimeStamp - timeStamp) / 60000));
                                float f = (float)(StartCarCleaningActivity.this.timerValue - (currentTimeStamp - timeStamp) / 60000.0D);
                                intent = new Intent(StartCarCleaningActivity.this,timerActivity.class);

                                intent.putExtra(StartCarCleaningActivity.this.getString(R.string.carNumber), StartCarCleaningActivity.this.CarNumber);
                                intent.putExtra(StartCarCleaningActivity.this.getString(R.string.area), StartCarCleaningActivity.this.area);
                                if (StartCarCleaningActivity.this.getIntent().hasExtra("interior"))
                                    intent.putExtra("interior", "interior");
                                intent.putExtra("timeInMinutes", f);
                                intent.putExtra("finalTimeInMinutes", StartCarCleaningActivity.this.timerValue);
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                StartCarCleaningActivity.this.finish();
                            } else {
                                Log.d("StartCarCleanActivity", "onDataChange: either timer is complete or not started ");
                                if (timeStamp != 0) {
                                    Log.d("StartCarCleanActivity", "onDataChange: timer is completed and sent to timer activity");
                                    float f = (float)(StartCarCleaningActivity.this.timerValue - (System.currentTimeMillis() - timeStamp) / 60000.0D);
                                    intent = new Intent(StartCarCleaningActivity.this,timerActivity.class);
                                    intent.putExtra("carNumber", StartCarCleaningActivity.this.CarNumber);
                                    intent.putExtra("area", StartCarCleaningActivity.this.area);
                                    intent.putExtra("timeInMinutes", f);
                                    intent.putExtra("finalTimeInMinutes", StartCarCleaningActivity.this.timerValue);
                                    if (StartCarCleaningActivity.this.getIntent().hasExtra("interior"))
                                        intent.putExtra("interior", "interior");
                                    StartCarCleaningActivity.this.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
//                                    StartCarCleaningActivity.this.finish();
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
