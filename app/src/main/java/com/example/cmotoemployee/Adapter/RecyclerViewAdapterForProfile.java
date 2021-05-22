package com.example.cmotoemployee.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cmotoemployee.Model.Payment;
import com.example.cmotoemployee.R;
import com.google.firebase.auth.FirebaseAuth;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecyclerViewAdapterForProfile extends RecyclerView.Adapter<RecyclerViewAdapterForProfile.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapterForP";

    FirebaseAuth auth = FirebaseAuth.getInstance();

    private List<Payment> carsPayment;

    private Context context;

    public RecyclerViewAdapterForProfile(List<Payment> paramList, Context paramContext) {
        this.carsPayment = paramList;
        this.context = paramContext;
    }

    public String getDay(String paramString) throws ParseException {
        Date date = (new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)).parse(paramString);
        Log.d("RecyclerViewAdapterForP", "getDay: date : " + date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String str = (new SimpleDateFormat("EEE", Locale.ENGLISH)).format(calendar.getTime());
        Log.d("RecyclerViewAdapterForP", "getDay: present day : " + str);
        return str.toUpperCase();
    }

    public int getItemCount() {
        return this.carsPayment.size();
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public int getItemViewType(int paramInt) {
        return paramInt;
    }

    public String getMonth(String paramString) throws ParseException {
        Date date = (new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)).parse(paramString);
        Log.d("RecyclerViewAdapterForP", "getMonth: date : " + date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String str = (new SimpleDateFormat("MMM", Locale.ENGLISH)).format(calendar.getTime());
        Log.d("RecyclerViewAdapterForP", "getMonth: present month : " + str);
        return str.toUpperCase();
    }

    public void onBindViewHolder(ViewHolder paramViewHolder, int paramInt) {
        try {
            TextView textView = paramViewHolder.dates;
            StringBuilder stringBuilder = new StringBuilder();
//            this();
            textView.setText(stringBuilder.append(((Payment)this.carsPayment.get(paramInt)).getDate().substring(8)).append(" ").append(getMonth(((Payment)this.carsPayment.get(paramInt)).getDate())).append(" ").append(((Payment)this.carsPayment.get(paramInt)).getDate().substring(0, 4)).toString());
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }
        try {
            TextView textView = paramViewHolder.day;
            StringBuilder stringBuilder = new StringBuilder();
//            this();
            textView.setText(stringBuilder.append(getDay(((Payment)this.carsPayment.get(paramInt)).getDate())).append("").toString());
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }
        paramViewHolder.price.setText("Rs. " + ((Payment)this.carsPayment.get(paramInt)).getPrice() + "");
        if (((Payment)this.carsPayment.get(paramInt)).getPayOn() != null && !((Payment)this.carsPayment.get(paramInt)).getPayOn().equals("due")) {
            try {
                TextView textView = paramViewHolder.paidOn;
                StringBuilder stringBuilder = new StringBuilder();
//                this();
                textView.setText(stringBuilder.append("Pay received on ").append(((Payment)this.carsPayment.get(paramInt)).getPayOn().substring(8)).append(" ").append(getMonth(((Payment)this.carsPayment.get(paramInt)).getPayOn())).toString());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
            Log.d("RecyclerViewAdapterForP", "onBindViewHolder: paid on :" + ((Payment)this.carsPayment.get(paramInt)).getPayOn());
        } else {
            paramViewHolder.paidOn.setText("Payment is due");
        }
        paramViewHolder.carsCleaned.setText(((Payment)this.carsPayment.get(paramInt)).getCarsCleaned() + " Cars");
    }

    public ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.payment_item, paramViewGroup, false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView carsCleaned;

        private TextView dates;

        private TextView day;

        private TextView paidOn;

        private TextView price;

        public ViewHolder(View param1View) {
            super(param1View);
            this.price = (TextView)param1View.findViewById(R.id.amount);
            this.dates = (TextView)param1View.findViewById(R.id.date);
            this.paidOn = (TextView)param1View.findViewById(R.id.paidOn);
            this.day = (TextView)param1View.findViewById(R.id.day);
            this.carsCleaned = (TextView)param1View.findViewById(R.id.carsCleaned);
        }
    }
}


