package com.example.cmotoemployee.EmployeeActivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.droidnet.DroidListener;
import com.droidnet.DroidNet;
import com.example.cmotoemployee.Adapter.RecyclerViewAdapter;
import com.example.cmotoemployee.Authentication.StartActivity;
import com.example.cmotoemployee.ErrorHandler.CrashHandler;
import com.example.cmotoemployee.Model.CarListItem;
import com.example.cmotoemployee.R;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.utils.L;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements DroidListener {
    private static final String TAG = "HomeActivity";

    private String Area = "";

    private String WorkingOn = "";

    private TextView aboutUs;

    private RecyclerViewAdapter adapter;

    private List<CarListItem> carItems;

    private TextView carList;

    private TextView carsCleaned;

    private int carsDone = 0;

    private List<String> carsModel;

    private HashMap<String, String> carsPhoto;

    private List<String> carsToBeWashed;
    private List<String> deactivatedCars;

    private TextView contactUs;

    private FirebaseDatabase database;

    private DroidNet droidNet;

    private ImageView exit;

    private ImageView faceBook;

    private List<Long> houseNumberList;

    private ImageView instagram;

    private boolean isConnected = true;

    private int lastClicked = 0;

    RecyclerView.LayoutManager layoutManager;

    private List<String> leaveTimeList;

    private ImageView linkedIn;

    private TextView logout;

    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseAuth mAuth;

    private RelativeLayout menu;

    private RelativeLayout menuClose;

    private ImageView menuOptions;

    private boolean menuVisible = false;

    private TextView name;

    private TextView paymentDetails;

    private TextView profile;

    private ImageView profileImage;

    private ProgressBar progressBar;

    private RecyclerView recyclerView;

    private DatabaseReference reference;

    private SwipeRefreshLayout refreshLayout;

    private TextView remainingCars;

    private ImageView scanner;

    private TextView termsAndCondition;

    private String todayCars = "";

    private Toolbar toolbar;

    private FirebaseUser user;

    private TextView workingComplete;

    public void menuVisibility(boolean paramBoolean) {
        if (!this.menuVisible) {
            this.menu.setVisibility(View.VISIBLE);
            TranslateAnimation translateAnimation = new TranslateAnimation(-this.menu.getWidth(), 0.0F, 0.0F, 0.0F);
            translateAnimation.setDuration(500L);
            translateAnimation.setFillAfter(true);
            this.menu.startAnimation((Animation)translateAnimation);
            this.menuClose.setVisibility(View.VISIBLE);
            this.menuVisible = true;
        } else {
            this.menu.setVisibility(View.GONE);
            TranslateAnimation translateAnimation = new TranslateAnimation(0.0F, -this.menu.getWidth(), 0.0F, 0.0F);
            translateAnimation.setDuration(500L);
            translateAnimation.setFillAfter(true);
            this.menu.startAnimation((Animation)translateAnimation);
            this.menuClose.setVisibility(View.GONE);
            translateAnimation.setFillAfter(false);
            this.menu.setClickable(false);
            this.menuVisible = false;
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_home);
        Thread.setDefaultUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new CrashHandler(getApplicationContext()));
        Log.d("HomeActivity", "onCreate: ");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        this.mAuth = firebaseAuth;
        this.user = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        this.database = firebaseDatabase;
        this.reference = firebaseDatabase.getReference();
        this.progressBar = (ProgressBar)findViewById(R.id.progressBar);
        this.menuOptions = (ImageView)findViewById(R.id.menu);
        this.paymentDetails = (TextView)findViewById(R.id.paymentsDetails);
        this.profile = (TextView)findViewById(R.id.profile);
        this.carList = (TextView)findViewById(R.id.carList);
        this.faceBook = (ImageView)findViewById(R.id.facebookLink);
        this.instagram = (ImageView)findViewById(R.id.instagramLink);
        this.linkedIn = (ImageView)findViewById(R.id.lunkedIn);
        this.termsAndCondition = (TextView)findViewById(R.id.termsAndCondition);
        this.contactUs = (TextView)findViewById(R.id.contactUs);
        this.aboutUs = (TextView)findViewById(R.id.aboutUs);
        this.logout = (TextView)findViewById(R.id.logout);
        this.progressBar.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        this.menu = (RelativeLayout)findViewById(R.id.Menu);
        this.exit = (ImageView)findViewById(R.id.exit);
        this.profileImage = (ImageView)findViewById(R.id.profileImage);
        this.name = (TextView)findViewById(R.id.name);
        this.menuClose = (RelativeLayout)findViewById(R.id.remainingBody);
        this.refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        this.scanner = (ImageView)findViewById(R.id.scanner);
        this.carsCleaned = (TextView)findViewById(R.id.done);
        this.remainingCars = (TextView)findViewById(R.id.remaining);
        this.workingComplete = (TextView)findViewById(R.id.workComplete);
        this.recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        this.carsToBeWashed = new ArrayList<>();
        this.deactivatedCars = new ArrayList<>();
        this.carsModel = new ArrayList<>();
        this.carsPhoto = new HashMap<>();
        this.houseNumberList = new ArrayList<>();
        this.leaveTimeList = new ArrayList<>();
        this.carItems = new ArrayList<>();
        this.todayCars = "";

//        AppUpdater appUpdater = new AppUpdater(this)
//                                    .setDisplay(Display.DIALOG)
//                .setUpdateXML("https://raw.githubusercontent.com/SahilRajpal-hub/appAutoUpdateXml/main/update.xml")
//                .showEvery(1).setTitleOnUpdateAvailable("Update available")
//                .setContentOnUpdateAvailable("Check out the latest version available of my app!")
//                .setTitleOnUpdateNotAvailable("Update not available")
//                .setContentOnUpdateNotAvailable("No update available. Check for updates again later!")
//                .setButtonUpdate("Update now?")
//                .setButtonDismiss("Maybe later")
//                .setButtonDoNotShowAgain("Huh, not interested");
//        appUpdater.start();


        try {
            DroidNet droidNet = DroidNet.getInstance();
            this.droidNet = droidNet;
            droidNet.addInternetConnectivityListener(this);
        } catch (Exception exception) {
            Log.e("HomeActivity", "onCreate: error : " + Arrays.toString(exception.getStackTrace()));
        }
        if (!this.isConnected)
            Toast.makeText((Context)this, "Internet connection lost", Toast.LENGTH_SHORT).show();
        this.reference.child("Employee").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            public void onCancelled(@NotNull DatabaseError param1DatabaseError) {}

            public void onDataChange(@NotNull DataSnapshot param1DataSnapshot) {
                @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                HomeActivity.this.name.setText(Objects.requireNonNull(param1DataSnapshot.child("Name").getValue()).toString());
                carsDone = (int) param1DataSnapshot.child("Work History").child(date).getChildrenCount();
                carsDone = carsDone==0 ? carsDone : carsDone-1;
            }
        });
        this.menuOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                HomeActivity.this.menuVisibility(true);
            }
        });
        this.exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                HomeActivity.this.menuVisibility(false);
            }
        });
        this.menuClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (HomeActivity.this.menuVisible)
                    HomeActivity.this.menuVisibility(false);
            }
        });
        this.paymentDetails.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (HomeActivity.this.menuVisible) {
                    HomeActivity.this.menuVisibility(false);
                    HomeActivity.this.startActivity(new Intent((Context)HomeActivity.this, PaymentActivity.class));
                }
            }
        });
        this.carList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (HomeActivity.this.menuVisible)
                    HomeActivity.this.menuVisibility(false);
            }
        });
        this.logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (HomeActivity.this.menuVisible) {
                    HomeActivity.this.menuVisibility(false);
                    HomeActivity.this.mAuth.signOut();
                    HomeActivity.this.startActivity(new Intent((Context)HomeActivity.this, StartActivity.class));
                    HomeActivity.this.finish();
                }
