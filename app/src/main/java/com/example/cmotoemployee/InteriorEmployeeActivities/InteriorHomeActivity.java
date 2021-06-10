package com.example.cmotoemployee.InteriorEmployeeActivities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.cmotoemployee.EmployeeActivities.ProfileActivity;
import com.example.cmotoemployee.ErrorHandler.CrashHandler;
import com.example.cmotoemployee.Model.CarListItem;
import com.example.cmotoemployee.R;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import java.util.concurrent.CountDownLatch;

public class InteriorHomeActivity extends AppCompatActivity implements DroidListener {
    private static final String TAG = "InteriorHomeActivity";

    private HashMap<String,String> Area;

    private String WorkingOn = "";

    private TextView aboutUs;

    private RecyclerViewAdapter adapter;

    private TextView carList;

    private TextView carsCleaned;

    private int carsDone = 0;

    private TextView contactUs;

    private FirebaseDatabase database;

    private DroidNet droidNet;

    private ImageView exit;

    private ImageView faceBook;

    private TextView friday;

    private RecyclerView fridayCars;

    private SwipeRefreshLayout refreshLayout;

    private ImageView instagram;

    private boolean isConnected = true;

    private int lastClicked = 0;

    private RecyclerView.LayoutManager layoutManager;

    private ImageView linkedIn;

    private TextView logout;

    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseAuth mAuth;

    private RelativeLayout menu;

    private RelativeLayout menuClose;

    private ImageView menuOptions;

    private boolean menuVisible = false;

    private TextView monday;

    private RecyclerView mondayCars;

    private TextView name;

    private TextView paymentDetails;

    private TextView profile;

    private ProgressBar progressBar;

    private RecyclerView recyclerView;

    private DatabaseReference reference;

    private TextView remainingCars;

    private ImageView scanner;

    private TextView termsAndCondition;

    private TextView thursday;

    private RecyclerView thursdayCars;

    private String todayCars = "";

    private Toolbar toolbar;

    private TextView tuesday;

    private RecyclerView tuesdayCars;

    private FirebaseUser user;

    private TextView wednesday;

    private RecyclerView wednesdayCars;

    private TextView workingComplete;

