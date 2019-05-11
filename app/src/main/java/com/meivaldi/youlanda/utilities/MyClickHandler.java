package com.meivaldi.youlanda.utilities;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.meivaldi.youlanda.data.network.RetrofitClientInstance;
import com.meivaldi.youlanda.databinding.CashoutBinding;
import com.meivaldi.youlanda.databinding.MoneyDigitBinding;
import com.meivaldi.youlanda.databinding.SpecialDiskonDialogBinding;
import com.meivaldi.youlanda.ui.CheckoutAdapter;
import com.meivaldi.youlanda.ui.DeviceListActivity;
import com.meivaldi.youlanda.ui.DiscountAdapter;
import com.meivaldi.youlanda.ui.MainActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyClickHandler {
    private Context context;
    private Dialog purchaseDialog;
    private Dialog moneyDialog;
    private AppCompatSpinner spinner;
    private AppCompatActivity activity;

    private BluetoothService bluetoothService;
    private BluetoothAdapter mBluetoothAdapter = null;

    public MyClickHandler(Context context, AppCompatSpinner spinner, AppCompatActivity activity, BluetoothService service) {
        this.context = context;
        this.spinner = spinner;
        this.activity = activity;

        bluetoothService = service;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void purchase(Order order) {
        if (mBluetoothAdapter.isEnabled()) {
            if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED) {
                List<Cart> cartList = order.getCartList();

                ProductRepository repository = InjectorUtils.provideRepository(context);
                List<Product> selectedProduct = new ArrayList<>();

                for (Cart cart : cartList) {
                    Product product = cart.getProduct();
                    product.setSelected(false);

                    repository.updateProduct(product);
                    selectedProduct.add(product);
                }

                GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
                Call<Product> call;

                Call<Order> save = service.saveOrder(order.getId(), order.getCashier(), order.getWaiter(), order.getTime(), order.getJenis(),
                        order.getTotal(), order.getDiskon(), order.getSpecial_discount(), order.getTax(), order.getPrice(),
                        order.getCash());
                save.enqueue(new Callback<Order>() {
                    @Override
                    public void onResponse(Call<Order> call, Response<Order> response) {
                        Toast.makeText(context, "Transaksi Berhasil", Toast.LENGTH_SHORT).show();
                        spinner.setSelection(0);
                    }

                    @Override
                    public void onFailure(Call<Order> call, Throwable t) {
                        Toast.makeText(context, "Transaksi Gagal", Toast.LENGTH_SHORT).show();
                    }
                });

                //print(order);
                changeModal(order.getPrice());

                List<Cart> temp = order.getCartList();
                int total = 0;

                for (Cart cart : temp) {
                    total += cart.getQuantity();
                }

                SharedPreferences preferences = context.getSharedPreferences(SessionManager.PREF_NAME, 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(SessionManager.KEY_PRODUCT, preferences.getInt(SessionManager.KEY_PRODUCT, 0) + total);
                editor.putInt(SessionManager.KEY_TRANSACTION, preferences.getInt(SessionManager.KEY_TRANSACTION, 0) + 1);
                editor.commit();

                order.getCartList().clear();
                order.setCartSum(0);
                order.setDiskon();
                order.setDiscount(new Discount(0));
                order.setSpecial_discount();
                order.setTotal();
                order.setTax();
                order.setPrice();
                order.setWaiter("");
                order.setId(order.getId() + 1);

                for (int i = 0; i < selectedProduct.size(); i++) {
                    Product selected = selectedProduct.get(i);
                    Log.d("TES", selected.getStok() + " " + selected.getNama());
                    call = service.saveProduct(Integer.valueOf(selected.getStok()), selected.getNama());

                    call.enqueue(new Callback<Product>() {
                        @Override
                        public void onResponse(Call<Product> call, Response<Product> response) {
                            //Toast.makeText(context, "Berhasil", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Product> call, Throwable t) {
                            //Toast.makeText(context, "Gagal", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                purchaseDialog.dismiss();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Printer tidak terhubung, hidupkan printer?")
                        .setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                        .setNegativeButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent serverIntent = new Intent(context, DeviceListActivity.class);
                                activity.startActivityForResult(serverIntent, MainActivity.REQUEST_CONNECT_DEVICE);
                            }
                        }).create();

                builder.show();
            }
        } else {
            Toast.makeText(context, "Hidupkan bluetooth terlebih dahulu!", Toast.LENGTH_LONG).show();
        }
    }

    public void print(Order order) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_bw);
        int nMode = 0;
        int nPaperWidth = 384;

        if (bitmap != null) {
            try {
                byte[] data = PrintPicture.POS_PrintBMP(bitmap, nPaperWidth, nMode);

                SendDataByte(Command.ESC_Init);
                SendDataByte(Command.LF);
                SendDataByte(data);
                SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(30));
                SendDataByte(PrinterCommand.POS_Set_Cut(1));
                SendDataByte(PrinterCommand.POS_Set_PrtInit());

                SendDataByte("\n".getBytes("GBK"));
                Command.ESC_Align[2] = 0x02;
                SendDataByte(Command.ESC_Align);
                Command.GS_ExclamationMark[2] = 0x05;
                SendDataByte(Command.GS_ExclamationMark);
                SendDataString(order.getTime());
                SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(48));
                SendDataByte(Command.GS_V_m_n);

                Command.ESC_Align[2] = 0x11;
                SendDataByte(Command.ESC_Align);
                ProductRepository repository = InjectorUtils.provideRepository(context);
                Karyawan karyawan = repository.getKaryawan();
                SendDataString(karyawan.getUnit());

                Command.ESC_Align[2] = 0x00;
                SendDataByte(Command.ESC_Align);
                Command.GS_ExclamationMark[2] = 0x05;
                SendDataByte(Command.GS_ExclamationMark);
                String info = "Nomor Order: #" + order.getId() +
                        "\nJenis Order: " + order.getJenis() +
                        "\nKasir      : " + order.getCashier() +
                        "\nPelayan    : " + order.getWaiter();
                String details = "\n\nNama Barang     @       Harga\n";

                String orderDetails = "";
                int len1 = "Nama Barang".length();
                int len2 = "@".length();
                int len3 = "Harga".length();

                for (Cart cart: order.getCartList()) {
                    String temp = cart.getProduct().getNama();
                    String temp2 = DataBindingUtils.currencyConvert(
                            Integer.valueOf(cart.getProduct().getHarga()) * cart.getQuantity());

                    int gap1 = 0;
                    int gap2 = gap2 = (14 - temp2.length()) + 2;;

                    if (cart.getProduct().getNama().length() >= 14) {
                        temp = insertString(cart.getProduct().getNama(), "\n", 14);
                        gap1 = (len1 - (temp.length() % 14)) + 8;
                    } else {
                        gap1 = 17 - temp.length();
                    }

                    String spasi1 = "";
                    for (int i=0; i<gap1-1; i++) {
                        spasi1 += " ";
                    }

                    String spasi2 = "";
                    for (int i=0; i<gap2-1; i++) {
                        spasi2 += " ";
                    }

                    Log.d("TAG2", "Len1 : " + len1);
                    Log.d("TAG2", "Gap1: " + gap1);
                    Log.d("TAG2", "Gap2: " + gap2);
                    Log.d("TAG2", "Spasi1: " + spasi1.length());
                    Log.d("TAG2", "Spasi2: " + spasi2.length());

                    orderDetails += temp + spasi1 + cart.getQuantity() + spasi2 + DataBindingUtils.currencyConvert(
                            Integer.valueOf(cart.getProduct().getHarga()) * cart.getQuantity()) + "\n";
                }

                int operand1 = DataBindingUtils.currencyConvert(order.getTotal()).length() + "Sub Total:".length();
                int sp1 = MainActivity.CHAR_MAX - operand1;
                String space1 = "";
                for (int i=0; i<sp1-1; i++) {
                    space1 += " ";
                }

                int operand2 = DataBindingUtils.currencyConvert(order.getDiskon()).length() + "Diskon".length();
                int sp2 = MainActivity.CHAR_MAX - operand2;
                String space2 = "";
                for (int i=0; i<sp2-1; i++) {
                    space2 += " ";
                }

                int operand3 = DataBindingUtils.currencyConvert(order.getSpecial_discount()).length() + "Diskon Spesial".length();
                int sp3 = MainActivity.CHAR_MAX - operand3;
                String space3 = "";
                for (int i=0; i<sp3-1; i++) {
                    space3 += " ";
                }

                int operand4 = DataBindingUtils.currencyConvert(order.getTax()).length() + "Ppn(10%)".length();
                int sp4 = MainActivity.CHAR_MAX - operand4;
                String space4 = "";
                for (int i=0; i<sp4-1; i++) {
                    space4 += " ";
                }

                int operand5 = DataBindingUtils.currencyConvert(order.getPrice()).length() + "Total Tagihan".length();
                int sp5 = MainActivity.CHAR_MAX - operand5;
                String space5 = "";
                for (int i=0; i<sp5-1; i++) {
                    space5 += " ";
                }

                int operand6 = DataBindingUtils.currencyConvert(order.getCash()).length() + "Dibayar".length();
                int sp6 = MainActivity.CHAR_MAX - operand6;
                String space6 = "";
                for (int i=0; i<sp6-1; i++) {
                    space6 += " ";
                }

                int operand7 = DataBindingUtils.getReturn(order).length() + "Kembalian".length();
                int sp7 = MainActivity.CHAR_MAX - operand7;
                String space7 = "";
                for (int i=0; i<sp7-1; i++) {
                    space7 += " ";
                }

                String money = "\nSub Total: " + space1 + DataBindingUtils.currencyConvert(order.getTotal()) + "\n" +
                        "Diskon " + space2 + DataBindingUtils.currencyConvert(order.getDiskon()) + "\n" +
                        "Diskon Spesial " + space3 + DataBindingUtils.currencyConvert(order.getSpecial_discount()) + "\n" +
                        "Ppn(10%) " + space4 + DataBindingUtils.currencyConvert(order.getTax()) + "\n" +
                        "Total Tagihan " + space5 + DataBindingUtils.currencyConvert(order.getPrice()) + "\n" +
                        "Dibayar " + space6 + DataBindingUtils.currencyConvert(order.getCash()) + "\n" +
                        "Kembalian " + space7 + DataBindingUtils.getReturn(order) + "\n";

                SendDataByte(info.getBytes("GBK"));
                SendDataByte(details.getBytes("GBK"));
                SendDataByte(orderDetails.getBytes("GBK"));
                SendDataByte(money.getBytes("GBK"));
                SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(25));
                Log.d("TAG", space1.length() + " " + space2.length()
                        + " " + space3.length()
                        + " " + space4.length()
                        + " " + space5.length()
                        + " " + space6.length()
                        + " " + space7.length());

                Log.d("TAG", sp1 + " " + sp2
                        + " " + sp3
                        + " " + sp4
                        + " " + sp5
                        + " " + sp6
                        + " " + sp7);

                Log.d("TAG", operand1 + " " + operand2
                        + " " + operand3
                        + " " + operand4
                        + " " + operand5
                        + " " + operand6
                        + " " + operand7);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public static String insertString(String originalString, String stringToBeInserted,
            int index) {
        String newString = originalString.substring(0, index + 1)
                + stringToBeInserted
                + originalString.substring(index + 1);

        return newString;
    }

    private void SendDataString(String data) {
        if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(context, "Printer tidak terhubung", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (data.length() > 0) {
            try {
                bluetoothService.write(data.getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void SendDataByte(byte[] data) {

        if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(context, "Printer tidak terhubung", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        bluetoothService.write(data);
    }

    private void changeModal(int price) {
        SharedPreferences preferences = context.getSharedPreferences(SessionManager.PREF_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();

        int modal = preferences.getInt(SessionManager.KEY_FINAL, 0);
        int newValue = modal + price;
        editor.putInt(SessionManager.KEY_FINAL, newValue);
        editor.commit();
    }

    public void setMoney(Order order, int cash) {
        if (cash < order.getPrice()) {
            Toast.makeText(context, "Uang tidak cukup", Toast.LENGTH_SHORT).show();
            return;
        } else {
            order.setCash(cash);
            moneyDialog.dismiss();
            onPurchaseClicked(order);
        }
    }

    public void onPurchaseClicked(Order order) {
        View view = LayoutInflater.from(context).inflate(R.layout.cashout, null, false);

        CashoutBinding binding = DataBindingUtil.bind(view);
        binding.setOrder(order);
        binding.setHandlers(this);

        purchaseDialog = new Dialog(context);
        purchaseDialog.setContentView(binding.getRoot());
        purchaseDialog.setCancelable(false);
        purchaseDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        RecyclerView recyclerView = binding.productList;
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        CheckoutAdapter adapter = new CheckoutAdapter(context, order.getCartList());
        recyclerView.setAdapter(adapter);

        purchaseDialog.show();
    }

    public void onCancleClicked(View view) {
        purchaseDialog.dismiss();
    }

    public void getMoney(Order order) {
        moneyDialog.dismiss();

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.cash_dialog);
        dialog.setCancelable(true);

        EditText cashET = dialog.findViewById(R.id.cash);
        Button button = dialog.findViewById(R.id.bayar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cash = Integer.valueOf(cashET.getText().toString());
                order.setCash(cash);

                if (cash < order.getPrice()) {
                    Toast.makeText(context, "Uang tidak cukup", Toast.LENGTH_SHORT).show();

                    return;
                }

                onPurchaseClicked(order);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showCash(Order order) {
        if (order.getJenis() == "Jenis Order") {
            Toast.makeText(context, "Silahkan pilih jenis order terlebih dahulu!", Toast.LENGTH_LONG).show();
            return;
        } else if (order.getWaiter() == "") {
            Toast.makeText(context, "Silahkan pilih pelayan terlebih dahulu!", Toast.LENGTH_LONG).show();
            return;
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.money_digit, null, false);

            MoneyDigitBinding binding = DataBindingUtil.bind(view);
            binding.setOrder(order);
            binding.setHandlers(this);

            moneyDialog = new Dialog(context);
            moneyDialog.setContentView(binding.getRoot());
            moneyDialog.setCancelable(true);

            moneyDialog.show();
        }
    }

    public void showDiscount(Order order) {
        View view = LayoutInflater.from(context).inflate(R.layout.special_diskon_dialog, null, false);

        SpecialDiskonDialogBinding binding = DataBindingUtil.bind(view);

        Dialog specialDiscount = new Dialog(context);
        specialDiscount.setContentView(binding.getRoot());
        specialDiscount.setCancelable(true);

        List<Discount> discounts = new ArrayList<>();
        DiscountAdapter adapter = new DiscountAdapter(context, discounts);

        RecyclerView discountList = binding.discountList;
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
        discountList.setLayoutManager(mLayoutManager);
        discountList.setItemAnimator(new DefaultItemAnimator());
        discountList.addOnItemTouchListener(new RecyclerTouchListener(context, discountList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Discount discount = discounts.get(position);
                order.setDiscount(new Discount(discount.getDiscount()));
                order.setSpecial_discount();
                order.setPrice();
                specialDiscount.dismiss();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        discounts.add(new Discount(5));
        discounts.add(new Discount(10));
        discounts.add(new Discount(20));

        discountList.setAdapter(adapter);

        specialDiscount.show();
    }

}

