package com.meivaldi.youlanda.ui;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.database.product.Product;
import com.meivaldi.youlanda.databinding.ProductCardBinding;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private List<Product> productList;
    private LayoutInflater layoutInflater;
    private ProductAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ProductCardBinding binding;

        public MyViewHolder(final ProductCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public ProductAdapter(List<Product> productList, ProductAdapterListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }

        ProductCardBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.product_card, viewGroup, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        final Product product = productList.get(i);

        myViewHolder.binding.setProduct(product);
        myViewHolder.binding.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProductClicked(product);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface ProductAdapterListener {
        void onProductClicked(Product product);
    }

}
