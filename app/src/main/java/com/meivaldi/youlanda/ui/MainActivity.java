package com.meivaldi.youlanda.ui;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.ProductRepository;
import com.meivaldi.youlanda.data.database.cart.Cart;
import com.meivaldi.youlanda.data.database.discount.Discount;
import com.meivaldi.youlanda.data.database.karyawan.Karyawan;
import com.meivaldi.youlanda.data.database.order.Order;
import com.meivaldi.youlanda.data.database.product.Product;
import com.meivaldi.youlanda.data.network.GetDataService;
import com.meivaldi.youlanda.data.network.OrderNumber;
import com.meivaldi.youlanda.data.network.RetrofitClientInstance;
import com.meivaldi.youlanda.databinding.ActivityMainBinding;
import com.meivaldi.youlanda.ui.fragment.AllProductFragment;
import com.meivaldi.youlanda.ui.fragment.BreadFragment;
import com.meivaldi.youlanda.ui.fragment.BrowniesFragment;
import com.meivaldi.youlanda.ui.fragment.DonutFragment;
import com.meivaldi.youlanda.ui.fragment.SpongeFragment;
import com.meivaldi.youlanda.ui.fragment.TartFragment;
import com.meivaldi.youlanda.utilities.InjectorUtils;
import com.meivaldi.youlanda.utilities.MyClickHandler;
import com.meivaldi.youlanda.utilities.RecyclerTouchListener;
import com.meivaldi.youlanda.utilities.SessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        AllProductFragment.AllProductFragmentListener,
        BreadFragment.BreadFragmentListener,
        TartFragment.TartFragmentListener,
        SpongeFragment.SpongeFragmentListener,
        DonutFragment.DonutFragmentListener,
        BrowniesFragment.BrowniesFragmentListener,
        Runnable {

    private RecyclerView cart;
    private CartAdapter cartAdapter;
    private List<Cart> cartList;
    private List<Karyawan> employeeList;
    private Order order;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ActivityMainBinding binding;
    private AppCompatSpinner spinner;
    private MyClickHandler handler;
    private Dialog karyawanDialog;
    private SessionManager session;
    private ProductRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        boolean isInitialized = getSharedPreferences(session.PREF_NAME, session.PRIVATE_MODE).getBoolean(session.KEY_IS_INITIALIZED, false);

        if (!isInitialized) {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.modal_awal);
            dialog.setCancelable(false);

            EditText modalET = dialog.findViewById(R.id.modal);
            Button button = dialog.findViewById(R.id.confirm);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int modal = Integer.valueOf(modalET.getText().toString());
                    order.setStarter(modal);
                    session.setInitialized(true);
                    dialog.dismiss();
                }
            });

            dialog.show();
        }

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

        tabLayout = binding.content.tabs;
        viewPager = binding.content.viewpager;

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFrag(new AllProductFragment(this), "Semua");
        viewPagerAdapter.addFrag(new BreadFragment(this), "Roti");
        viewPagerAdapter.addFrag(new TartFragment(this), "Tar");
        viewPagerAdapter.addFrag(new SpongeFragment(this), "Bolu");
        viewPagerAdapter.addFrag(new DonutFragment(this), "Donut");
        viewPagerAdapter.addFrag(new BrowniesFragment(this), "Brownies");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        cart = binding.content.recyclerView;
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cart.setLayoutManager(layoutManager);
        cart.setHasFixedSize(true);

        cartList = new ArrayList<>();
        order = new Order(cartList, new Date());

        repository = InjectorUtils.provideRepository(getApplicationContext());

        cartAdapter = new CartAdapter(getApplicationContext(), cartList, order);
        cart.setAdapter(cartAdapter);

        List<String> jenisOrder = new ArrayList<>();
        jenisOrder.add("Jenis Order");
        jenisOrder.add("Beli Langsung");
        jenisOrder.add("Ojek Online");

        spinner = binding.content.jenisOrder;
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.custom_spinner, jenisOrder);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String jenis = jenisOrder.get(position);
                order.setJenis(jenis);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        employeeList = new ArrayList<>();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Karyawan>> call = service.getAllKaryawan();

        call.enqueue(new Callback<List<Karyawan>>() {
            @Override
            public void onResponse(Call<List<Karyawan>> call, Response<List<Karyawan>> response) {
                employeeList = response.body();
            }

            @Override
            public void onFailure(Call<List<Karyawan>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });

        Call<OrderNumber> number = service.getOrderNumber();
        number.enqueue(new Callback<OrderNumber>() {
            @Override
            public void onResponse(Call<OrderNumber> call, Response<OrderNumber> response) {
                int order_number = response.body().getId();
                order.setId(order_number+1);
            }

            @Override
            public void onFailure(Call<OrderNumber> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
            }
        });

        String jenis = spinner.getSelectedItem().toString();
        order.setJenis(jenis);
        order.setCashier(repository.getKaryawan().getNama());

        handler = new MyClickHandler(this);
        binding.setHandlers(handler);

        Date date = repository.getNormalizedUtcDateForToday();

        Log.d("TANGGAL", date.toString());

        Thread thread = new Thread(this);
        thread.start();

        binding.setOrder(order);
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

        if (id == R.id.pelayan) {
            karyawanDialog = new Dialog(this);
            karyawanDialog.setContentView(R.layout.daftar_karyawan);
            karyawanDialog.setCancelable(false);

            RecyclerView daftarKaryawan = karyawanDialog.findViewById(R.id.recycler_view);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
            daftarKaryawan.setLayoutManager(mLayoutManager);
            daftarKaryawan.setItemAnimator(new DefaultItemAnimator());
            daftarKaryawan.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), daftarKaryawan, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    order.setWaiter(employeeList.get(position).getNama());
                    karyawanDialog.dismiss();
                }

                @Override
                public void onLongClick(View view, int position) {
                    order.setWaiter(employeeList.get(position).getNama());
                    karyawanDialog.dismiss();
                }
            }));

            KaryawanAdapter adapter = new KaryawanAdapter(getApplicationContext(), employeeList);
            daftarKaryawan.setAdapter(adapter);

            karyawanDialog.show();
        }

        DrawerLayout drawer = binding.drawerLayout;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private int getIndex(List<Cart> cartList, String nama) {
        int index = -1;

        for (Cart cart : cartList) {
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
        int stok = Integer.valueOf(product.getStok());

        if (product.isSelected()) {
            cartList.add(new Cart(product, 1));

            stok -= 1;
            product.setStok(String.valueOf(stok));
        } else {
            int index = getIndex(cartList, product.getNama());

            stok += cartList.get(index).getQuantity();
            product.setStok(String.valueOf(stok));

            cartList.remove(index);
        }

        order.setCartSum(cartList.size());
        order.setTotal();
        order.setTax();
        order.setDiskon();
        order.setSpecial_discount();
        order.setDiscount(new Discount(0));
        order.setPrice();

        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSpongeProductListener(Product product) {
        int stok = Integer.valueOf(product.getStok());

        if (product.isSelected()) {
            cartList.add(new Cart(product, 1));

            stok -= 1;
            product.setStok(String.valueOf(stok));
        } else {
            int index = getIndex(cartList, product.getNama());

            stok += cartList.get(index).getQuantity();
            product.setStok(String.valueOf(stok));

            cartList.remove(index);
        }

        order.setCartSum(cartList.size());
        order.setTotal();
        order.setTax();
        order.setDiskon();
        order.setSpecial_discount();
        order.setDiscount(new Discount(0));
        order.setPrice();

        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTartProductClicked(Product product) {
        int stok = Integer.valueOf(product.getStok());

        if (product.isSelected()) {
            cartList.add(new Cart(product, 1));

            stok -= 1;
            product.setStok(String.valueOf(stok));
        } else {
            int index = getIndex(cartList, product.getNama());

            stok += cartList.get(index).getQuantity();
            product.setStok(String.valueOf(stok));

            cartList.remove(index);
        }

        order.setCartSum(cartList.size());
        order.setTotal();
        order.setTax();
        order.setDiskon();
        order.setSpecial_discount();
        order.setDiscount(new Discount(0));
        order.setPrice();

        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAllProductClicked(Product product) {
        int stok = Integer.valueOf(product.getStok());

        if (product.isSelected()) {
            cartList.add(new Cart(product, 1));

            stok -= 1;
            product.setStok(String.valueOf(stok));
        } else {
            int index = getIndex(cartList, product.getNama());

            stok += cartList.get(index).getQuantity();
            product.setStok(String.valueOf(stok));

            cartList.remove(index);
        }

        order.setCartSum(cartList.size());
        order.setTotal();
        order.setTax();
        order.setDiskon();
        order.setSpecial_discount();
        order.setDiscount(new Discount(0));
        order.setPrice();

        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDonutProductClicked(Product product) {
        int stok = Integer.valueOf(product.getStok());

        if (product.isSelected()) {
            cartList.add(new Cart(product, 1));

            stok -= 1;
            product.setStok(String.valueOf(stok));
        } else {
            int index = getIndex(cartList, product.getNama());

            stok += cartList.get(index).getQuantity();
            product.setStok(String.valueOf(stok));

            cartList.remove(index);
        }

        order.setCartSum(cartList.size());
        order.setTotal();
        order.setTax();
        order.setDiskon();
        order.setSpecial_discount();
        order.setDiscount(new Discount(0));
        order.setPrice();

        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBrowniesProductClicked(Product product) {
        int stok = Integer.valueOf(product.getStok());

        if (product.isSelected()) {
            cartList.add(new Cart(product, 1));

            stok -= 1;
            product.setStok(String.valueOf(stok));
        } else {
            int index = getIndex(cartList, product.getNama());

            stok += cartList.get(index).getQuantity();
            product.setStok(String.valueOf(stok));

            cartList.remove(index);
        }

        order.setCartSum(cartList.size());
        order.setTotal();
        order.setTax();
        order.setDiskon();
        order.setSpecial_discount();
        order.setDiscount(new Discount(0));
        order.setPrice();

        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            logoutUser(repository.getKaryawan());
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser(Karyawan karyawan) {
        repository.deleteKaryawan(karyawan);
        session.setLogin(false);
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public void run() {
        int hours, minutes, seconds;

        Date date = new Date();
        hours = date.getHours();
        minutes = date.getMinutes();
        seconds = date.getSeconds();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            seconds++;

            if (seconds >= 60) {
                seconds = 0;
                minutes++;
            }

            if (minutes >= 60) {
                minutes = 0;
                hours++;
            }

            String sHours="", sMinutes ="", sSeconds="";

            if (hours < 10) {
                sHours = "0" + String.valueOf(hours);
            } else {
                sHours = String.valueOf(hours);
            }

            if (minutes < 10) {
                sMinutes = "0" + String.valueOf(minutes);
            } else {
                sMinutes = String.valueOf(minutes);
            }

            if (seconds < 10) {
                sSeconds = "0" + String.valueOf(seconds);
            } else {
                sSeconds = String.valueOf(seconds);
            }

            String clock = sHours + ":" + sMinutes + ":" + sSeconds;
            order.setTime(clock);
        }
    }

}
