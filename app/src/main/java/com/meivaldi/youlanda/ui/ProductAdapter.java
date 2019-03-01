package com.meivaldi.youlanda.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.database.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private List<Product> productList;
    private LayoutInflater layoutInflater;
    private Context context;
    private ProductAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail;
        private TextView nama, harga;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            nama = view.findViewById(R.id.nama_produk);
            harga = view.findViewById(R.id.harga_produk);
        }

    }

    public ProductAdapter(Context context, List<Product> productList, ProductAdapterListener listener) {
        this.productList = productList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }

        View view = layoutInflater.inflate(R.layout.product_card, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        final Product product = productList.get(i);
        myViewHolder.nama.setText(product.getNama());
        myViewHolder.harga.setText("Rp." + product.getHarga());
        myViewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProductClicked(product);
            }
        });

        Glide.with(context)
                .load(product.getFoto())
                .apply(new RequestOptions().transform(new RoundedCorners(30)))
                .into(myViewHolder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface ProductAdapterListener {
        void onProductClicked(Product product);
    }

}
