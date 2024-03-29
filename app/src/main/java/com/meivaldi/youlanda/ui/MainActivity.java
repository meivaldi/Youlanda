package com.meivaldi.youlanda.ui;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.database.cart.Cart;
import com.meivaldi.youlanda.data.database.order.Order;
import com.meivaldi.youlanda.data.database.product.Product;
import com.meivaldi.youlanda.databinding.ActivityMainBinding;
import com.meivaldi.youlanda.ui.fragment.BreadFragment;
import com.meivaldi.youlanda.ui.fragment.SpongeFragment;
import com.meivaldi.youlanda.ui.fragment.TartFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        BreadFragment.BreadFragmentListener,
        TartFragment.TartFragmentListener,
        SpongeFragment.SpongeFragmentListener {

    private RecyclerView cart;
    private CartAdapter cartAdapter;
    private List<Cart> cartList;
    private Order order;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Toolbar toolbar = binding.content.toolbar;
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);

        DrawerLayout drawer = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        cart = binding.content.recyclerView;
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cart.setLayoutManager(layoutManager);
        cart.setHasFixedSize(true);

        cartList = new ArrayList<>();
        order = new Order(cartList);

        cartAdapter = new CartAdapter(getApplicationContext(), cartList, order);
        cart.setAdapter(cartAdapter);

        binding.setOrder(order);

        loadFragment(new BreadFragment(this));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.bread) {
            loadFragment(new BreadFragment(this));
        } else if (id == R.id.tart) {
            loadFragment(new TartFragment(this));
        } else if (id == R.id.sponge) {
            loadFragment(new SpongeFragment(this));
        }

        DrawerLayout drawer = binding.drawerLayout;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private int getIndex(List<Cart> cartList, String nama) {
        int index = -1;

        for (Cart cart: cartList) {
            if (cart.getProduct().getNama().equals(nama)) {
                index = cartList.indexOf(cart);
            }
        }

        return index;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBreadProductClicked(Product product) {
        if (product.isSelected()) {
            cartList.add(new Cart(product, 1));
        } else {
            int index = getIndex(cartList, product.getNama());
            cartList.remove(index);
        }

        order.setCartSum(cartList.size());
        order.setTotal();
        order.setTax();
        order.setPrice();

        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSpongeProductListener(Product product) {
        if (product.isSelected()) {
            cartList.add(new Cart(product, 1));
        } else {
            int index = getIndex(cartList, product.getNama());
            cartList.remove(index);
        }

        order.setCartSum(cartList.size());
        order.setTotal();
        order.setTax();
        order.setPrice();

        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTartProductClicked(Product product) {
        if (product.isSelected()) {
            cartList.add(new Cart(product, 1));
        } else {
            int index = getIndex(cartList, product.getNama());
            cartList.remove(index);
        }

        order.setCartSum(cartList.size());
        order.setTotal();
        order.setTax();
        order.setPrice();

        cartAdapter.notifyDataSetChanged();
    }
}
