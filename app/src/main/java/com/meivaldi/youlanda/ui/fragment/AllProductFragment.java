package com.meivaldi.youlanda.ui.fragment;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.ProductRepository;
import com.meivaldi.youlanda.data.database.product.Product;
import com.meivaldi.youlanda.databinding.FragmentAllProductBinding;
import com.meivaldi.youlanda.ui.MainActivity;
import com.meivaldi.youlanda.ui.MainActivityViewModel;
import com.meivaldi.youlanda.ui.MainViewModelFactory;
import com.meivaldi.youlanda.ui.ProductAdapter;
import com.meivaldi.youlanda.utilities.InjectorUtils;

import java.util.List;

@SuppressLint("ValidFragment")
public class AllProductFragment extends Fragment implements ProductAdapter.ProductAdapterListener {

    private MainActivityViewModel viewModel;
    private FragmentAllProductBinding binding;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private SearchView searchView;
    private AllProductFragmentListener listener;

    @SuppressLint("ValidFragment")
    public AllProductFragment(AllProductFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_product, container, false);

        recyclerView = binding.productList;
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        MainViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(getContext(), "semua");
        viewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);

        viewModel.getProductList().observe(this, products -> {
            this.productList = products;
            adapter = new ProductAdapter(productList, this);
            recyclerView.setAdapter(adapter);
        });

        View view = binding.getRoot();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (this.isVisible()) {
            if (!isVisibleToUser) {
                ProductRepository repository = InjectorUtils.provideRepository(getContext());
                for (Product product: productList) {
                    repository.updateProduct(product);
                }
            }
        }
    }

    @Override
    public void onProductClicked(Product product) {
        int stok = Integer.valueOf(product.getStok());

        if (stok <= 0) {
            if (product.isSelected()) {
                product.setSelected(product.isSelected() ? false : true);

                listener.onAllProductClicked(product);
            } else {
                Toast.makeText(getContext(), "Stok habis!", Toast.LENGTH_SHORT).show();
            }
        } else {
            product.setSelected(product.isSelected() ? false : true);

            listener.onAllProductClicked(product);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public interface AllProductFragmentListener {
        void onAllProductClicked(Product product);
    }
}
