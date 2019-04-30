package com.meivaldi.youlanda.data.database.product;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.meivaldi.youlanda.BR;

@Entity(tableName = "product")
public class Product extends BaseObservable {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("kode_produk")
    @Expose
    private String kode;

    @SerializedName("nama_produk")
    @Expose
    private String nama;

    @SerializedName("foto_produk")
    @Expose
    private String foto;

    @SerializedName("harga_produk")
    @Expose
    private String harga;

    @SerializedName("jenis")
    @Expose
    private String jenis;

    @SerializedName("stok")
    @Expose
    private String stok;

    @SerializedName("diskon")
    @Expose
    private String diskon;

    private boolean selected;

    public Product(int id, String nama, String foto, String harga, String jenis, String stok, String diskon, String kode) {
        this.id = id;
        this.nama = nama;
        this.foto = foto;
        this.harga = harga;
        this.jenis = jenis;
        this.stok = stok;
        this.diskon = diskon;
        this.selected = false;
        this.kode = kode;
    }

    /*@Ignore
    public Product(String nama, String foto, String harga, String jenis, String stok, String diskon) {
        this.nama = nama;
        this.foto = foto;
        this.harga = harga;
        this.jenis = jenis;
        this.stok = stok;
        this.diskon = diskon;
        this.selected = false;
    }
*/
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

    @Bindable
    public String getKode() {
        return kode;
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

    public void setKode(String kode) {
        this.kode = kode;
        notifyPropertyChanged(BR.kode);
    }

    @BindingAdapter({"android:productImage"})
    public static void loadImage(ImageView view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .apply(new RequestOptions().transform(new RoundedCorners(30)))
                .into(view);
    }
}
