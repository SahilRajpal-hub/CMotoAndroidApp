package com.example.cmotoemployee.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cmotoemployee.CollectPaymentActivity;
import com.example.cmotoemployee.R;

import java.util.List;

public class RecyclerViewAdapterForPayment extends RecyclerView.Adapter<RecyclerViewAdapterForPayment.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapterForP";

    private String Area;

    private List<String> carNumbers;

    private List<Integer> carsPayment;

    private Context context;

    public RecyclerViewAdapterForPayment(List<String> paramList, List<Integer> paramList1, Context paramContext, String paramString) {
        this.carsPayment = paramList1;
        this.carNumbers = paramList;
        this.context = paramContext;
        this.Area = paramString;
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

    public void onBindViewHolder(ViewHolder paramViewHolder, final int position) {
        paramViewHolder.carNumber.setText((String)this.carNumbers.get(position) + "");
        paramViewHolder.amount.setText((new StringBuilder()).append(this.carsPayment.get(position)).append("").toString());
        paramViewHolder.carNumber.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                Intent intent = new Intent(RecyclerViewAdapterForPayment.this.context, CollectPaymentActivity.class);
                intent.putExtra("carNumber", RecyclerViewAdapterForPayment.this.carNumbers.get(position));
                intent.putExtra("area", RecyclerViewAdapterForPayment.this.Area);
                intent.putExtra("price", RecyclerViewAdapterForPayment.this.carsPayment.get(position));
                RecyclerViewAdapterForPayment.this.context.startActivity(intent);
            }
        });
    }

    public ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.interior_payment_item, paramViewGroup, false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView amount;

        private TextView carNumber;

        public ViewHolder(View param1View) {
            super(param1View);
            this.carNumber = (TextView)param1View.findViewById(R.id.carNumber);
            this.amount = (TextView)param1View.findViewById(R.id.amount);
        }
    }
}

