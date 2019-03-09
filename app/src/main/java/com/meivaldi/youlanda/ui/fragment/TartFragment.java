package com.meivaldi.youlanda.ui.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.ProductRepository;
import com.meivaldi.youlanda.data.database.product.Product;
import com.meivaldi.youlanda.databinding.FragmentTartBinding;
import com.meivaldi.youlanda.ui.MainActivityViewModel;
import com.meivaldi.youlanda.ui.MainViewModelFactory;
import com.meivaldi.youlanda.ui.ProductAdapter;
import com.meivaldi.youlanda.utilities.InjectorUtils;

@SuppressLint("ValidFragment")
public class TartFragment extends Fragment implements ProductAdapter.ProductAdapterListener {

    private MainActivityViewModel viewModel;
    private FragmentTartBinding binding;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;

    private TartFragmentListener listener;

    @SuppressLint("ValidFragment")
    public TartFragment(TartFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tart, container, false);

        recyclerView = binding.productList;
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        MainViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(getContext(), "tar");
        viewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);

        viewModel.getProductList().observe(this, products -> {
            adapter = new ProductAdapter(products, this);
            recyclerView.setAdapter(adapter);
        });

        View view = binding.getRoot();
        return view;
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onProductClicked(Product product) {
        int stok = Integer.valueOf(product.getStok());

        if (stok <= 0) {
            if (product.isSelected()) {
                product.setSelected(product.isSelected() ? false : true);
                ProductRepository repository = InjectorUtils.provideRepository(getContext());
                repository.updateProduct(product);

                listener.onTartProductClicked(product);
            } else {
                Toast.makeText(getContext(), "Stok habis!", Toast.LENGTH_SHORT).show();
            }
        } else {
            product.setSelected(product.isSelected() ? false : true);
            ProductRepository repository = InjectorUtils.provideRepository(getContext());
            repository.updateProduct(product);

            listener.onTartProductClicked(product);
        }
    }

    public interface TartFragmentListener {
        void onTartProductClicked(Product product);
    }

}