    private int carsRemaining = 0;



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
        setContentView(R.layout.interior_home);
        Thread.setDefaultUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new CrashHandler(getApplicationContext()));
        Log.d("InteriorHomeActivity", "onCreate: ");
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
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        this.logout = (TextView)findViewById(R.id.logout);
        this.progressBar.setVisibility(View.VISIBLE);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        this.menu = (RelativeLayout)findViewById(R.id.Menu);
        this.exit = (ImageView)findViewById(R.id.exit);
        this.name = (TextView)findViewById(R.id.name);
        this.menuClose = (RelativeLayout)findViewById(R.id.remainingBody);
        this.scanner = (ImageView)findViewById(R.id.scanner);
        this.carsCleaned = (TextView)findViewById(R.id.done);
        this.remainingCars = (TextView)findViewById(R.id.remaining);
        this.workingComplete = (TextView)findViewById(R.id.workComplete);
        this.recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        Area = new HashMap<>();


        this.monday = (TextView)findViewById(R.id.monday);
        this.tuesday = (TextView)findViewById(R.id.tuesday);
        this.wednesday = (TextView)findViewById(R.id.wednesday);
        this.thursday = (TextView)findViewById(R.id.thursday);
        this.friday = (TextView)findViewById(R.id.friday);
        this.mondayCars = (RecyclerView)findViewById(R.id.mondayCars);
        this.tuesdayCars = (RecyclerView)findViewById(R.id.tuesdayCars);
        this.wednesdayCars = (RecyclerView)findViewById(R.id.wednesdayCars);
        this.thursdayCars = (RecyclerView)findViewById(R.id.thursdayCars);
        this.fridayCars = (RecyclerView)findViewById(R.id.fridayCars);
        this.todayCars = "";
        try {
            DroidNet droidNet = DroidNet.getInstance();
            this.droidNet = droidNet;
            droidNet.addInternetConnectivityListener(this);
        } catch (Exception exception) {
            Log.e("InteriorHomeActivity", "onCreate: error : " + exception.getStackTrace());
        }
        if (!this.isConnected)
            Toast.makeText((Context)this, "Internet connection lost", Toast.LENGTH_SHORT).show();
        this.reference.child("InteriorEmployee").child(this.mAuth.getUid()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError param1DatabaseError) {}

            public void onDataChange(DataSnapshot param1DataSnapshot) {
                InteriorHomeActivity.this.name.setText(param1DataSnapshot.getValue().toString());
            }
        });
        this.menuOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                InteriorHomeActivity.this.menuVisibility(true);
            }
        });
        this.exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                InteriorHomeActivity.this.menuVisibility(false);
            }
        });
        this.menuClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.menuVisible)
                    InteriorHomeActivity.this.menuVisibility(false);
            }
        });
        this.paymentDetails.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.menuVisible) {
                    InteriorHomeActivity.this.menuVisibility(false);
                    InteriorHomeActivity.this.startActivity((new Intent((Context)InteriorHomeActivity.this, InteriorPaymentActivity.class)).putExtra("interior", "interior"));
                }
            }
        });
        this.carList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.menuVisible)
                    InteriorHomeActivity.this.menuVisibility(false);
            }
        });
        this.logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.menuVisible) {
                    InteriorHomeActivity.this.menuVisibility(false);
                    InteriorHomeActivity.this.mAuth.signOut();
                    InteriorHomeActivity.this.startActivity(new Intent((Context)InteriorHomeActivity.this, StartActivity.class));
                    InteriorHomeActivity.this.finish();
                }
            }
        });
        this.faceBook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.menuVisible) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.facebook.com/CMoTo-101685531582029/"));
                    InteriorHomeActivity.this.startActivity(intent);
                }
            }
        });
        this.linkedIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.menuVisible) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.linkedin.com/company/cmoto"));
                    InteriorHomeActivity.this.startActivity(intent);
                }
            }
        });
        this.instagram.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.menuVisible) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.instagram.com/invites/contact/?i=iix8qk5d4i0a&utm_content=h7njsz7"));
                    InteriorHomeActivity.this.startActivity(intent);
                }
            }
        });
        this.termsAndCondition.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.menuVisible)
                    Toast.makeText((Context)InteriorHomeActivity.this, "Terms and Condition", Toast.LENGTH_SHORT).show();
            }
        });
        this.contactUs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.menuVisible)
                    Toast.makeText((Context)InteriorHomeActivity.this, "Contact Us", Toast.LENGTH_SHORT).show();
            }
        });
        this.aboutUs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.menuVisible)
                    Toast.makeText((Context)InteriorHomeActivity.this, "About Us", Toast.LENGTH_SHORT).show();
            }
        });
        this.profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.menuVisible)
                    InteriorHomeActivity.this.startActivity((new Intent((Context)InteriorHomeActivity.this, ProfileActivity.class)).putExtra("interior", "interior"));
            }
        });
        receiveData("mondayCars", this.mondayCars);
        receiveData("tuesdayCars", this.tuesdayCars);
        receiveData("wednesdayCars", this.wednesdayCars);
        receiveData("thursdayCars", this.thursdayCars);
        receiveData("fridayCars", this.fridayCars);
        this.progressBar.setVisibility(View.GONE);
        this.monday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.mondayCars.getVisibility() == View.VISIBLE) {
                    InteriorHomeActivity.this.mondayCars.setVisibility(View.GONE);
                } else {
                    InteriorHomeActivity.this.mondayCars.setVisibility(View.VISIBLE);
                }
            }
        });
        this.tuesday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.tuesdayCars.getVisibility() == View.VISIBLE) {
                    InteriorHomeActivity.this.tuesdayCars.setVisibility(View.GONE);
                } else {
                    InteriorHomeActivity.this.tuesdayCars.setVisibility(View.VISIBLE);
                }
            }
        });
        this.wednesday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.wednesdayCars.getVisibility() == View.VISIBLE) {
                    InteriorHomeActivity.this.wednesdayCars.setVisibility(View.GONE);
                } else {
                    InteriorHomeActivity.this.wednesdayCars.setVisibility(View.VISIBLE);
                }
            }
        });
        this.thursday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.thursdayCars.getVisibility() == View.VISIBLE) {
                    InteriorHomeActivity.this.thursdayCars.setVisibility(View.GONE);
                } else {
                    InteriorHomeActivity.this.thursdayCars.setVisibility(View.VISIBLE);
                }
            }
        });
        this.friday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (InteriorHomeActivity.this.fridayCars.getVisibility() == View.VISIBLE) {
                    InteriorHomeActivity.this.fridayCars.setVisibility(View.GONE);
                } else {
                    InteriorHomeActivity.this.fridayCars.setVisibility(View.VISIBLE);
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                carsRemaining=0;
                carsDone=0;
                String str = (new SimpleDateFormat("yyyy-MM-dd")).format(Calendar.getInstance().getTime());
                FirebaseDatabase.getInstance().getReference().child("InteriorEmployee").child(FirebaseAuth.getInstance().getUid()).child("Interior Work History").child(str).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Log.d(TAG, "onDataChange: snapshot : " + snapshot.getChildrenCount());
                            carsCleaned.setText((int) snapshot.getChildrenCount() + "\n" + "done");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
                receiveData("mondayCars", mondayCars);
                receiveData("tuesdayCars", tuesdayCars);
                receiveData("wednesdayCars", wednesdayCars);
                receiveData("thursdayCars", thursdayCars);
                receiveData("fridayCars", fridayCars);

            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        this.droidNet.removeInternetConnectivityChangeListener(this);
    }

    public void onInternetConnectivityChanged(boolean paramBoolean) {
        if (paramBoolean) {
            Log.d("InteriorHomeActivity", "onInternetConnectivityChanged: INTERNET connected");
            RecyclerView recyclerView = this.recyclerView;
            if (recyclerView != null) {
                recyclerView.setVisibility(View.VISIBLE);
                this.scanner.setVisibility(View.VISIBLE);
            }
            this.isConnected = true;
        } else {
            Log.d("InteriorHomeActivity", "onInternetConnectivityChanged: INTERNET lost in HomeActivity");
//            this.isConnected = false;
//            this.recyclerView.setVisibility(View.GONE);
//            this.scanner.setVisibility(View.GONE);
//            this.progressBar.setVisibility(View.GONE);
            Toast.makeText((Context)this, "Internet Connection Lost", Toast.LENGTH_SHORT).show();
        }
    }

    public void receiveData(final String dayCars, final RecyclerView recyclerView) {

        String str = (new SimpleDateFormat("yyyy-MM-dd")).format(Calendar.getInstance().getTime());
        FirebaseDatabase.getInstance().getReference().child("InteriorEmployee").child(FirebaseAuth.getInstance().getUid()).child("Interior Work History").child(str).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Log.d(TAG, "onDataChange: snapshot : " + snapshot.getChildrenCount());
                    carsCleaned.setText((int) snapshot.getChildrenCount() + "\n" + "done");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        ArrayList arrayList1 = new ArrayList();
        final ArrayList carsModel = new ArrayList();
        final HashMap<String, String> carsPhoto = new HashMap<>();
        final ArrayList houseNumberList = new ArrayList();
        final ArrayList leaveTimeList = new ArrayList();
        this.reference.child("InteriorEmployee").child(this.mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError param1DatabaseError) {
                Log.d("InteriorHomeActivity", "onCancelled: event cancelled due to error : " + param1DatabaseError.getMessage());
                Toast.makeText((Context)InteriorHomeActivity.this, "event cancelled", Toast.LENGTH_SHORT).show();
            }

            public void onDataChange(DataSnapshot param1DataSnapshot) {
                if (param1DataSnapshot != null) {
                    String str = (new SimpleDateFormat("yyyy-MM-dd")).format(Calendar.getInstance().getTime());
                    Log.d("InteriorHomeActivity", "onDataChange: we got this cars : " + dayCars + param1DataSnapshot.child(dayCars).getValue());
                    todayCars = (String)param1DataSnapshot.child(dayCars).getValue();
//                    Area = (String)param1DataSnapshot.child("Working_Address").getValue();
                    if (param1DataSnapshot.child("working on").getValue() != null)
                         param1DataSnapshot.child("working on").getValue().toString();
                    if (param1DataSnapshot.child("Work History").child(str).child("paidOn").exists()) {
                        carsDone = (int)param1DataSnapshot.child("Work History").child(str).getChildrenCount() - 2;
                    } else if (param1DataSnapshot.child("Work History").child(str).exists()) {
                        carsDone = (int)param1DataSnapshot.child("Work History").child(str).getChildrenCount() - 1;
                    }
                    if (InteriorHomeActivity.this.todayCars != null) {
                        ArrayList[] carsToBeWashed = new ArrayList[1];
                        carsToBeWashed[0] = new ArrayList(Arrays.asList(InteriorHomeActivity.this.todayCars.trim().split("\\s*,\\s*")));
                        HashSet<?> hashSet = new HashSet(carsToBeWashed[0]);
                        carsToBeWashed[0].clear();
                        carsToBeWashed[0].addAll(hashSet);
                        carsToBeWashed[0].remove("");
                        Log.d("InteriorHomeActivity", "onCreate: we got cars array as follows " + carsToBeWashed[0]);
                        Log.d("InteriorHomeActivity", "Area  " + InteriorHomeActivity.this.Area);
                        if (carsToBeWashed[0].size() == 0) {
                            recyclerView.setVisibility(View.GONE);
                        }

                        readData(carsToBeWashed[0], new OnGetListener() {
                            @Override
                            public void onStart() {
                                Log.d(TAG, "onStart: carstobewashed : " + carsToBeWashed[0].toString());
                                Log.d(TAG, "onStart: readData called");
                            }

                            @Override
                            public void onSuccess() {



                        for (int finalI = 0; finalI < carsToBeWashed[0].size(); finalI++) {
                            DatabaseReference databaseReference = InteriorHomeActivity.this.reference.child("cars").child(Area.get(carsToBeWashed[0].get(finalI))).child((String) carsToBeWashed[0].get(finalI));
                            Log.d("InteriorHomeActivity", "onDataChange: area = " + InteriorHomeActivity.this.Area + " car = " + (String)carsToBeWashed[0].get(finalI));
                            int finalI1 = finalI;
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                public void onCancelled(DatabaseError param2DatabaseError) {
                                    InteriorHomeActivity.this.progressBar.setVisibility(View.GONE);
                                    Toast.makeText((Context)InteriorHomeActivity.this, "got error : " + param2DatabaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("InteriorHomeActivity", "onCancelled: got error : " + param2DatabaseError.getDetails());
                                }

                                public void onDataChange(DataSnapshot param2DataSnapshot) {
                                    Log.d("InteriorHomeActivity", "onDataChange: getting carsModel and carsPhoto" + param2DataSnapshot);
                                    try {
                                        if (param2DataSnapshot.exists()) {
                                            StringBuilder stringBuilder = new StringBuilder();
//                                            this();
                                            Log.d("InteriorHomeActivity", stringBuilder.append("onDataChange: car : ").append(param2DataSnapshot.child("model")).toString());
                                            if(!param2DataSnapshot.child("Active").getValue().toString().equals("1")){
                                                carsToBeWashed[0].remove(finalI1);
                                            }else {
                                                carsModel.add((String) param2DataSnapshot.child("model").getValue());
                                                carsPhoto.put(carsToBeWashed[0].get(finalI1).toString(), param2DataSnapshot.child("photo").getValue().toString());
                                                leaveTimeList.add((String) param2DataSnapshot.child("leaveTime").getValue());
                                                houseNumberList.add((Long) param2DataSnapshot.child("houseNumber").getValue());
                                            }
                                        }
                                    } catch (Exception exception) {
                                        Log.d("InteriorHomeActivity", "onDataChange: error : " + exception.getMessage());
                                        Toast.makeText((Context)InteriorHomeActivity.this, "error " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    if (refreshLayout.isRefreshing()) {
                                        refreshLayout.setRefreshing(false);
                                    }
                                    Log.d("InteriorHomeActivity", "onDataChange: model : " + carsModel + "photo : " + carsPhoto);
                                    Log.d("InteriorHomeActivity", "onDataChange: " + carsToBeWashed[0].size() + " : " + carsModel.size());
                                    if (carsModel.size() == carsToBeWashed[0].size()) {
                                        Log.d("InteriorHomeActivity", "onDataChange: setting adapter");
                                        if (carsToBeWashed[0].contains(InteriorHomeActivity.this.WorkingOn) && carsToBeWashed[0].indexOf(InteriorHomeActivity.this.WorkingOn) != 0) {
                                            carsToBeWashed[0].remove(InteriorHomeActivity.this.WorkingOn);
                                            carsToBeWashed[0].add(0, InteriorHomeActivity.this.WorkingOn);
                                        }
                                        carsRemaining += carsToBeWashed[0].size();
                                        InteriorHomeActivity.this.setAdapter(recyclerView, carsToBeWashed[0], carsModel, carsPhoto, houseNumberList, leaveTimeList, dayCars);
                                        InteriorHomeActivity.this.progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }

                            }

                            @Override
                            public void onFailed(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
        });
    }

    public void readData(ArrayList<String> carsToBeWashed,final OnGetListener listener) {
        listener.onStart();
        getAreas(carsToBeWashed,listener);

    }

    public void getAreas(ArrayList<String> carsToBeWashed,OnGetListener listener){
        Log.d(TAG, "getAreas: function called");

        Log.d(TAG, "getAreas: carsTobeWadhed : " + carsToBeWashed.toString());
        for(int carIndex=0; carIndex<carsToBeWashed.size(); carIndex++){
            int finalCarIndex = carIndex;
            Log.d(TAG, "getAreas: car : " + carsToBeWashed.get(carIndex));
            reference.child("Car Status").child((String) carsToBeWashed.get(carIndex)).child("Working_Address").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        Area.put((String) carsToBeWashed.get(finalCarIndex), snapshot.getValue().toString());
                        Log.d(TAG, "onDataChange: done : " + finalCarIndex + " : " + carsToBeWashed.size());
                        if (finalCarIndex == carsToBeWashed.size() - 1) {
                            listener.onSuccess();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }

    }

    public void setAdapter(RecyclerView paramRecyclerView, List<String> paramList1, List<String> paramList2, HashMap<String, String> paramHashMap, List<Long> paramList, List<String> paramList3, String paramString) {
        Log.d("InteriorHomeActivity", "setAdapter: setAdapter function called");
        remainingCars.setText(carsRemaining+" " + "\n" + "remaining");
        ArrayList<CarListItem> arrayList = new ArrayList();
        for (byte b = 0; b < paramList2.size(); b++)
            arrayList.add(new CarListItem(paramList1.get(b), paramList2.get(b), paramHashMap.get(paramList1.get(b)), paramList.get(b), paramList3.get(b)));
        Collections.sort(arrayList, new Comparator<CarListItem>() {
            public int compare(CarListItem param1CarListItem1, CarListItem param1CarListItem2) {
                return param1CarListItem1.getHouseNumber().compareTo(param1CarListItem2.getHouseNumber());
            }
        });
        this.adapter = new RecyclerViewAdapter(arrayList, (Context)this, Area, "interior", paramString);
        paramRecyclerView.setHasFixedSize(true);
        paramRecyclerView.setMotionEventSplittingEnabled(false);
        paramRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        this.layoutManager = (RecyclerView.LayoutManager)linearLayoutManager;
        paramRecyclerView.setLayoutManager((RecyclerView.LayoutManager)linearLayoutManager);
        paramRecyclerView.setAdapter((RecyclerView.Adapter)this.adapter);
        this.adapter.notifyDataSetChanged();
    }
}

interface OnGetListener {
    public void onStart();
    public void onSuccess();
    public void onFailed(DatabaseError databaseError);
}