//                Toast.makeText(HomeActivity.this, "Just to check", Toast.LENGTH_SHORT).show();
            }
        });
        this.faceBook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (HomeActivity.this.menuVisible) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.facebook.com/CMoTo-101685531582029/"));
                    HomeActivity.this.startActivity(intent);
                }
            }
        });
        this.linkedIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (HomeActivity.this.menuVisible) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.linkedin.com/company/cmoto"));
                    HomeActivity.this.startActivity(intent);
                }
            }
        });
        this.instagram.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (HomeActivity.this.menuVisible) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.instagram.com/invites/contact/?i=iix8qk5d4i0a&utm_content=h7njsz7"));
                    HomeActivity.this.startActivity(intent);
                }
            }
        });
        this.termsAndCondition.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (HomeActivity.this.menuVisible)
                    Toast.makeText((Context)HomeActivity.this, "Terms and Condition", Toast.LENGTH_SHORT).show();
            }
        });
        this.contactUs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (HomeActivity.this.menuVisible)
                    Toast.makeText((Context)HomeActivity.this, "Contact Us", Toast.LENGTH_SHORT).show();
            }
        });
        this.aboutUs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (HomeActivity.this.menuVisible)
                    Toast.makeText((Context)HomeActivity.this, "About Us", Toast.LENGTH_SHORT).show();
            }
        });
        this.profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (HomeActivity.this.menuVisible)
                    HomeActivity.this.startActivity(new Intent((Context)HomeActivity.this, ProfileActivity.class));
            }
        });
        receiveData();
        this.scanner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (SystemClock.elapsedRealtime() - HomeActivity.this.lastClicked < 1000L)
                    return;
