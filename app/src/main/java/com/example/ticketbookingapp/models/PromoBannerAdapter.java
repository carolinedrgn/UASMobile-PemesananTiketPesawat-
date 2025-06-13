package com.example.ticketbookingapp.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticketbookingapp.R;

import java.util.List;

// Adapter untuk menampilkan daftar promo banner dalam bentuk RecyclerView
public class PromoBannerAdapter extends RecyclerView.Adapter<PromoBannerAdapter.ViewHolder> {

    // Daftar resource ID gambar banner yang akan ditampilkan
    private List<Integer> bannerList;

    // Konstruktor untuk mengisi daftar banner
    public PromoBannerAdapter(List<Integer> bannerList) {
        this.bannerList = bannerList;
    }

    // Dipanggil ketika ViewHolder baru perlu dibuat (inflate layout item)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout XML item_promo_banner.xml menjadi objek View
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promo_banner, parent, false);
        // Kembalikan ViewHolder yang berisi tampilan tersebut
        return new ViewHolder(v);
    }

    // Dipanggil untuk mengisi data ke dalam ViewHolder sesuai posisi item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Ambil resource ID gambar dari daftar berdasarkan posisi
        // dan set ke ImageView di dalam ViewHolder
        holder.imageBanner.setImageResource(bannerList.get(position));
    }

    // Mengembalikan jumlah total item di dalam adapter
    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    // ViewHolder adalah class yang merepresentasikan satu item tampilan (item promo)
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Komponen tampilan untuk menampilkan gambar banner
        ImageView imageBanner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ambil referensi ke ImageView dari layout item
            imageBanner = itemView.findViewById(R.id.imageBanner);
        }
    }
}
