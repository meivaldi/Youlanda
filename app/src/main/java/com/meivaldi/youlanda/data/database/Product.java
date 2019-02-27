package com.meivaldi.youlanda.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "product")
public class Product {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nama, foto, jenis;
    private double harga;

    public Product(int id, String nama, String foto, String jenis, double harga) {
        this.id = id;
        this.nama = nama;
        this.foto = foto;
        this.jenis = jenis;
        this.harga = harga;
    }

    @Ignore
    public Product(String nama, String foto, String jenis, double harga) {
        this.nama = nama;
        this.foto = foto;
        this.jenis = jenis;
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

    public String getJenis() { return jenis; }

    public double getHarga() {
        return harga;
    }
}
