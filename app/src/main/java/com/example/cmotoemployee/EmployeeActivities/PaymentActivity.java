package com.example.cmotoemployee.EmployeeActivities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cmotoemployee.Adapter.RecyclerViewAdapterForProfile;
import com.example.cmotoemployee.Model.Payment;
import com.example.cmotoemployee.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = "PaymentActivity";

    private List<String> CarNumbers;

    private int DueAmount = 0;

    private RecyclerViewAdapterForProfile adapter;

    private TextView amountDue;

    private FirebaseAuth auth;

    private ImageView back;

    private List<Payment> carsPayment;

    private FirebaseDatabase database;

    private String employeeType;

    private String lastPaid;

    private TextView lastPaidOn;

    private TextView presentPayment;

    private RecyclerView recyclerView;

    private DatabaseReference reference;

    private void setAdapter(List<Payment> paramList) {
        try {
            Comparator<Payment> comparator = new Comparator<Payment>() {
                public int compare(Payment param1Payment1, Payment param1Payment2) {
                    if (param1Payment1.getDate() == null || param1Payment2.getDate() == null)
                        return 0;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        return simpleDateFormat.parse(param1Payment1.getDate()).compareTo(simpleDateFormat.parse(param1Payment2.getDate()));
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                        return 0;
                    }
                }
            };
//            super(this);
            Collections.sort(paramList, comparator);
            try {
                TextView textView1 = this.presentPayment;
                StringBuilder stringBuilder1 = new StringBuilder();
//                this();
                textView1.setText(stringBuilder1.append(getMonth(((Payment)paramList.get(0)).getDate())).append("").toString());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
            TextView textView = this.amountDue;
            StringBuilder stringBuilder = new StringBuilder();
//            this();
            textView.setText(stringBuilder.append(this.DueAmount).append("").toString());
            String str = this.lastPaid;
            if (str != null && str.length() > 4) {
                TextView textView1 = this.lastPaidOn;
                StringBuilder stringBuilder1 = new StringBuilder();
//                this();
                textView1.setText(stringBuilder1.append("last paid on ").append(this.lastPaid.substring(0, 2)).append(" ").append(getMonth(this.lastPaid)).append(" ").append(this.lastPaid.substring(6)).toString());
            }
            RecyclerViewAdapterForProfile recyclerViewAdapterForProfile = new RecyclerViewAdapterForProfile(carsPayment,getApplicationContext());
//            this(paramList, getApplicationContext());
            this.adapter = recyclerViewAdapterForProfile;
            this.recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//            this(getApplicationContext());
            this.recyclerView.setLayoutManager((RecyclerView.LayoutManager)linearLayoutManager);
            this.recyclerView.setAdapter((RecyclerView.Adapter)this.adapter);
        } catch (Error error) {
            Log.d("PaymentActivity", "setAdapter: got the error while setting employee profile" + error.getMessage());
            Toast.makeText((Context)this, "Met an Error while retrieving your Profile data", Toast.LENGTH_SHORT).show();
        } catch (ParseException parseException) {}
    }

    public String getMonth(String paramString) throws ParseException {
        Date date = (new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)).parse(paramString);
        Log.d("PaymentActivity", "getMonth: date : " + date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String str = (new SimpleDateFormat("MMM", Locale.ENGLISH)).format(calendar.getTime());
        Log.d("PaymentActivity", "getMonth: present month : " + str);
        return str.toUpperCase();
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_payment_details);
        this.database = FirebaseDatabase.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.reference = this.database.getReference();
        this.recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        this.amountDue = (TextView)findViewById(R.id.amountDue);
        this.back = (ImageView)findViewById(R.id.back);
        this.lastPaidOn = (TextView)findViewById(R.id.lastPaidOn);
        this.presentPayment = (TextView)findViewById(R.id.presentMonth);
        this.carsPayment = new ArrayList<>();
        this.CarNumbers = new ArrayList<>();
        if (getIntent().hasExtra("interior")) {
            this.employeeType = "InteriorEmployee";
        } else {
            this.employeeType = "Employee";
        }
        this.back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                PaymentActivity.this.finish();
            }
        });
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView param1RecyclerView, int param1Int) {
                super.onScrollStateChanged(param1RecyclerView, param1Int);
                if (param1Int == 0) {
                    String str = ((Payment)PaymentActivity.this.carsPayment.get(((LinearLayoutManager)param1RecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition())).getDate();
                    Log.d("PaymentActivity", "onScrollStateChanged: date : " + str);
                    try {
                        PaymentActivity.this.presentPayment.setText(PaymentActivity.this.getMonth(str));
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                        Log.d("PaymentActivity", "onScrollStateChanged: error while setting present month : " + parseException.getMessage());
                    }
                }
            }
        });
        this.reference.child(this.employeeType).child(this.auth.getUid()).child("Work History").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError param1DatabaseError) {
                Toast.makeText((Context)PaymentActivity.this, "request cancelled", Toast.LENGTH_SHORT).show();
            }

            public void onDataChange(DataSnapshot param1DataSnapshot) {
                byte b = 0;
                try {
                    for (DataSnapshot dataSnapshot : param1DataSnapshot.getChildren()) {
                        Payment payment = new Payment();
//                        this();
                        StringBuilder stringBuilder2 = new StringBuilder();
//                        this();
                        Log.d("PaymentActivity", stringBuilder2.append("onDataChange: ").append(dataSnapshot).toString());
                        if (dataSnapshot.getKey().toString() != null)
                            payment.setDate(dataSnapshot.getKey());
                        int money = 0;
                        if (dataSnapshot.child("income").getValue().toString() != null) {
                            money = Integer.parseInt(dataSnapshot.child("income").getValue().toString());
                            payment.setPrice(money);
                            DueAmount+= money;
//                            PaymentActivity.access$212(PaymentActivity.this, i);
                        }
                        if (dataSnapshot.child("paid on").exists()) {
                            payment.setPayOn(dataSnapshot.child("paid on").getValue().toString());
//                            PaymentActivity.access$302(PaymentActivity.this, payment.getDate());
//                            PaymentActivity.access$220(PaymentActivity.this, i);
                            lastPaid = payment.getDate();
                            DueAmount -= money;
                            payment.setCarsCleaned((int)dataSnapshot.getChildrenCount() - 2);
                        } else {
                            payment.setPayOn("due");
                            payment.setCarsCleaned((int)dataSnapshot.getChildrenCount() - 1);
                        }
                        StringBuilder stringBuilder1 = new StringBuilder();
//                        this();
                        Log.d("PaymentActivity", stringBuilder1.append("onDataChange: income = ").append(money).toString());
                        b++;
                        PaymentActivity.this.carsPayment.add(payment);
                    }
                    PaymentActivity paymentActivity = PaymentActivity.this;
                    paymentActivity.setAdapter(paymentActivity.carsPayment);
                } catch (Exception exception) {
                    Log.d("PaymentActivity", "onDataChange: got an exception" + exception);
                    Toast.makeText((Context)PaymentActivity.this, "An exception occurred while retrieving data from server : " + exception.getStackTrace(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

