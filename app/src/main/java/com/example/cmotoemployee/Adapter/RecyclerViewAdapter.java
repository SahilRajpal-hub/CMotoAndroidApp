package com.example.cmotoemployee.Adapter;

import android.content.Context;
import android.content.Intent;
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
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private String Area;

    private boolean Connected = true;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    private List<CarListItem> carItems;

    private Context context;

    private String daysCar;

    private String employeeType;

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

    public RecyclerViewAdapter(List<CarListItem> paramList, Context paramContext, String paramString1, String paramString2, String paramString3) {
        this.carItems = paramList;
        this.context = paramContext;
        this.Area = paramString1;
        this.daysCar = paramString3;
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

    private boolean isPaid(String paramString) {
        Date date1;
        Date date2 = null;
        Date date3 = new Date();
        Date date4 = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date1 = simpleDateFormat.parse(paramString);
            date2 = date1;
            date4 = simpleDateFormat.parse(String.valueOf(date4));
            date2 = date4;
            date3 = date1;
            date1 = date2;
        } catch (ParseException parseException) {
            Log.d("RecyclerViewAdapter", "isPaid: error : " + parseException.getMessage());
            date1 = date3;
            date3 = date2;
        }
        Log.d("RecyclerViewAdapter", "isPaid: paid date :  month : " + date3.getMonth());
        Log.d("RecyclerViewAdapter", "isPaid: todays date : " + date1.getMonth());
        return (date3.getMonth() == date1.getMonth());
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
        if (((CarListItem)this.carItems.get(position)).getLeaveTime().equals("Before 6am") || ((CarListItem)this.carItems.get(position)).getLeaveTime().equals("7am-8am") || ((CarListItem)this.carItems.get(position)).getLeaveTime().equals("6am-7am"))
            holder.background.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorAccentLight));
        Picasso.get().load(((CarListItem)this.carItems.get(position)).getPhoto()).transform((Transformation)new RoundedTransformation(60, 0)).into(holder.CarPhoto);
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
                            Double double_2 = Double.valueOf(param2DataSnapshot.child("timeStamp").getValue().toString());
                            Double double_1 = Double.valueOf(System.currentTimeMillis());
                            Log.d("RecyclerViewAdapter", "onDataChange: difference is " + (timerValue[0] - (double_1.doubleValue() - double_2.doubleValue()) / 60000.0D));
                            (new CountDownTimer((long)((timerValue[0] * 60000) - double_1.doubleValue() - double_2.doubleValue()), 1000L) {
                                public void onFinish() {
                                    holder.timer.setText("Time Finished");
                                }

                                public void onTick(long param3Long) {
                                    param3Long /= 1000L;
                                    if (!RecyclerViewAdapter.this.isInterior)
                                        holder.timer.setText(String.format("%02d", new Object[] { Long.valueOf(param3Long / 60L) }) + ":" + String.format("%02d", new Object[] { Long.valueOf(param3Long % 60L) }));
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
                if (!Connected) {
                    Toast.makeText(RecyclerViewAdapter.this.context, "No INTERNET connection", Toast.LENGTH_SHORT).show();
                    return;
                }
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
                                intent.putExtra(RecyclerViewAdapter.this.context.getString(R.string.Area), RecyclerViewAdapter.this.Area);
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

