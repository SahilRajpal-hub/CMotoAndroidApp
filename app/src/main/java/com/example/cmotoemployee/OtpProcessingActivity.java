package com.example.cmotoemployee;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cmotoemployee.InteriorEmployeeActivities.InteriorHomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OtpProcessingActivity extends AppCompatActivity {
    public static final Integer CAMERA_REQUEST_CODE = Integer.valueOf(101);

    private static final String TAG = "OtpProcessingActivity";

    private FirebaseAuth auth;

    private TextView captureCash;

    private String carNumber;

    private LinearLayout loadingEffect;

    private TextInputEditText otp;

    private ProgressBar progressBar;

    private DatabaseReference reference;

    private StorageReference storageReference;

    private TextView verifyOtp;

    private void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime();
        System.gc();
    }

    private void uploadImage() throws IOException {
        this.progressBar.setVisibility(0);
        this.loadingEffect.setVisibility(0);
        getWindow().setFlags(16, 16);
        String str1 = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(Calendar.getInstance().getTime());
        Calendar.getInstance();
        final String date = (new SimpleDateFormat("yyyy-MM-dd")).format(Calendar.getInstance().getTime());
        Uri uri = Uri.parse("file:///sdcard/photo1.jpg");
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
        bitmap.recycle();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("payments/" + this.carNumber + "/" + str1);
        this.storageReference = storageReference;
        storageReference.putBytes(arrayOfByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            public void onSuccess(UploadTask.TaskSnapshot param1TaskSnapshot) {
                OtpProcessingActivity.this.storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    public void onSuccess(Uri param2Uri) {
                        String str = param2Uri.toString();
                        OtpProcessingActivity.this.reference.child("Car Status").child(OtpProcessingActivity.this.carNumber).child("lastPaidOn").setValue(date);
                        OtpProcessingActivity.this.reference.child("Car Status").child(OtpProcessingActivity.this.carNumber).child("Payment History").child(date).child("takenBy").setValue(OtpProcessingActivity.this.auth.getUid());
                        OtpProcessingActivity.this.reference.child("Car Status").child(OtpProcessingActivity.this.carNumber).child("Payment History").child(date).child("cashPhoto").setValue(str);
                        OtpProcessingActivity.this.freeMemory();
                        OtpProcessingActivity.this.progressBar.setVisibility(8);
                        OtpProcessingActivity.this.loadingEffect.setVisibility(8);
                        OtpProcessingActivity.this.getWindow().clearFlags(16);
                        OtpProcessingActivity.this.startActivity(new Intent((Context)OtpProcessingActivity.this, InteriorHomeActivity.class));
                        OtpProcessingActivity.this.finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception param1Exception) {
                OtpProcessingActivity.this.freeMemory();
                OtpProcessingActivity.this.progressBar.setVisibility(8);
                OtpProcessingActivity.this.loadingEffect.setVisibility(8);
                OtpProcessingActivity.this.getWindow().clearFlags(16);
                Toast.makeText((Context)OtpProcessingActivity.this, "Met an Error while uploading cash image", 0).show();
            }
        });
    }

    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        Log.d("OtpProcessingActivity", "onActivityResult: ");
        Log.d("OtpProcessingActivity", "requestCode: " + paramInt1);
        Log.d("OtpProcessingActivity", "resultCode: " + paramInt2);
        Log.d("OtpProcessingActivity", "data: " + paramIntent);
        if (paramInt1 == CAMERA_REQUEST_CODE.intValue()) {
            Log.d("OtpProcessingActivity", "onActivityResult: image is captured by interior employee");
            try {
                uploadImage();
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        StrictMode.setVmPolicy((new StrictMode.VmPolicy.Builder()).build());
        setContentView(2131492900);
        this.otp = (TextInputEditText)findViewById(2131296605);
        this.verifyOtp = (TextView)findViewById(2131296785);
        this.captureCash = (TextView)findViewById(2131296374);
        this.loadingEffect = (LinearLayout)findViewById(2131296535);
        this.progressBar = (ProgressBar)findViewById(2131296631);
        this.loadingEffect.setVisibility(8);
        this.progressBar.setVisibility(8);
        this.storageReference = FirebaseStorage.getInstance().getReference();
        this.reference = FirebaseDatabase.getInstance().getReference();
        this.auth = FirebaseAuth.getInstance();
        this.carNumber = getIntent().getStringExtra("carNumber");
        this.captureCash.setVisibility(8);
        this.verifyOtp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (OtpProcessingActivity.this.otp.getText().toString().equals("1234")) {
                    Toast.makeText((Context)OtpProcessingActivity.this, "Otp matched", 0).show();
                    OtpProcessingActivity.this.captureCash.setVisibility(0);
                } else {
                    Toast.makeText((Context)OtpProcessingActivity.this, "Otp doesn't match", 0).show();
                }
            }
        });
        this.captureCash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra("output", (Parcelable)Uri.parse("file:///sdcard/photo1.jpg"));
                intent.putExtra(OtpProcessingActivity.this.getString(2131886115), OtpProcessingActivity.this.getIntent().getStringExtra("carNumber").toString());
                OtpProcessingActivity.this.startActivityForResult(intent, OtpProcessingActivity.CAMERA_REQUEST_CODE.intValue());
            }
        });
    }
}
