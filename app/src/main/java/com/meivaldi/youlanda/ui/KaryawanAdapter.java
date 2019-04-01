package com.meivaldi.youlanda.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.database.karyawan.Karyawan;

import java.util.List;

public class KaryawanAdapter extends RecyclerView.Adapter<KaryawanAdapter.MyViewHolder> {

    private Context context;
    private List<Karyawan> karyawanList;

    public KaryawanAdapter(Context context, List<Karyawan> karyawanList) {
        this.context = context;
        this.karyawanList = karyawanList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.discount_item, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Karyawan karyawan = karyawanList.get(i);

        holder.nama.setText(karyawan.getNama());
    }

    @Override
    public int getItemCount() {
        return karyawanList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nama;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.discount);
        }
    }

}