//                HomeActivity.access$302(HomeActivity.this, (int)SystemClock.elapsedRealtime());
                lastClicked = (int) SystemClock.elapsedRealtime();
                HomeActivity.this.reference.child("Employee").child(HomeActivity.this.mAuth.getUid()).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onCancelled(DatabaseError param2DatabaseError) {}

                    public void onDataChange(DataSnapshot param2DataSnapshot) {
                        if (param2DataSnapshot.getValue().toString().equals("working")) {
                            Toast.makeText((Context)HomeActivity.this, "The timer is already started", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent((Context)HomeActivity.this, QRCodeScanning.class);
                        Bundle bundle = new Bundle();
                        bundle.putCharSequenceArrayList("carsNumberArray", new ArrayList(HomeActivity.this.carsToBeWashed));
                        bundle.putCharSequence("Areas", HomeActivity.this.Area);
                        intent.putExtras(bundle);
                        HomeActivity.this.startActivity(intent);
                    }
                });
            }
        });
        if(getIntent().hasExtra("reload")){
            carsToBeWashed = new ArrayList<>();
            deactivatedCars = new ArrayList<>();
            carsModel = new ArrayList<>();
            carsPhoto = new HashMap<>();
            houseNumberList = new ArrayList<>();
            leaveTimeList = new ArrayList<>();
            carItems = new ArrayList<>();
            HomeActivity.this.receiveData();
        }
        this.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                carsToBeWashed = new ArrayList<>();
                deactivatedCars = new ArrayList<>();
                carsModel = new ArrayList<>();
                carsPhoto = new HashMap<>();
                houseNumberList = new ArrayList<>();
                leaveTimeList = new ArrayList<>();
                carItems = new ArrayList<>();
                HomeActivity.this.receiveData();
