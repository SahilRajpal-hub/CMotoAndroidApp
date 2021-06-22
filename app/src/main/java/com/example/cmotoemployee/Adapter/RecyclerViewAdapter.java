package com.example.cmotoemployee.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cmotoemployee.EmployeeActivities.StartCarCleaningActivity;
import com.example.cmotoemployee.Model.CarListItem;
import com.example.cmotoemployee.R;
import com.example.cmotoemployee.RoundedTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private String Area;
    private HashMap<String,String> AreaKey;

    private boolean Connected = true;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    private List<CarListItem> carItems;

    private Context context;

    private String daysCar;

    private String employeeType;
    GradientDrawable gd = new GradientDrawable();


    private boolean isInterior = false;

    private long lastClicked = 0L;

    private String status;

    private long toBeWait = 2000L;

    public RecyclerViewAdapter(List<CarListItem> paramList, Context paramContext, String paramString1, String paramString2) {
        this.carItems = paramList;
        this.context = paramContext;
        this.Area = paramString1;
        this.daysCar = " ";
        if (paramString2.equals("interior")) {
            Log.d("RecyclerViewAdapter", "RecyclerViewAdapter: employeeType : interior");
            this.isInterior = true;
            this.employeeType = "InteriorEmployee";
            this.status = "Interior Cleaning status";
        } else {
            this.employeeType = "Employee";
            this.status = "status";
        }
    }

    public RecyclerViewAdapter(List<CarListItem> paramList, Context paramContext, HashMap<String,String> paramString1, String paramString2, String paramString3) {
        this.carItems = paramList;
        this.context = paramContext;
        this.Area = paramString1.get(carItems.get(0).getNumber());
        this.daysCar = paramString3;
        Log.d(TAG, "RecyclerViewAdapter: Area : " + Area);
        if (paramString2.equals("interior")) {
            Log.d("RecyclerViewAdapter", "RecyclerViewAdapter: employeeType : interior");
            this.isInterior = true;
            this.employeeType = "InteriorEmployee";
            this.status = "Interior Cleaning status";
            this.AreaKey = paramString1;
            Log.d(TAG, "RecyclerViewAdapter: AreaKey : " + AreaKey);
        } else {
            this.employeeType = "Employee";
            this.status = "status";
        }
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

    public int getItemCount() {
        return this.carItems.size();
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public int getItemViewType(int paramInt) {
        return paramInt;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        gd.setColor(context.getResources().getColor(R.color.colorAccentLight));
        gd.setCornerRadius(50);
        holder.CarNumber.setText(((CarListItem)this.carItems.get(position)).getNumber());
        holder.carName.setText(((CarListItem)this.carItems.get(position)).getModel());
        if (this.isInterior)
            FirebaseDatabase.getInstance().getReference().child("Car Status").child(((CarListItem)this.carItems.get(position)).getNumber()).child("lastPaidOn").addListenerForSingleValueEvent(new ValueEventListener() {
                public void onCancelled(DatabaseError param1DatabaseError) {}

                public void onDataChange(DataSnapshot param1DataSnapshot) {
                    String str = param1DataSnapshot.getValue().toString();
                    if (RecyclerViewAdapter.this.isPaid(str)) {
                        holder.active.setBackground(ContextCompat.getDrawable(RecyclerViewAdapter.this.context, R.drawable.circular_active_background));
                    } else {
                        holder.active.setBackground(ContextCompat.getDrawable(RecyclerViewAdapter.this.context, R.drawable.circular_deactivated_background));
                    }
                    holder.lastCleaned.setText(str + "");
                }
            });
//        if (((CarListItem)this.carItems.get(position)).getLeaveTime().equals("Before 6am") || ((CarListItem)this.carItems.get(position)).getLeaveTime().equals("7am-8am") || ((CarListItem)this.carItems.get(position)).getLeaveTime().equals("6am-7am"))
//            holder.background.setBackgroundDrawable(gd);
//            holder.background.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorAccentLight));
        Picasso.get().load(carItems.get(position).getPhoto()).transform((Transformation)new RoundedTransformation(60, 0)).into(holder.CarPhoto);
        FirebaseDatabase.getInstance().getReference(this.employeeType).child(this.auth.getUid()).child("working on").addValueEventListener(new ValueEventListener() {
            public void onCancelled(DatabaseError param1DatabaseError) {}

            public void onDataChange(DataSnapshot param1DataSnapshot) {
                if (param1DataSnapshot.getValue() != null && param1DataSnapshot.getValue().toString().equals(((CarListItem)RecyclerViewAdapter.this.carItems.get(position)).getNumber())) {
                    holder.CarNumber.setTextColor(ContextCompat.getColor(RecyclerViewAdapter.this.context, R.color.colorAccent));
                    holder.carName.setTextColor(ContextCompat.getColor(RecyclerViewAdapter.this.context, R.color.colorAccent));
                    final int[] timerValue = {0};
                    FirebaseDatabase.getInstance().getReference("Car Status").child(((CarListItem)RecyclerViewAdapter.this.carItems.get(position)).getNumber()).addValueEventListener(new ValueEventListener() {
                        public void onCancelled(DatabaseError param2DatabaseError) {}

                        public void onDataChange(DataSnapshot param2DataSnapshot) {
                            Log.d("RecyclerViewAdapter", "onDataChange: dataSnapshot : " + param2DataSnapshot);
                            if (param2DataSnapshot.child("category").getValue().toString().toLowerCase().equals("hatchback")) {
                                timerValue[0] = 1;
                            } else if (param2DataSnapshot.child("category").getValue().toString().toLowerCase().equals("sedan") || param2DataSnapshot.child("category").getValue().toString().toLowerCase().equals("luv") || param2DataSnapshot.child("category").getValue().toString().toLowerCase().equals("compactsedan")) {
                                timerValue[0] = 1;
                            } else if (param2DataSnapshot.child("category").getValue().toString().toLowerCase().equals("suv")) {
                                timerValue[0] = 1;
                            }
                            Double timeStamp = Double.valueOf(param2DataSnapshot.child("timeStamp").getValue().toString());
                            Double currentTimeStamp = Double.valueOf(System.currentTimeMillis());
                            Log.d("RecyclerViewAdapter", "onDataChange: difference is " + (timerValue[0] - (currentTimeStamp - timeStamp) / 60000.0D));
                            (new CountDownTimer((long)((long) (60000 * timerValue[0] - (currentTimeStamp - timeStamp))), 1000) {
                                public void onFinish() {
                                    if (!RecyclerViewAdapter.this.isInterior)
                                         holder.timer.setText("Time Finished");
                                }

                                public void onTick(long param3Long) {
                                    long seconds = param3Long / 1000;
                                    if (!RecyclerViewAdapter.this.isInterior)
                                        holder.timer.setText(String.format("%02d", seconds/60) + ":" + String.format("%02d", seconds%60));
                                }
                            }).start();
                        }
                    });
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(RecyclerViewAdapter.this.employeeType).child(RecyclerViewAdapter.this.auth.getUid()).child("working on");
//                if (!Connected) {
//                    Toast.makeText(RecyclerViewAdapter.this.context, "No INTERNET connection", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (SystemClock.elapsedRealtime() - RecyclerViewAdapter.this.lastClicked < RecyclerViewAdapter.this.toBeWait)
                    return;
                lastClicked = SystemClock.elapsedRealtime();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onCancelled(DatabaseError param2DatabaseError) {
                        Toast.makeText(RecyclerViewAdapter.this.context, "got an error", Toast.LENGTH_SHORT).show();
                    }

                    public void onDataChange(DataSnapshot param2DataSnapshot) {
                        try {
                            if (param2DataSnapshot.getValue().toString().equals(((CarListItem)carItems.get(position)).getNumber()) || param2DataSnapshot.getValue().toString().equals("")) {
                                Intent intent = new Intent(context,StartCarCleaningActivity.class);
//                                this(RecyclerViewAdapter.this.context, StartCarCleaningActivity.class);
                                if (RecyclerViewAdapter.this.isInterior)
                                    intent.putExtra("interior", "interior");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(RecyclerViewAdapter.this.context.getString(R.string.carNumber), holder.CarNumber.getText().toString());
                                if(isInterior){
                                    intent.putExtra(RecyclerViewAdapter.this.context.getString(R.string.Area), AreaKey.get(holder.CarNumber.getText().toString()));
                                }else{
                                    intent.putExtra(RecyclerViewAdapter.this.context.getString(R.string.Area), Area);
                                }
                                intent.putExtra("daysCar", RecyclerViewAdapter.this.daysCar);
                                RecyclerViewAdapter.this.context.startActivity(intent);
                                return;
                            }
                            Toast.makeText(RecyclerViewAdapter.this.context, "Please complete your previous task first", Toast.LENGTH_SHORT).show();
                        } catch (Exception exception) {
                            Log.d("RecyclerViewAdapter", "onDataChange: got the error while retrieving data = " + exception.getMessage());
                            Toast.makeText(RecyclerViewAdapter.this.context, "Problem exist in your account.Got error while retrieving data from the server", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        View view;
        if (this.isInterior) {
            view = LayoutInflater.from(this.context).inflate(R.layout.interior_car_item, paramViewGroup, false);
        } else {
            view = LayoutInflater.from(this.context).inflate(R.layout.car_item, paramViewGroup, false);
        }
        Log.d("RecyclerViewAdapter", "onCreateViewHolder: employeeType : " + this.employeeType);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView CarNumber;

        private ImageView CarPhoto;

        private ImageView active;

        private LinearLayout background;

        private TextView carName;

        private TextView lastCleaned;

        private TextView timer;

        public ViewHolder(View param1View) {
            super(param1View);
            this.CarNumber = (TextView)param1View.findViewById(R.id.carNumber);
            this.carName = (TextView)param1View.findViewById(R.id.carName);
            this.CarPhoto = (ImageView)param1View.findViewById(R.id.carPhoto);
            if (RecyclerViewAdapter.this.isInterior) {
                this.active = (ImageView)param1View.findViewById(R.id.active);
                this.lastCleaned = (TextView)param1View.findViewById(R.id.lastCleaned);
            } else {
                this.timer = (TextView)param1View.findViewById(R.id.timer);
            }
            this.background = (LinearLayout)param1View.findViewById(R.id.linearLayout);
        }
    }
}

