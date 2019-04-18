package com.meivaldi.youlanda.utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.meivaldi.youlanda.BR;
import com.meivaldi.youlanda.data.ProductRepository;
import com.meivaldi.youlanda.ui.LoginActivity;

public class CashierHandler extends BaseObservable {

    private AppCompatActivity activity;
    private Context context;
    private Dialog closeCashier;

    public CashierHandler(AppCompatActivity activity, Context context, Dialog closeCashier) {
        this.activity = activity;
        this.context = context;
        this.closeCashier = closeCashier;
    }

    public void closeCashier(View view) {
        closeCashier.dismiss();

        ProductRepository repository = InjectorUtils.provideRepository(context);
        repository.deleteKaryawan(repository.getKaryawan());

        SharedPreferences preferences = context.getSharedPreferences(SessionManager.PREF_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(SessionManager.KEY_STARTER, 0);
        editor.putInt(SessionManager.KEY_FINAL, 0);

        SessionManager session = new SessionManager(context);
        session.setLogin(false);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        activity.finish();
    }

    @Bindable
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
        notifyPropertyChanged(BR.context);
    }

}
