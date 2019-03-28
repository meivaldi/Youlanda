package com.meivaldi.youlanda.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.database.discount.Discount;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.MyViewHolder> {

    private Context context;
    private List<Discount> discountList;

    public DiscountAdapter(Context context, List<Discount> discountList) {
        this.context = context;
        this.discountList = discountList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.discount_item, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Discount discount = discountList.get(i);

        holder.discount.setText(discount.getDiscount() + "%");
    }

    @Override
    public int getItemCount() {
        return discountList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView discount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.discount = itemView.findViewById(R.id.discount);
        }
    }

}
