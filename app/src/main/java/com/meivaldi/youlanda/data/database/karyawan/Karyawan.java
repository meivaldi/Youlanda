package com.meivaldi.youlanda.data.database.karyawan;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.meivaldi.youlanda.BR;

import java.io.Serializable;

@Entity(tableName = "karyawan")
public class Karyawan extends BaseObservable implements Serializable {

    @PrimaryKey
    @SerializedName("id")
    int id;

    @SerializedName("nik")
    String nik;

    @SerializedName("nama")
    String nama;

    public Karyawan(int id, String nik, String nama) {
        this.id = id;
        this.nik = nik;
        this.nama = nama;
    }

    @Ignore
    public Karyawan(String nik, String nama) {
        this.nik = nik;
        this.nama = nama;
    }

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
        notifyPropertyChanged(BR.nama);
    }

    @Bindable
    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
        notifyPropertyChanged(BR.nik);
    }
}
