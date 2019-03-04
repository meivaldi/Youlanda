package com.meivaldi.youlanda.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.database.cart.Cart;
import com.meivaldi.youlanda.databinding.CartItemBinding;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private Context context;
    private List<Cart> cartList;
    private LayoutInflater layoutInflater;

    public CartAdapter(Context context, List<Cart> cartList) {
        this.context = context;
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
        myViewHolder.binding.setProduct(cart.getProduct());

        myViewHolder.tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = cart.getQuantity() + 1;
                cart.setQuantity(quantity);
            }
        });

        myViewHolder.kurang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = cart.getQuantity();

                if (quantity <= 1) {
                    Toast.makeText(context, "Minimal order harus 1 produk", Toast.LENGTH_SHORT).show();
                } else {
                    quantity -= 1;
                    cart.setQuantity(quantity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final CartItemBinding binding;
        private final LinearLayout tambah, kurang;

        public MyViewHolder(final CartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            tambah = binding.tambah;
            kurang = binding.kurang;
        }
    }
}
