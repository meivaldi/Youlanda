package com.meivaldi.youlanda.ui;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.database.product.Product;
import com.meivaldi.youlanda.databinding.ProductCardBinding;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> implements Filterable {

    private List<Product> productList;
    private List<Product> productListFiltered;
    private LayoutInflater layoutInflater;
    private ProductAdapterListener listener;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    productListFiltered = productList;
                } else {
                    List<Product> filteredList = new ArrayList<>();
                    for (Product product: productList) {
                        if (product.getNama().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(product);
                        }
                    }

                    productListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productListFiltered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                productListFiltered = (ArrayList<Product>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ProductCardBinding binding;

        public MyViewHolder(final ProductCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public ProductAdapter(List<Product> productList, ProductAdapterListener listener) {
        this.productList = productList;
        this.productListFiltered = productList;
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
        final Product product = productListFiltered.get(i);

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
        return productListFiltered.size();
    }

    public interface ProductAdapterListener {
        void onProductClicked(Product product);
    }

}
