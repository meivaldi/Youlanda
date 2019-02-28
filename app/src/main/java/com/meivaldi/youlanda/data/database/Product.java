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

    private String jenis;

    @SerializedName("harga_produk")
    private String harga;

    private int stok;

    public Product(int id, String nama, String foto, String jenis, String harga, int stok) {
        this.id = id;
        this.nama = nama;
        this.foto = foto;
        this.jenis = jenis;
        this.harga = harga;
        this.stok = stok;
    }

    @Ignore
    public Product(String nama, String foto, String jenis, String harga, int stok) {
        this.nama = nama;
        this.foto = foto;
        this.jenis = jenis;
        this.harga = harga;
        this.stok = stok;
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

    public String getJenis() { return jenis; }

    public String getHarga() {
        return harga;
    }

    public int getStok() {
        return stok;
    }
}