//                refreshLayout.setRefreshing(false);
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        try {
            this.droidNet.removeInternetConnectivityChangeListener(this);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void onInternetConnectivityChanged(boolean paramBoolean) {
        if (paramBoolean) {
            Log.d("HomeActivity", "onInternetConnectivityChanged: INTERNET connected");
            RecyclerView recyclerView = this.recyclerView;
            if (recyclerView != null) {
                recyclerView.setVisibility(View.VISIBLE);
                this.scanner.setVisibility(View.VISIBLE);
            }
            this.isConnected = true;
        } else {
            Log.d("HomeActivity", "onInternetConnectivityChanged: INTERNET lost in HomeActivity");
//            this.isConnected = false;
//            this.recyclerView.setVisibility(View.GONE);
//            this.scanner.setVisibility(View.GONE);
//            this.progressBar.setVisibility(View.GONE);
            Toast.makeText((Context)this, "Internet Connection Lost", Toast.LENGTH_SHORT).show();
        }
    }



    public void receiveData() {
        this.reference.child("Employee").child(this.mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError param1DatabaseError) {
                Log.d("HomeActivity", "onCancelled: event cancelled due to error : " + param1DatabaseError.getMessage());
                Toast.makeText((Context)HomeActivity.this, "event cancelled", Toast.LENGTH_SHORT).show();
            }

            public void onDataChange(DataSnapshot param1DataSnapshot) {
                if (param1DataSnapshot != null) {
                    String str = (new SimpleDateFormat("yyyy-MM-dd")).format(Calendar.getInstance().getTime());
                    Log.d("HomeActivity", "onDataChange: we got this cars : " + param1DataSnapshot.child("todaysCars").getValue());

                    todayCars = (String)param1DataSnapshot.child("todaysCars").getValue();
                    Area = (String)param1DataSnapshot.child("Working_Address").getValue();
                    if (param1DataSnapshot.child("working on").getValue() != null)
                        WorkingOn = param1DataSnapshot.child("working on").getValue().toString();
                    if (param1DataSnapshot.child("Work History").child(str).child("paidOn").exists()) {

//                        HomeActivity.access$1402(HomeActivity.this, (int)param1DataSnapshot.child("Work History").child(str).getChildrenCount() - 2);
                    } else if (param1DataSnapshot.child("Work History").child(str).exists()) {
//                        HomeActivity.access$1402(HomeActivity.this, (int)param1DataSnapshot.child("Work History").child(str).getChildrenCount() - 1);
                    }
                    if (HomeActivity.this.todayCars != null) {
//                        HomeActivity.access$402(HomeActivity.this, new ArrayList(Arrays.asList((Object[])HomeActivity.this.todayCars.trim().split("\\s*,\\s*"))));
                        carsToBeWashed = new ArrayList(Arrays.asList((Object[])HomeActivity.this.todayCars.trim().split("\\s*,\\s*")));
                        HashSet hashSet = new HashSet(HomeActivity.this.carsToBeWashed);
                        HomeActivity.this.carsToBeWashed.clear();
                        HomeActivity.this.carsToBeWashed.addAll(hashSet);
                        HomeActivity.this.carsToBeWashed.remove("");
                        Log.d("HomeActivity", "onCreate: we got cars array as follows " + HomeActivity.this.carsToBeWashed);
                        if (HomeActivity.this.carsToBeWashed.size() == 0) {
                            HomeActivity.this.recyclerView.setVisibility(View.GONE);
                            HomeActivity.this.scanner.setVisibility(View.GONE);
                            HomeActivity.this.workingComplete.setVisibility(View.GONE);
                            HomeActivity.this.progressBar.setVisibility(View.GONE);
                            HomeActivity.this.remainingCars.setText("remaining\n");
                            HomeActivity.this.carsCleaned.setText("done\n" + HomeActivity.this.carsDone);
                        }

                        for (byte finalI = 0; finalI < HomeActivity.this.carsToBeWashed.size(); finalI++) {
                            byte finalI1 = finalI;
                            HomeActivity.this.reference.child("cars").child(HomeActivity.this.Area).child(HomeActivity.this.carsToBeWashed.get(finalI)).addListenerForSingleValueEvent(new ValueEventListener() {
                                public void onCancelled(DatabaseError param2DatabaseError) {
                                    HomeActivity.this.progressBar.setVisibility(View.GONE);
                                    Toast.makeText((Context)HomeActivity.this, "got error : " + param2DatabaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("HomeActivity", "onCancelled: got error : " + param2DatabaseError.getDetails());
                                }

                                public void onDataChange(DataSnapshot param2DataSnapshot) {
                                    Log.d("HomeActivity", "onDataChange: getting carsModel and carsPhoto" + param2DataSnapshot);
                                    try {
                                        if (param2DataSnapshot.exists()) {
                                            StringBuilder stringBuilder = new StringBuilder();
//                                            this();
                                            Log.d("HomeActivity", stringBuilder.append("onDataChange: car : ").append(param2DataSnapshot.child("model")).toString());
                                            if(!param2DataSnapshot.child("Active").getValue().toString().equals("1")){
                                                deactivatedCars.add(carsToBeWashed.get(finalI1));
                                            }else{

                                                HomeActivity.this.carsModel.add((String)param2DataSnapshot.child("model").getValue());
                                                HomeActivity.this.carsPhoto.put(HomeActivity.this.carsToBeWashed.get(finalI1), param2DataSnapshot.child("photo").getValue().toString());
                                                HomeActivity.this.leaveTimeList.add((String)param2DataSnapshot.child("leaveTime").getValue());
                                                HomeActivity.this.houseNumberList.add((Long)param2DataSnapshot.child("houseNumber").getValue());
                                            }

                                        }
                                    } catch (Exception exception) {
                                        Log.d("HomeActivity", "onDataChange: error : " + exception.getMessage());
                                        Toast.makeText((Context)HomeActivity.this, "error due car "+ carsToBeWashed.get(finalI1) + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    if (refreshLayout.isRefreshing()) {
                                        refreshLayout.setRefreshing(false);
                                    }
                                    Log.d("HomeActivity", "onDataChange: model : " + HomeActivity.this.carsModel + "photo : " + HomeActivity.this.carsPhoto);
                                    Log.d("HomeActivity", "onDataChange: " + HomeActivity.this.carsToBeWashed.size() + " : " + HomeActivity.this.carsModel.size());
                                    if ((HomeActivity.this.houseNumberList.size()+deactivatedCars.size()) == HomeActivity.this.carsToBeWashed.size()) {
                                        Log.d("HomeActivity", "onDataChange: setting adapter");
                                        if (HomeActivity.this.carsToBeWashed.contains(HomeActivity.this.WorkingOn) && HomeActivity.this.carsToBeWashed.indexOf(HomeActivity.this.WorkingOn) != 0) {
                                            HomeActivity.this.carsToBeWashed.remove(HomeActivity.this.WorkingOn);
                                            HomeActivity.this.carsToBeWashed.add(0, HomeActivity.this.WorkingOn);
                                        }
                                        setAdapter();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        refreshLayout.setRefreshing(false);
                    }
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void setAdapter() {
        carsToBeWashed.removeAll(deactivatedCars);
        Log.d("HomeActivity", "setAdapter: carsTobeWashed : " + this.carsToBeWashed.size());
        Log.d("HomeActivity", "setAdapter: carsModel : " + this.carsModel.size());
        Log.d("HomeActivity", "setAdapter: carsPhoto : " + this.carsPhoto.size());
        Log.d("HomeActivity", "setAdapter: houseNumberList : " + this.houseNumberList.size());
        Log.d("HomeActivity", "setAdapter: leaveTimeList : " + this.leaveTimeList.size());
        Log.d("HomeActivity", "setAdapter: setAdapter function called");
        this.remainingCars.setText(this.carsToBeWashed.size() + "\nRemaining");
        this.carsCleaned.setText(this.carsDone + "\nDone");
        for (byte b = 0; b < this.carsPhoto.size(); b++) {
            CarListItem carListItem = new CarListItem(this.carsToBeWashed.get(b), this.carsModel.get(b), this.carsPhoto.get(this.carsToBeWashed.get(b)), this.houseNumberList.get(b), this.leaveTimeList.get(b));
            this.carItems.add(carListItem);
        }
//        Collections.sort(this.carItems, new Comparator<CarListItem>() {
//            public int compare(CarListItem param1CarListItem1, CarListItem param1CarListItem2) {
//                return param1CarListItem1.getHouseNumber().compareTo(param1CarListItem2.getHouseNumber());
//            }
//        });
        this.adapter = new RecyclerViewAdapter(this.carItems, (Context)this, this.Area, "exterior");
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setMotionEventSplittingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        this.recyclerView.setLayoutManager((RecyclerView.LayoutManager)linearLayoutManager);
        this.recyclerView.setAdapter((RecyclerView.Adapter)this.adapter);
        this.adapter.notifyDataSetChanged();
        this.refreshLayout.setRefreshing(false);
    }
}

