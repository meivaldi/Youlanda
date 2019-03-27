package com.meivaldi.youlanda.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.database.cart.Cart;
import com.meivaldi.youlanda.databinding.CheckoutBinding;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.MyViewHolder> {

    private Context context;
    private List<Cart> cartList;
    private LayoutInflater layoutInflater;

    public CheckoutAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }

        CheckoutBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.checkout, viewGroup, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        final Cart cart = cartList.get(i);

        holder.binding.setCart(cart);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckoutBinding binding;

        public MyViewHolder(CheckoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
