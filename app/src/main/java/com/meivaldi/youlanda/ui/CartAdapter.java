package com.meivaldi.youlanda.ui;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.database.cart.Cart;
import com.meivaldi.youlanda.databinding.CartItemBinding;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private List<Cart> cartList;
    private LayoutInflater layoutInflater;

    public CartAdapter(List<Cart> cartList) {
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }

        CartItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.cart_item, viewGroup, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Cart cart = cartList.get(i);

        myViewHolder.binding.setCart(cart);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final CartItemBinding binding;

        public MyViewHolder(final CartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
