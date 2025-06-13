package com.example.ticketbookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbookingapp.R;
import com.example.ticketbookingapp.models.PromoBannerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PromoBannerActivity extends AppCompatActivity {

    RecyclerView recyclerView; // Komponen RecyclerView untuk menampilkan banner promo
    PromoBannerAdapter adapter; // Adapter untuk mengatur bagaimana data promo ditampilkan
    List<Integer> bannerList; // List yang menyimpan ID resource gambar promo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_banner); // Mengatur layout XML activity ini

        recyclerView = findViewById(R.id.recyclerViewBanner); // Menghubungkan RecyclerView dari layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Mengatur layout manager secara vertikal

        bannerList = new ArrayList<>(); // Inisialisasi list untuk gambar banner promo

        // Menambahkan resource gambar banner ke dalam list
        bannerList.add(R.drawable.promo1);
        bannerList.add(R.drawable.promo2);
        bannerList.add(R.drawable.promo3);
        bannerList.add(R.drawable.promo4);
        bannerList.add(R.drawable.promo5);
        bannerList.add(R.drawable.promo6);

        adapter = new PromoBannerAdapter(bannerList); // Membuat instance adapter dengan list gambar
        recyclerView.setAdapter(adapter); // Menyambungkan adapter ke RecyclerView

        // Button kembali ke MainActivity
        Button buttonBack = findViewById(R.id.buttonBackHomePromo); // Menghubungkan tombol dari layout
        buttonBack.setOnClickListener(v -> { // Saat tombol ditekan
            Intent intent = new Intent(PromoBannerActivity.this, MainActivity.class); // Buat intent untuk pindah ke MainActivity
            startActivity(intent); // Jalankan MainActivity
            finish(); // Tutup PromoBannerActivity
        });
    }
}
