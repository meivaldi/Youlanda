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

    public Product(int id, String nama, String foto, String harga) {
        this.id = id;
        this.nama = nama;
        this.foto = foto;
        this.harga = harga;
    }

    @Ignore
    public Product(String nama, String foto, String harga) {
        this.nama = nama;
        this.foto = foto;
        this.harga = harga;
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

}
