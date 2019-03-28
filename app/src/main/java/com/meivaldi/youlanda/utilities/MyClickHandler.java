package com.meivaldi.youlanda.utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.ProductRepository;
import com.meivaldi.youlanda.data.database.cart.Cart;
import com.meivaldi.youlanda.data.database.discount.Discount;
import com.meivaldi.youlanda.data.database.order.Order;
import com.meivaldi.youlanda.data.database.product.Product;
import com.meivaldi.youlanda.data.network.GetDataService;
import com.meivaldi.youlanda.data.network.RetrofitClientInstance;
import com.meivaldi.youlanda.databinding.CashoutBinding;
import com.meivaldi.youlanda.databinding.SpecialDiskonDialogBinding;
import com.meivaldi.youlanda.ui.CheckoutAdapter;
import com.meivaldi.youlanda.ui.DiscountAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyClickHandler {

    private Context context;
    private Dialog purchaseDialog;

    public MyClickHandler(Context context) {
        this.context = context;
    }

    public void purchase(Order order) {
        List<Cart> cartList = order.getCartList();

        ProductRepository repository = InjectorUtils.provideRepository(context);
        List<Product> selectedProduct = new ArrayList<>();

        for (Cart cart: cartList) {
            Product product = cart.getProduct();
            product.setSelected(false);

            repository.updateProduct(product);
            selectedProduct.add(product);
        }

        order.getCartList().clear();
        order.setCartSum(0);
        order.setDiskon();
        order.setTotal();
        order.setTax();
        order.setPrice();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Product> call;

        for (int i=0; i<selectedProduct.size(); i++) {
            Product selected = selectedProduct.get(i);
            Log.d("TES", selected.getStok() + " " + selected.getNama());
            call = service.saveProduct(Integer.valueOf(selected.getStok()), selected.getNama());

            call.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    Toast.makeText(context, "Berhasil", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    Toast.makeText(context, "Gagal", Toast.LENGTH_SHORT).show();
                }
            });
        }

        purchaseDialog.dismiss();
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

