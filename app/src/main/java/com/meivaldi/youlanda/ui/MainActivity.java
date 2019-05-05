package com.meivaldi.youlanda.ui;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import android.view.LayoutInflater;
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
import com.meivaldi.youlanda.databinding.TutupKasirDialogBinding;
import com.meivaldi.youlanda.ui.fragment.AllProductFragment;
import com.meivaldi.youlanda.ui.fragment.DonutFragment;
import com.meivaldi.youlanda.ui.fragment.JamFragment;
import com.meivaldi.youlanda.ui.fragment.PastryFragment;
import com.meivaldi.youlanda.ui.fragment.SpongeFragment;
import com.meivaldi.youlanda.ui.fragment.TartFragment;
import com.meivaldi.youlanda.ui.interfaces.Swipeable;
import com.meivaldi.youlanda.utilities.BluetoothService;
import com.meivaldi.youlanda.utilities.CashierHandler;
import com.meivaldi.youlanda.utilities.Command;
import com.meivaldi.youlanda.utilities.InjectorUtils;
import com.meivaldi.youlanda.utilities.MyClickHandler;
import com.meivaldi.youlanda.utilities.PrintPicture;
import com.meivaldi.youlanda.utilities.PrinterCommand;
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
        TartFragment.TartFragmentListener,
        SpongeFragment.SpongeFragmentListener,
        DonutFragment.DonutFragmentListener,
        PastryFragment.PastryFragmentListener,
        JamFragment.JamFragmentListener,
        Runnable {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int CHAR_MAX = 32;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;
    public static final int MESSAGE_UNABLE_CONNECT = 7;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final int REQUEST_CHOSE_BMP = 3;
    public static final int REQUEST_CAMER = 4;

    public static final String CHINESE = "GBK";
    public static final String THAI = "CP874";
    public static final String KOREAN = "EUC-KR";
    public static final String BIG5 = "BIG5";

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
    private Dialog tutupKasirDialog;

    private BluetoothService mService = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private String mConnectedDeviceName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth tidak tersedia", Toast.LENGTH_SHORT).show();
            finish();
        }

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
                    session.setStarter(modal);
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
        viewPagerAdapter.addFrag(new TartFragment(this), "TART");
        viewPagerAdapter.addFrag(new SpongeFragment(this), "BOLU & CAKE");
        viewPagerAdapter.addFrag(new DonutFragment(this), "DONAT");
        viewPagerAdapter.addFrag(new PastryFragment(this), "KUE KERING");
        viewPagerAdapter.addFrag(new JamFragment(this), "SELAI, CERES & MARGARIN");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

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

        mService = new BluetoothService(this, mHandler);
        handler = new MyClickHandler(this, spinner, this, mService);
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
        } else if (id == R.id.tutup) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tutup_kasir_dialog, null, false);

            TutupKasirDialogBinding binding = DataBindingUtil.bind(view);

            tutupKasirDialog = new Dialog(MainActivity.this);
            tutupKasirDialog.setContentView(binding.getRoot());
            tutupKasirDialog.setCancelable(true);

            CashierHandler cashHandler = new CashierHandler(this, getApplicationContext(), tutupKasirDialog);
            binding.setHandlers(cashHandler);

            tutupKasirDialog.show();
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
    public void onPastryProductClicked(Product product) {
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
    public void onJamProductClicked(Product product) {
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
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tutup_kasir_dialog, null, false);

            TutupKasirDialogBinding binding = DataBindingUtil.bind(view);

            tutupKasirDialog = new Dialog(MainActivity.this);
            tutupKasirDialog.setContentView(binding.getRoot());
            tutupKasirDialog.setCancelable(false);

            CashierHandler cashHandler = new CashierHandler(this, getApplicationContext(), tutupKasirDialog);
            binding.setHandlers(cashHandler);

            tutupKasirDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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

    private void SendDataByte(byte[] data) {

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), "Printer tidak terhubung", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mService.write(data);
    }


    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:

                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_WRITE:

                    break;
                case MESSAGE_READ:

                    break;
                case MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "Terhubung ke " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MESSAGE_CONNECTION_LOST:
                    Toast.makeText(getApplicationContext(), "Koneksi Printer Terputus",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_UNABLE_CONNECT:
                    Toast.makeText(getApplicationContext(), "Tidak bisa terhubung ke perangkat",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void Init() {
        mService = new BluetoothService(this, mHandler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:{
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    if (BluetoothAdapter.checkBluetoothAddress(address)) {
                        BluetoothDevice device = mBluetoothAdapter
                                .getRemoteDevice(address);
                        mService.connect(device);
                    }
                }
                break;
            }
            case REQUEST_ENABLE_BT:{
                if (resultCode == Activity.RESULT_OK) {
                    Init();
                } else {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Bluetooth does not start, quit the program",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
            case REQUEST_CHOSE_BMP:{
                if (resultCode == Activity.RESULT_OK){

                }
                break;
            }
            case REQUEST_CAMER:{
                if (resultCode == Activity.RESULT_OK){

                }
                break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mService == null)
                Init();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mService != null) {

            if (mService.getState() == BluetoothService.STATE_NONE) {
                mService.start();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mService != null)
            mService.stop();
    }
}
