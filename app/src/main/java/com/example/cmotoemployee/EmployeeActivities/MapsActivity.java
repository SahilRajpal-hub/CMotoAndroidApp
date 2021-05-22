package com.example.cmotoemployee.EmployeeActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.example.cmotoemployee.ErrorHandler.CrashHandler;
import com.example.cmotoemployee.InteriorEmployeeActivities.InteriorHomeActivity;
import com.example.cmotoemployee.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";

    private static final int VERIFY_PERMISSION_REQUEST_CODE = 134;

    public final String[] LOCATION_SERVICE_PERMISSION = new String[] { "android.permission.ACCESS_FINE_LOCATION" };

    private TextView addLocation;

    private Location currentLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private GoogleMap mMap;

    private ImageView marker;

    private void getDeviceLocation() {
        Log.d("MapsActivity", "getDeviceLocation: getting device present location");
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((Activity)this);
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        try {
            Task task = fusedLocationProviderClient.getLastLocation();
            OnCompleteListener onCompleteListener = new OnCompleteListener() {
                public void onComplete(Task param1Task) {
                    if (param1Task.isSuccessful()) {
                        Log.d("MapsActivity", "onComplete: location found " + param1Task.getResult());
//                        MapsActivity.access$102(MapsActivity.this, (Location)param1Task.getResult());
                        currentLocation = (Location)param1Task.getResult();
                        if (MapsActivity.this.currentLocation != null) {
                            MapsActivity.this.moveCamera(new LatLng(MapsActivity.this.currentLocation.getLatitude(), MapsActivity.this.currentLocation.getLongitude()), 15.0F);
                        } else {
                            Toast.makeText((Context)MapsActivity.this, "Try to refresh the map", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("MapsActivity", "onComplete: cant get location");
                        Toast.makeText((Context)MapsActivity.this, "unable to get current Location", Toast.LENGTH_SHORT).show();
                    }
                }
            };
//            super(this);
            task.addOnCompleteListener(onCompleteListener);
        } catch (SecurityException securityException) {
            Log.e("MapsActivity", "getDeviceLocation: got security error : " + securityException.getMessage());
        }
    }

    private void goToCarLocation() {
        Log.d("MapsActivity", "goToCarLocation: going to cars location");
        Double double_1 = Double.valueOf(getIntent().getDoubleExtra(getString(R.string.latitude), this.currentLocation.getLatitude()));
        Double double_2 = Double.valueOf(getIntent().getDoubleExtra(getString(R.string.longitude), this.currentLocation.getLongitude()));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(double_1.doubleValue(), double_2.doubleValue()));
        markerOptions.title("Car's Location");
        moveCamera(new LatLng(double_1.doubleValue(), double_2.doubleValue()), 15.0F);
        this.mMap.addMarker(markerOptions);
    }

    private void moveCamera(LatLng paramLatLng, float paramFloat) {
        Log.d("MapsActivity", "moveCamera: moving camera to loaction : " + paramLatLng);
        this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(paramLatLng, paramFloat));
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_maps);
        Thread.setDefaultUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new CrashHandler(getApplicationContext()));
        Log.d("MapsActivity", "onCreate: called for MapsActivity");
        this.marker = (ImageView)findViewById(R.id.marker);
//        TextView textView = (TextView)findViewById(R.id.);
//        this.addLocation = textView;
//        textView.setVisibility(8);
//        if (getIntent().hasExtra(getString(2131886149))) {
//            this.marker.setVisibility(8);
//            this.addLocation.setVisibility(0);
//        }
        this.marker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                MapsActivity.this.goToCarLocation();
            }
        });
        this.addLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                FirebaseDatabase.getInstance().getReference().child("cars").child(getIntent().getStringExtra(getString(R.string.Area)))
                        .child(getIntent().getStringExtra(getString(R.string.Society)))
                        .child(getIntent().getStringExtra("CarNumber")).child("Location").setValue(String.valueOf(currentLocation.getLatitude() + "," + currentLocation.getLongitude()));
                Intent intent = new Intent((Context)MapsActivity.this, InteriorHomeActivity.class);
                MapsActivity.this.startActivity(intent);
                MapsActivity.this.finish();
            }
        });
        ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    public void onMapReady(GoogleMap paramGoogleMap) {
        this.mMap = paramGoogleMap;
        if (ActivityCompat.checkSelfPermission((Context)this, "android.permission.ACCESS_FINE_LOCATION") != 0 && ActivityCompat.checkSelfPermission((Context)this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            Log.d("MapsActivity", "onCreate: permission asking , if not available previously");
            ActivityCompat.requestPermissions((Activity)this, this.LOCATION_SERVICE_PERMISSION, 134);
            return;
        }
        this.mMap.setMyLocationEnabled(true);
        this.mMap.getUiSettings().setMyLocationButtonEnabled(true);
        getDeviceLocation();
    }
}

