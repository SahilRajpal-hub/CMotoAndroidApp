package com.example.cmotoemployee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.droidnet.DroidNet;
import com.example.cmotoemployee.EmployeeActivities.MapsActivity;
import com.example.cmotoemployee.Model.Car;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.io.IOException;

public class CollectPaymentActivity extends AppCompatActivity {
    private static final String TAG = "CollectPaymentActivity";

    private ImageView CallOwner;

    private String CarNumber = "";

    private String CarOwnerPhone = "";

    private TextView Collect;

    private boolean Connected = true;

    private ImageView OpenMap;

    private String area = "";

    private FirebaseAuth auth;

    private ImageView back;

    private Button button;

    private TextView carColor;

    private TextView carLocation;

    private TextView carModel;

    private TextView carNumber;

    private TextView carNumberHeading;

    private ImageView carPhoto;

    private TextView car_number;

    private DroidNet droidNet;

    private long lastClicked = 0L;

    private String location = "";

    private String mVerificationId = "";

    private Integer price = Integer.valueOf(0);

    private ProgressBar progressBar;

    private DatabaseReference reference;

    private DatabaseReference storageReference;

    @SuppressLint("ResourceType")
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(2131492892);
        this.carNumber = (TextView)findViewById(2131296382);
        this.Collect = (TextView)findViewById(2131296410);
        this.carColor = (TextView)findViewById(2131296376);
        this.carNumberHeading = (TextView)findViewById(2131296382);
        this.car_number = (TextView)findViewById(2131296385);
        this.carModel = (TextView)findViewById(2131296380);
        this.carPhoto = (ImageView)findViewById(2131296383);
        this.OpenMap = (ImageView)findViewById(2131296604);
        this.back = (ImageView)findViewById(2131296351);
        this.CallOwner = (ImageView)findViewById(2131296370);
        this.progressBar = (ProgressBar)findViewById(2131296631);
        this.carLocation = (TextView)findViewById(2131296379);
        this.auth = FirebaseAuth.getInstance();
        this.storageReference = FirebaseDatabase.getInstance().getReference();
        this.area = "";
        this.area = getIntent().getStringExtra("area");
        this.price = Integer.valueOf(getIntent().getIntExtra("price", 0));
        this.CarNumber = getIntent().getStringExtra("carNumber");
        this.back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                CollectPaymentActivity.this.finish();
            }
        });
        this.OpenMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (SystemClock.elapsedRealtime() - CollectPaymentActivity.this.lastClicked < 1000L)
                    return;
//                CollectPaymentActivity.access$002(CollectPaymentActivity.this, SystemClock.elapsedRealtime());
                Intent intent = new Intent((Context)CollectPaymentActivity.this, MapsActivity.class);
                Double double_2 = Double.valueOf(CollectPaymentActivity.this.location.substring(0, CollectPaymentActivity.this.location.indexOf(",")));
                Double double_1 = Double.valueOf(CollectPaymentActivity.this.location.substring(CollectPaymentActivity.this.location.indexOf(",") + 1));
                intent.putExtra(CollectPaymentActivity.this.getString(2131886158), double_2);
                intent.putExtra(CollectPaymentActivity.this.getString(2131886162), double_1);
                CollectPaymentActivity.this.startActivity(intent);
            }
        });
        this.CallOwner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (SystemClock.elapsedRealtime() - CollectPaymentActivity.this.lastClicked < 1000L)
                    return;
                lastClicked = SystemClock.elapsedRealtime();
//                CollectPaymentActivity.access$002(CollectPaymentActivity.this, SystemClock.elapsedRealtime());
                FirebaseDatabase.getInstance().getReference().child("Employee").child(FirebaseAuth.getInstance().getUid()).child("ContactNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onCancelled(DatabaseError param2DatabaseError) {}

                    public void onDataChange(DataSnapshot param2DataSnapshot) {
                        String str1 = null;
                        String str2 = param2DataSnapshot.getValue().toString();
                        HttpsRequest httpsRequest = new HttpsRequest();
                        param2DataSnapshot = null;
                        try {
                            StringBuilder stringBuilder = new StringBuilder();

                            String str = httpsRequest.run(stringBuilder.append("https://us-central1-cmoto-4267a.cloudfunctions.net/callCustomer?to=").append(CollectPaymentActivity.this.CarOwnerPhone).append("&from=").append(str2).toString());
                            str1 = str;
                        } catch (IOException iOException) {
                            iOException.printStackTrace();
                        }
                        if (str1 != null)
                            Toast.makeText((Context)CollectPaymentActivity.this, "Calling request has sent", 0).show();
                    }
                });
            }
        });
        this.Collect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (SystemClock.elapsedRealtime() - CollectPaymentActivity.this.lastClicked < 1000L)
                    return;
                lastClicked = SystemClock.elapsedRealtime();
//                CollectPaymentActivity.access$002(CollectPaymentActivity.this, SystemClock.elapsedRealtime());
                CollectPaymentActivity.this.startActivity((new Intent((Context)CollectPaymentActivity.this, OtpProcessingActivity.class)).putExtra("carNumber", CollectPaymentActivity.this.CarNumber));
            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("cars").child(this.area);
        this.reference = databaseReference;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError param1DatabaseError) {
                Log.d("CollectPaymentActivity", " snapshot not available : " + param1DatabaseError.getMessage());
                Toast.makeText((Context)CollectPaymentActivity.this, "Barcode cannot be verified", 0).show();
            }

            public void onDataChange(DataSnapshot param1DataSnapshot) {
                Log.d("CollectPaymentActivity", "onDataChange: creating car object with snapshot : " + param1DataSnapshot);
                try {
                    if (param1DataSnapshot.hasChild(CollectPaymentActivity.this.CarNumber)) {
                        StringBuilder stringBuilder = new StringBuilder();
//                        this();
                        Log.d("CollectPaymentActivity", stringBuilder.append("onDataChange: got the snapshot : ").append(param1DataSnapshot).toString());
                        Car car = (Car)param1DataSnapshot.child(CollectPaymentActivity.this.CarNumber).getValue(Car.class);
//                        CollectPaymentActivity.access$102(CollectPaymentActivity.this, car.getLocation());
//                        CollectPaymentActivity.access$202(CollectPaymentActivity.this, car.getMobileNo());
                        CollectPaymentActivity.this.carLocation.setText(car.getAddress());
                        CollectPaymentActivity.this.carNumber.setText(car.getNumber());
                        CollectPaymentActivity.this.carNumberHeading.setText(car.getNumber());
//                        CollectPaymentActivity.access$302(CollectPaymentActivity.this, car.getNumber());
                        CollectPaymentActivity.this.car_number.setText(car.getNumber());
                        CollectPaymentActivity.this.carColor.setText(car.getColor());
                        CollectPaymentActivity.this.carModel.setText(car.getModel());
                        RequestCreator requestCreator = Picasso.get().load(car.getPhoto());
//                        RoundedTransformation roundedTransformation = new RoundedTransformation();
//                        this(30, 0);
//                        requestCreator.transform(roundedTransformation).memoryPolicy(MemoryPolicy.NO_CACHE, new MemoryPolicy[0]).into(CollectPaymentActivity.this.carPhoto);
                        CollectPaymentActivity.this.progressBar.setVisibility(View.VISIBLE);
                    }
                } catch (Exception exception) {
                    Log.d("CollectPaymentActivity", "onDataChange: got error while setting car model" + exception);
                    Toast.makeText((Context)CollectPaymentActivity.this, "Error occurred. Unable to get Cars Data. Message :" + exception.getMessage(), 0).show();
                }
            }
        });
    }
}

