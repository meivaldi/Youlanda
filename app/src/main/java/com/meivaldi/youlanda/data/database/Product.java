package com.meivaldi.youlanda.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "product")
public class Product {

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

    private boolean selected;

    public Product(int id, String nama, String foto, String harga, String jenis, String stok) {
        this.id = id;
        this.nama = nama;
        this.foto = foto;
        this.harga = harga;
        this.jenis = jenis;
        this.stok = stok;
        this.selected = false;
    }

    @Ignore
    public Product(String nama, String foto, String harga, String jenis, String stok) {
        this.nama = nama;
        this.foto = foto;
        this.harga = harga;
        this.jenis = jenis;
        this.stok = stok;
        this.selected = false;
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getFoto() {
        return foto;
    }

    public String getHarga() {
        return harga;
    }

    public String getJenis() {
        return jenis;
    }

    public String getStok() {
        return stok;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
