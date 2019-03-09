package com.meivaldi.youlanda.data.database.product;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.annotations.SerializedName;

import com.meivaldi.youlanda.BR;

@Entity(tableName = "product")
public class Product extends BaseObservable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("nama_produk")
    private String nama;

    @SerializedName("foto_produk")
    private String foto;

    @SerializedName("harga_produk")
    private String harga;

    @SerializedName("jenis")
    private String jenis;

    @SerializedName("stok")
    private String stok;

    @SerializedName("diskon")
    private String diskon;

    private boolean selected;

    public Product(int id, String nama, String foto, String harga, String jenis, String stok, String diskon) {
        this.id = id;
        this.nama = nama;
        this.foto = foto;
        this.harga = harga;
        this.jenis = jenis;
        this.stok = stok;
        this.diskon = diskon;
        this.selected = false;
    }

    @Ignore
    public Product(String nama, String foto, String harga, String jenis, String stok, String diskon) {
        this.nama = nama;
        this.foto = foto;
        this.harga = harga;
        this.jenis = jenis;
        this.stok = stok;
        this.diskon = diskon;
        this.selected = false;
    }

    public int getId() {
        return id;
    }

    @Bindable
    public String getNama() {
        return nama;
    }

    @Bindable
    public String getFoto() {
        return foto;
    }

    @Bindable
    public String getHarga() {
        return harga;
    }

    @Bindable
    public String getJenis() {
        return jenis;
    }

    @Bindable
    public String getStok() {
        return stok;
    }

    @Bindable
    public String getDiskon() {
        return diskon;
    }

    @Bindable
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        notifyPropertyChanged(BR.selected);
    }

    public void setNama(String nama) {
        this.nama = nama;
        notifyPropertyChanged(BR.nama);
    }

    public void setHarga(String harga) {
        this.harga = harga;
        notifyPropertyChanged(BR.harga);
    }

    public void setStok(String stok) {
        this.stok = stok;
        notifyPropertyChanged(BR.stok);
    }

    @BindingAdapter({"android:productImage"})
    public static void loadImage(ImageView view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .apply(new RequestOptions().transform(new RoundedCorners(30)))
                .into(view);
    }
}
