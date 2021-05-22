package com.example.cmotoemployee.EmployeeActivities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.cmotoemployee.InteriorEmployeeActivities.InteriorHomeActivity;
import com.example.cmotoemployee.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadImagesActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 2;

    private static final int SCANNED_ACTIVITY_RESULT = 29;

    private static final String TAG = "UploadImagesActivity";

    private static final int VERIFY_PERMISSION_REQUEST_CODE = 1;

    public final String[] CAMERA_PERMISSION = new String[] { "android.permission.CAMERA" };

    public final String[] LOCATION_SERVICE_PERMISSION = new String[] { "android.permission.ACCESS_FINE_LOCATION" };

    private String area;

    private FirebaseAuth auth;

    private String carNumber;

    private String employeeType;

    private String employeesType;

    private boolean image1Uploaded = false;

    private boolean image2Uploaded = false;

    private boolean image3Uploaded = false;

    private boolean image4Uploaded = false;

    private long lastClicked = 0L;

    private LinearLayout loadingEffect;

    private int n = 0;

    private int photoUploadProgress = 0;

    private ProgressBar progressBar;

    private DatabaseReference reference;

    private String status;

    private StorageReference storageReference;

    private ImageView submit;

    private int timerValue;

    private ImageView uploadImage1;

    private ImageView uploadImage2;

    private ImageView uploadImage3;

    private ImageView uploadImage4;

    private String workHistory;

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

    private void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime();
        System.gc();
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

    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        Calendar calendar = Calendar.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.storageReference = FirebaseStorage.getInstance().getReference().child("cars/" + this.area + "/" + this.carNumber + "/" + calendar.getTime() + " photo " + this.n);
        if (paramInt2 != 0 && paramInt1 == 2) {
            Log.d("UploadImagesActivity", "onActivityResult: request code is for camera");
            if (this.n == 1) {
                this.image1Uploaded = true;
                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath(), "photo1.jpg"));
                this.uploadImage1.setImageURI(null);
                this.uploadImage1.setImageURI(uri);
            }
            if (this.n == 2) {
                this.image2Uploaded = true;
                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath(), "photo2.jpg"));
                this.uploadImage2.setImageURI(null);
                this.uploadImage2.setImageURI(uri);
            }
            if (this.n == 3) {
                this.image3Uploaded = true;
                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath(), "photo3.jpg"));
                this.uploadImage3.setImageURI(null);
                this.uploadImage3.setImageURI(uri);
            }
            if (this.n == 4) {
                this.image4Uploaded = true;
                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath(), "photo4.jpg"));
                this.uploadImage4.setImageURI(null);
                this.uploadImage4.setImageURI(uri);
            }
            this.n++;
        }
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        StrictMode.setVmPolicy((new StrictMode.VmPolicy.Builder()).build());
        setContentView(R.layout.activity_upload_images);
        this.uploadImage1 = (ImageView)findViewById(R.id.uploadImage1);
        this.uploadImage2 = (ImageView)findViewById(R.id.uploadImage2);
        this.uploadImage3 = (ImageView)findViewById(R.id.uploadImage3);
        this.uploadImage4 = (ImageView)findViewById(R.id.uploadImage4);
        this.progressBar = (ProgressBar)findViewById(R.id.progressBar);
        this.loadingEffect = (LinearLayout)findViewById(R.id.loadingEffect);
        this.submit = (ImageView)findViewById(R.id.submit);
        this.progressBar.setVisibility(View.GONE);
        this.carNumber = getIntent().getStringExtra("carNumber");
        this.area = getIntent().getStringExtra("area");
        if (getIntent().hasExtra("interior")) {
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
        this.submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (!UploadImagesActivity.this.image1Uploaded && !UploadImagesActivity.this.image2Uploaded && !UploadImagesActivity.this.image3Uploaded && !UploadImagesActivity.this.image4Uploaded) {
                    Toast.makeText((Context)UploadImagesActivity.this, "Upload all images", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (UploadImagesActivity.this.getIntent().hasExtra("interior")) {
                    UploadImagesActivity.this.startActivity((new Intent((Context)UploadImagesActivity.this, InteriorHomeActivity.class)).putExtra("interior", "interior"));
                } else {
                    UploadImagesActivity.this.startActivity(new Intent((Context)UploadImagesActivity.this, HomeActivity.class));
                }
                UploadImagesActivity.this.finish();
            }
        });
        this.uploadImage1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (SystemClock.elapsedRealtime() - UploadImagesActivity.this.lastClicked < 1000L)
                    return;
                lastClicked = SystemClock.elapsedRealtime();
                if (UploadImagesActivity.this.image1Uploaded) {
                    Toast.makeText((Context)UploadImagesActivity.this, "This image is uploaded", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("UploadImagesActivity", "onClick: launching camera after checking permissions");
                reference = FirebaseDatabase.getInstance().getReference().child("Car Status").child(UploadImagesActivity.this.getIntent().getStringExtra("carNumber"));
                UploadImagesActivity.this.reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onCancelled(DatabaseError param2DatabaseError) {}

                    public void onDataChange(DataSnapshot param2DataSnapshot) {
                        long l = System.currentTimeMillis();
                        Double double_ = Double.valueOf(param2DataSnapshot.child("timeStamp").getValue().toString());
                        if (l - double_.doubleValue() < (UploadImagesActivity.this.timerValue * 60000)) {
                            Log.d("UploadImagesActivity", "onDataChange: Timer is not complete timeStamp : " + ((l - double_.doubleValue()) / 60000.0D));
                        } else if (ActivityCompat.checkSelfPermission((Context)UploadImagesActivity.this, UploadImagesActivity.this.CAMERA_PERMISSION[0]) == 0) {
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            intent.putExtra("output", (Parcelable)Uri.parse("file:///sdcard/photo1.jpg"));
                            intent.putExtra(UploadImagesActivity.this.getString(R.string.carNumber), UploadImagesActivity.this.getIntent().getStringExtra("carNumber").toString());
                            UploadImagesActivity.this.startActivityForResult(intent, 2);
                        }
                    }
                });
            }
        });
        this.uploadImage2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (SystemClock.elapsedRealtime() - UploadImagesActivity.this.lastClicked < 1000L)
                    return;
                lastClicked = SystemClock.elapsedRealtime();
                if (UploadImagesActivity.this.image2Uploaded) {
                    Toast.makeText((Context)UploadImagesActivity.this, "This image is uploaded", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("UploadImagesActivity", "onClick: launching camera after checking permissions");
                reference = FirebaseDatabase.getInstance().getReference().child("Car Status").child(UploadImagesActivity.this.getIntent().getStringExtra("carNumber").toString());
                UploadImagesActivity.this.reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onCancelled(DatabaseError param2DatabaseError) {}

                    public void onDataChange(DataSnapshot param2DataSnapshot) {
                        long l = System.currentTimeMillis();
                        Double double_ = Double.valueOf(param2DataSnapshot.child("timeStamp").getValue().toString());
                        if (l - double_.doubleValue() < (UploadImagesActivity.this.timerValue * 60000)) {
                            Log.d("UploadImagesActivity", "onDataChange: Timer is not complete timeStamp : " + ((l - double_.doubleValue()) / 60000.0D));
                        } else if (ActivityCompat.checkSelfPermission((Context)UploadImagesActivity.this, UploadImagesActivity.this.CAMERA_PERMISSION[0]) == 0) {
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            intent.putExtra("output", (Parcelable)Uri.parse("file:///sdcard/photo2.jpg"));
                            intent.putExtra(UploadImagesActivity.this.getString(R.string.carNumber), UploadImagesActivity.this.getIntent().getStringExtra("carNumber").toString());
                            UploadImagesActivity.this.startActivityForResult(intent, 2);
                        }
                    }
                });
            }
        });
        this.uploadImage3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (SystemClock.elapsedRealtime() - UploadImagesActivity.this.lastClicked < 1000L)
                    return;
                lastClicked = SystemClock.elapsedRealtime();
                if (UploadImagesActivity.this.image3Uploaded) {
                    Toast.makeText((Context)UploadImagesActivity.this, "This image is uploaded", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("UploadImagesActivity", "onClick: launching camera after checking permissions");
                reference = FirebaseDatabase.getInstance().getReference().child("Car Status").child(UploadImagesActivity.this.getIntent().getStringExtra("carNumber").toString());
                UploadImagesActivity.this.reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onCancelled(DatabaseError param2DatabaseError) {}

                    public void onDataChange(DataSnapshot param2DataSnapshot) {
                        long l = System.currentTimeMillis();
                        Double double_ = Double.valueOf(param2DataSnapshot.child("timeStamp").getValue().toString());
                        if (l - double_.doubleValue() < (UploadImagesActivity.this.timerValue * 60000)) {
                            Log.d("UploadImagesActivity", "onDataChange: Timer is not complete timeStamp : " + ((l - double_.doubleValue()) / 60000.0D));
                        } else if (ActivityCompat.checkSelfPermission((Context)UploadImagesActivity.this, UploadImagesActivity.this.CAMERA_PERMISSION[0]) == 0) {
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            intent.putExtra("output", (Parcelable)Uri.parse("file:///sdcard/photo3.jpg"));
                            intent.putExtra(UploadImagesActivity.this.getString(R.string.carNumber), UploadImagesActivity.this.getIntent().getStringExtra("carNumber").toString());
                            UploadImagesActivity.this.startActivityForResult(intent, 2);
                        }
                    }
                });
            }
        });
        this.uploadImage4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (SystemClock.elapsedRealtime() - UploadImagesActivity.this.lastClicked < 1000L)
                    return;
                lastClicked = SystemClock.elapsedRealtime();
                if (UploadImagesActivity.this.image4Uploaded) {
                    Toast.makeText((Context)UploadImagesActivity.this, "This image is uploaded", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("UploadImagesActivity", "onClick: launching camera after checking permissions");
                reference = FirebaseDatabase.getInstance().getReference().child("Car Status").child(UploadImagesActivity.this.getIntent().getStringExtra("carNumber").toString());
                UploadImagesActivity.this.reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onCancelled(DatabaseError param2DatabaseError) {}

                    public void onDataChange(DataSnapshot param2DataSnapshot) {
                        long l = System.currentTimeMillis();
                        Double double_ = Double.valueOf(param2DataSnapshot.child("timeStamp").getValue().toString());
                        if (l - double_.doubleValue() < (UploadImagesActivity.this.timerValue * 60000)) {
                            Log.d("UploadImagesActivity", "onDataChange: Timer is not complete timeStamp : " + ((l - double_.doubleValue()) / 60000.0D));
                        } else if (ActivityCompat.checkSelfPermission((Context)UploadImagesActivity.this, UploadImagesActivity.this.CAMERA_PERMISSION[0]) == 0) {
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            intent.putExtra("output", (Parcelable)Uri.parse("file:///sdcard/photo4.jpg"));
                            intent.putExtra(UploadImagesActivity.this.getString(R.string.carNumber), UploadImagesActivity.this.getIntent().getStringExtra("carNumber").toString());
                            UploadImagesActivity.this.startActivityForResult(intent, 2);
                        }
                    }
                });
            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (this.n >= 1) {
            this.image1Uploaded = true;
            Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath(), "photo1.jpg"));
            this.uploadImage1.setImageURI(uri);
        }
        if (this.n >= 2) {
            this.image2Uploaded = true;
            Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath(), "photo2.jpg"));
            this.uploadImage2.setImageURI(uri);
        }
        if (this.n >= 3) {
            this.image3Uploaded = true;
            Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath(), "photo3.jpg"));
            this.uploadImage3.setImageURI(uri);
        }
        if (this.n >= 4) {
            this.image4Uploaded = true;
            Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath(), "photo4.jpg"));
            this.uploadImage4.setImageURI(uri);
            uploadImages();
        }
    }

    public void uploadImages() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.loadingEffect.setVisibility(View.VISIBLE);
        getWindow().setFlags(16, 16);
        Uri uri1 = Uri.parse("file:///sdcard/photo1.jpg");
        Uri uri2 = Uri.parse("file:///sdcard/photo2.jpg");
        Uri uri3 = Uri.parse("file:///sdcard/photo3.jpg");
        Uri uri4 = Uri.parse("file:///sdcard/photo4.jpg");
        Bitmap[] arrayOfBitmap = new Bitmap[4];
        try {
            Bitmap bitmap1 = modifyOrientation(MediaStore.Images.Media.getBitmap(getContentResolver(), uri1), uri1);
            Bitmap bitmap2 = modifyOrientation(MediaStore.Images.Media.getBitmap(getContentResolver(), uri2), uri2);
            Bitmap bitmap3 = modifyOrientation(MediaStore.Images.Media.getBitmap(getContentResolver(), uri3), uri3);
            Bitmap bitmap4 = modifyOrientation(MediaStore.Images.Media.getBitmap(getContentResolver(), uri4), uri4);
            int i = (int)(bitmap1.getWidth() * 0.3D);
            int j = bitmap1.getHeight();
            j = (int)(j * 0.3D);
            arrayOfBitmap[0] = Bitmap.createScaledBitmap(bitmap1, i, j, true);
            arrayOfBitmap[1] = Bitmap.createScaledBitmap(bitmap2, (int)(bitmap2.getWidth() * 0.3D), (int)(bitmap2.getHeight() * 0.3D), true);
            i = (int)(bitmap3.getWidth() * 0.3D);
            j = bitmap3.getHeight();
            Bitmap[] arrayOfBitmap1 = arrayOfBitmap;
            j = (int)(j * 0.3D);
            arrayOfBitmap1[2] = Bitmap.createScaledBitmap(bitmap3, i, j, true);
            arrayOfBitmap1[3] = Bitmap.createScaledBitmap(bitmap4, (int)(bitmap4.getWidth() * 0.3D), (int)(bitmap4.getHeight() * 0.3D), true);
            bitmap1.recycle();
            bitmap2.recycle();
            bitmap3.recycle();
            bitmap4.recycle();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

    }
}

