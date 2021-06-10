package com.example.cmotoemployee.InteriorEmployeeActivities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cmotoemployee.Adapter.RecyclerViewAdapter;
import com.example.cmotoemployee.Adapter.RecyclerViewAdapterForPayment;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class InteriorPaymentActivity extends AppCompatActivity {
    private static final String TAG = "InteriorPaymentActivity";

    private RecyclerViewAdapter adapter;

    private TextView amountDue;

    private FirebaseAuth auth;

    private ImageView back;

    private ArrayList<String> carNumbers;

    private ArrayList<Integer> carPayments;

    private TextView month;

    private RecyclerView recyclerView;

    private DatabaseReference reference;

    private Integer totalPrice = Integer.valueOf(0);

    private String getMonth() {
        int i = (new Date()).getMonth();
        String str = "";
        switch (i) {
            default:
                return str;
            case 11:
                str = "DEC";
            case 10:
                str = "NOV";
            case 9:
                str = "OCT";
            case 8:
                str = "SEP";
            case 7:
                str = "AUG";
            case 6:
                str = "JULY";
            case 5:
                str = "JUNE";
            case 4:
                str = "MAY";
            case 3:
                str = "APRIL";
            case 2:
                str = "MARCH";
            case 1:
                str = "FEB";
            case 0:
                break;
        }
        str = "JAN";
        return str;
    }

    private boolean isPaid(String paramString) {
        if(paramString.equals("")) return true;
        Date lastPaid = null;
        Date presentDate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            lastPaid = simpleDateFormat.parse(paramString);
            presentDate = simpleDateFormat.parse(String.valueOf(presentDate));
        } catch (ParseException parseException) {
            Log.d("RecyclerViewAdapter", "isPaid: error : " + parseException.getMessage());

        }
        Log.d("RecyclerViewAdapter", "isPaid: paid date :  month : " + presentDate);
        Log.d("RecyclerViewAdapter", "isPaid: todays date : " + lastPaid);
        return (presentDate.getMonth() == lastPaid.getMonth());
    }

    private void setAdapter(final ArrayList<String> carNumber, final ArrayList<Integer> carPayment) {
        Log.d("InteriorPaymentActivity", "setAdapter: setAdapter called : ");
        Log.d("InteriorPaymentActivity", "setAdapter: carNumbers " + carNumber);
        Log.d("InteriorPaymentActivity", "setAdapter: carPayments " + carPayment);
        this.amountDue.setText(this.totalPrice + "");
        this.reference.child("InteriorEmployee").child(this.auth.getUid()).child("Working_Address").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError param1DatabaseError) {}

            public void onDataChange(DataSnapshot param1DataSnapshot) {
                String str = param1DataSnapshot.getValue().toString();
                RecyclerViewAdapterForPayment recyclerViewAdapterForPayment = new RecyclerViewAdapterForPayment(carNumber, carPayment, (Context)InteriorPaymentActivity.this, str);
                InteriorPaymentActivity.this.recyclerView.setHasFixedSize(true);
                InteriorPaymentActivity.this.recyclerView.setMotionEventSplittingEnabled(false);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(InteriorPaymentActivity.this.getApplicationContext());
                InteriorPaymentActivity.this.recyclerView.setLayoutManager((RecyclerView.LayoutManager)linearLayoutManager);
                InteriorPaymentActivity.this.recyclerView.setAdapter((RecyclerView.Adapter)recyclerViewAdapterForPayment);
            }
        });
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_payment_details);
        this.reference = FirebaseDatabase.getInstance().getReference();
        this.auth = FirebaseAuth.getInstance();
        this.recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        TextView textView = (TextView)findViewById(R.id.presentMonth);
        this.month = textView;
        textView.setText(getMonth());
        this.back = (ImageView)findViewById(R.id.back);
        this.amountDue = (TextView)findViewById(R.id.amountDue);
        this.carNumbers = new ArrayList<>();
        this.carPayments = new ArrayList<>();
        this.back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                InteriorPaymentActivity.this.finish();
            }
        });
        this.reference.child("InteriorEmployee").child(this.auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError param1DatabaseError) {}

            public void onDataChange(DataSnapshot param1DataSnapshot) {
                final ArrayList<String> carNumber = new ArrayList();
                final ArrayList<Integer> carPayment = new ArrayList();
                ArrayList<String> arrayList4 = new ArrayList(Arrays.asList((Object[])param1DataSnapshot.child("mondayCars").getValue().toString().trim().split("\\s*,\\s*")));
                ArrayList<String> arrayList5 = new ArrayList(Arrays.asList((Object[])param1DataSnapshot.child("tuesdayCars").getValue().toString().trim().split("\\s*,\\s*")));
                ArrayList<String> arrayList6 = new ArrayList(Arrays.asList((Object[])param1DataSnapshot.child("wednesdayCars").getValue().toString().trim().split("\\s*,\\s*")));
                ArrayList<String> arrayList7 = new ArrayList(Arrays.asList((Object[])param1DataSnapshot.child("thursdayCars").getValue().toString().trim().split("\\s*,\\s*")));
                ArrayList<String> arrayList1 = new ArrayList(Arrays.asList((Object[])param1DataSnapshot.child("fridayCars").getValue().toString().trim().split("\\s*,\\s*")));
                carNumbers.addAll(arrayList4);
                carNumbers.addAll(arrayList5);
                carNumbers.addAll(arrayList6);
                carNumbers.addAll(arrayList7);
                carNumbers.addAll(arrayList1);
                Log.d("InteriorPaymentActivity", "onDataChange: check 1 carNumbers : " + InteriorPaymentActivity.this.carNumbers);
//                InteriorPaymentActivity.access$102(InteriorPaymentActivity.this, Integer.valueOf(0));
                for (byte finalI = 0; finalI < InteriorPaymentActivity.this.carNumbers.size(); finalI++) {
                    byte finalI1 = finalI;
                    InteriorPaymentActivity.this.reference.child("Car Status").child(InteriorPaymentActivity.this.carNumbers.get(finalI)).addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onCancelled(DatabaseError param2DatabaseError) {}

                        public void onDataChange(DataSnapshot param2DataSnapshot) {
                            if (param2DataSnapshot.child("lastPaidOn").exists()) {

                                String str = param2DataSnapshot.child("category").getValue().toString().toLowerCase();
                                int price = 0;
                                switch (str) {
                                    case "compact sedan":
                                    case "hatchback":
                                        price = 100;
                                        break;
                                    case "sedan":
                                        price = 200;
                                        break;
                                    case "suv":
                                        price = 300;
                                        break;
                                    case "luv":
                                        break;
                                }

                                if (!InteriorPaymentActivity.this.isPaid(param2DataSnapshot.child("lastPaidOn").getValue().toString())) {
                                    carNumber.add(InteriorPaymentActivity.this.carNumbers.get(finalI1));
                                    carPayment.add(Integer.valueOf(price));
                                    totalPrice = Integer.valueOf(InteriorPaymentActivity.this.totalPrice.intValue() + price);
                                }
                                if (finalI1 == InteriorPaymentActivity.this.carNumbers.size() - 1) {
                                    Log.d("InteriorPaymentActivity", "onDataChange: check 2 carNumber and carpayments final are : -- ");
                                    Log.d("InteriorPaymentActivity", "onDataChange: carNumber : " + carNumber);
                                    Log.d("InteriorPaymentActivity", "onDataChange: carPayment : " + carPayment);
                                    InteriorPaymentActivity.this.setAdapter(carNumber, carPayment);
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}

