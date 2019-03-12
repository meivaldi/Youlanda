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
import com.meivaldi.youlanda.data.ProductRepository;
import com.meivaldi.youlanda.data.database.cart.Cart;
import com.meivaldi.youlanda.data.database.order.Order;
import com.meivaldi.youlanda.data.database.product.Product;
import com.meivaldi.youlanda.databinding.CartItemBinding;
import com.meivaldi.youlanda.utilities.InjectorUtils;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private Context context;
    private List<Cart> cartList;
    private Order order;
    private LayoutInflater layoutInflater;

    public CartAdapter(Context context, List<Cart> cartList, Order order) {
        this.context = context;
        this.cartList = cartList;
        this.order = order;
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

        ProductRepository repository = InjectorUtils.provideRepository(context);

        myViewHolder.tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = cart.getProduct();
                int quantity = cart.getQuantity() + 1;
                int stok = Integer.valueOf(product.getStok());

                if ((stok-1) >= 0) {
                    stok -= 1;
                } else {
                    Toast.makeText(context, "Stok tidak cukup!", Toast.LENGTH_SHORT).show();
                    return;
                }

                product.setStok(String.valueOf(stok));
                cart.setQuantity(quantity);
                order.setTotal();
                order.setTax();
                order.setDiskon();
                order.setPrice();

                repository.updateProduct(product);
            }
        });

        myViewHolder.kurang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = cart.getProduct();
                int quantity = cart.getQuantity();
                int stok = Integer.valueOf(product.getStok());

                if (quantity <= 1) {
                    Toast.makeText(context, "Minimal order harus 1 produk", Toast.LENGTH_SHORT).show();
                } else {
                    quantity -= 1;
                    stok += 1;

                    product.setStok(String.valueOf(stok));
                    cart.setQuantity(quantity);
                    order.setTotal();
                    order.setTax();
                    order.setDiskon();
                    order.setPrice();
                }

                repository.updateProduct(product);
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
