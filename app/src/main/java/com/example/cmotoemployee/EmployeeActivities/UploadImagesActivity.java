package com.example.cmotoemployee.EmployeeActivities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.cmotoemployee.InteriorEmployeeActivities.InteriorHomeActivity;
import com.example.cmotoemployee.InteriorEmployeeActivities.InteriorPaymentActivity;
import com.example.cmotoemployee.R;
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

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
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

    private String employeeWorkHistory;

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
            this.employeeWorkHistory = "Interior Work History";
            this.employeesType = "InteriorEmployees";
            this.status = "Interior Cleaning status";
            this.workHistory = "Interior Work History";
        } else {
            this.employeeType = "Employee";
            this.employeesType = "Employees";
            this.employeeWorkHistory = "Work History";
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
                if (SystemClock.elapsedRealtime() - UploadImagesActivity.this.lastClicked < 1000)
                    return;
                lastClicked = SystemClock.elapsedRealtime();
                if (UploadImagesActivity.this.image1Uploaded) {
                    Toast.makeText((Context)UploadImagesActivity.this, "This image is uploaded", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("UploadImagesActivity", "onClick: launching camera after checking permissions" + getIntent().getStringExtra(getString(R.string.carNumber)));
                reference = FirebaseDatabase.getInstance().getReference().child("Car Status").child(getIntent().getStringExtra(getString(R.string.carNumber)));
                UploadImagesActivity.this.reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onCancelled(DatabaseError param2DatabaseError) {}

                    public void onDataChange(DataSnapshot param2DataSnapshot) {
                        long l = System.currentTimeMillis();
                        Double timeStamp = Double.valueOf(param2DataSnapshot.child("timeStamp").getValue().toString());
                        if (l - timeStamp < (timerValue * 60000)) {
                            Log.d("UploadImagesActivity", "onDataChange: Timer is not complete timeStamp : " + ((l - timeStamp) / 60000.0D));
                        } else if (ActivityCompat.checkSelfPermission((Context)UploadImagesActivity.this, UploadImagesActivity.this.CAMERA_PERMISSION[0]) == 0) {
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            intent.putExtra("output", (Parcelable)Uri.parse("file:///sdcard/photo1.jpg"));
                            intent.putExtra(UploadImagesActivity.this.getString(R.string.carNumber), UploadImagesActivity.this.getIntent().getStringExtra("carNumber").toString());
                            startActivityForResult(intent, 2);
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
                            UploadImagesActivity.this.startActivityForResult(intent, CAMERA_REQUEST_CODE);
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        Uri uri1 = Uri.parse("file:///sdcard/photo1.jpg");
        Uri uri2 = Uri.parse("file:///sdcard/photo2.jpg");
        Uri uri3 = Uri.parse("file:///sdcard/photo3.jpg");
        Uri uri4 = Uri.parse("file:///sdcard/photo4.jpg");
        Bitmap[] arrayOfBitmap = new Bitmap[4];
        try {

            Bitmap[] parts = new Bitmap[4];
            String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

            parts[0] = modifyOrientation(MediaStore.Images.Media.getBitmap(getContentResolver(), uri1), uri1);
            parts[1] = modifyOrientation(MediaStore.Images.Media.getBitmap(getContentResolver(), uri2), uri2);
            parts[2] = modifyOrientation(MediaStore.Images.Media.getBitmap(getContentResolver(), uri3), uri3);
            parts[3] = modifyOrientation(MediaStore.Images.Media.getBitmap(getContentResolver(), uri4), uri4);


            Bitmap result = Bitmap.createBitmap(parts[0].getWidth(), parts[0].getHeight() * 4, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setTextSize(12);

            for (int i = 0; i < parts.length; i++) {
                canvas.drawBitmap(parts[i], 0, parts[i].getHeight() * i, paint);
            }

            Bitmap bitmap = drawStringOnBitmap(result,date,new Point(200,200),Color.BLUE,60*scale);



            byte[] bytes = getByteFromBitmap(bitmap, 50);
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
                                reference = FirebaseDatabase.getInstance().getReference();
                                reference.child("Car Status").child(carNumber).child("doneBy").setValue(auth.getUid());
                                reference.child("Car Status").child(carNumber).child(employeeWorkHistory).child(date).child("doneBy").setValue(auth.getUid());
                                reference.child("Car Status").child(carNumber).child(employeeWorkHistory).child(date).child("Photo Url").setValue(URI);
                                reference.child("Car Status").child(carNumber).child(status).setValue("cleaned");
                                reference.child("Car Status").child(carNumber).child("timeStamp").setValue("0");
                                if (getIntent().hasExtra("interior")) {
                                    reference.child("Car Status").child(carNumber).child("lastCleanedInterior").setValue(date);
                                }
                                reference.child(employeeType).child(FirebaseAuth.getInstance().getUid()).child("status").setValue("free");
                                reference.child(employeeType).child(FirebaseAuth.getInstance().getUid()).child("working on").setValue("");
                                reference.child(employeesType).child(area).child(FirebaseAuth.getInstance().getUid()).child("working on").setValue("");
                                reference.child(employeeType).child(auth.getUid()).child(employeeWorkHistory).child(date).child(carNumber).child("time").setValue(Calendar.getInstance().getTime());
                                reference.child(employeeType).child(auth.getUid()).child(employeeWorkHistory).child(date).child(carNumber).child("Image Url " + n).setValue(URI).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        result.recycle();
                                        parts[0].recycle();
                                        parts[1].recycle();
                                        parts[2].recycle();
                                        parts[3].recycle();
                                        progressBar.setVisibility(View.GONE);
                                        loadingEffect.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        if (UploadImagesActivity.this.getIntent().hasExtra("interior")) {
                                            startActivity(new Intent(UploadImagesActivity.this,InteriorHomeActivity.class).putExtra("interior","InteriorEmployee").putExtra("reload","reload").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        } else {
                                            startActivity(new Intent(UploadImagesActivity.this,HomeActivity.class).putExtra("reload","reload").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                Log.d(TAG, "onSuccess: got error after uploading" + e.getMessage());
                                Toast.makeText(UploadImagesActivity.this, "error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {

                    Toast.makeText(UploadImagesActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                    result.recycle();
                    parts[0].recycle();
                    parts[1].recycle();
                    parts[2].recycle();
                    parts[3].recycle();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    result.recycle();
                    parts[0].recycle();
                    parts[1].recycle();
                    parts[2].recycle();
                    parts[3].recycle();
                    progressBar.setVisibility(View.GONE);
                    loadingEffect.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });





        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

    }

    public static Bitmap drawStringOnBitmap(Bitmap src, String string, Point location, int color, float size) {

        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        canvas.drawBitmap(src, 0, 0, null);

        paint.setColor(color);
//        paint.setAlpha(alpha);
        paint.setTextSize(size);
//        paint.setAntiAlias(true);
//        paint.setUnderlineText(underline);
        canvas.drawText(string, location.x, location.y, paint);

        return result;
    }

    private byte[] getByteFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        return stream.toByteArray();
    }

}

