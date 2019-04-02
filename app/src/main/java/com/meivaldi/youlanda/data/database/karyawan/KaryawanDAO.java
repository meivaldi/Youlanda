package com.meivaldi.youlanda.data.database.karyawan;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface KaryawanDAO {

    @Query("SELECT * FROM karyawan")
    LiveData<List<Karyawan>> getAllKaryawan();

    @Insert
    void insertKaryawan(Karyawan karyawan);

    @Query("SELECT * FROM karyawan")
    Karyawan getKaryawan();

    @Delete
    void deleteKaryawan(Karyawan karyawan);

}
