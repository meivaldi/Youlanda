package com.meivaldi.youlanda.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "product")
public class Product {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int id;

    @SerializedName("nama")
    private String nama;

    @SerializedName("foto")
    private String foto;

    @SerializedName("jenis")
    private String jenis;

    @SerializedName("harga")
    private double harga;

    @SerializedName("stok")
    private int stok;

    public Product(int id, String nama, String foto, String jenis, double harga, int stok) {
        this.id = id;
        this.nama = nama;
        this.foto = foto;
        this.jenis = jenis;
        this.harga = harga;
        this.stok = stok;
    }

    @Ignore
    public Product(String nama, String foto, String jenis, double harga, int stok) {
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

    public double getHarga() {
        return harga;
    }

    public int getStok() {
        return stok;
    }
}
