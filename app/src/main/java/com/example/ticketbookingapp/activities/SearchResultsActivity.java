package com.example.ticketbookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbookingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultsActivity extends AppCompatActivity {

    private TextView textViewSearchResult; // Untuk menampilkan teks hasil pencarian
    private RecyclerView recyclerViewSchedules; // Menampilkan daftar jadwal dalam bentuk list
    private ImageButton buttonBack; // Tombol kembali ke MainActivity
    private ScheduleAdapter scheduleAdapter; // Adapter untuk menampilkan data jadwal
    private List<Map<String, Object>> scheduleList; // List untuk menyimpan data jadwal dari Firestore
    private FirebaseFirestore db; // Instance Firestore (database)
    private FirebaseAuth mAuth; // Instance Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results); // Mengatur tampilan dari layout XML

        db = FirebaseFirestore.getInstance(); // Inisialisasi Firestore
        mAuth = FirebaseAuth.getInstance(); // Inisialisasi Firebase Auth

        // Debug: Verifikasi apakah Firebase berhasil dihubungkan
        try {
            db.collection("test").document("testDoc").get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Log.d("SearchResultsActivity", "Firebase koneksi berhasil!");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("SearchResultsActivity", "Firebase koneksi gagal: " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e("SearchResultsActivity", "Error inisialisasi Firebase: " + e.getMessage());
        }

        textViewSearchResult = findViewById(R.id.textViewSearchResult); // Hubungkan TextView dari layout
        recyclerViewSchedules = findViewById(R.id.recyclerViewSchedules); // Hubungkan RecyclerView dari layout
        buttonBack = findViewById(R.id.buttonBack); // Hubungkan tombol back dari layout

        // Setup tombol kembali ke MainActivity
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(SearchResultsActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Selesai agar tidak kembali ke activity ini saat tekan back
        });

        // Setup daftar jadwal menggunakan RecyclerView dan adapter
        scheduleList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(scheduleList, schedule -> {
            // Ketika item dijadwal diklik
            if (mAuth.getCurrentUser() == null) {
                // Jika user belum login
                Toast.makeText(this, "Silakan login terlebih dahulu!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SearchResultsActivity.this, LoginActivity.class));
                finish();
                return;
            }

            // Siapkan data pemesanan
            String userId = mAuth.getCurrentUser().getUid(); // Ambil ID user saat ini
            Map<String, Object> booking = new HashMap<>();
            booking.put("userId", userId);
            booking.put("scheduleId", schedule.get("id").toString());
            booking.put("departureTime", schedule.get("departureTime").toString());
            booking.put("arrivalTime", schedule.get("arrivalTime").toString());
            booking.put("price", schedule.get("price").toString());
            booking.put("airline", schedule.get("airline").toString());
            booking.put("departureCity", schedule.get("departureCity").toString());
            booking.put("destinationCity", schedule.get("destinationCity").toString());
            booking.put("departureDate", schedule.get("departureDate").toString());
            booking.put("transportType", schedule.get("transportType").toString());
            booking.put("bookingTime", System.currentTimeMillis()); // Waktu pemesanan saat ini
            booking.put("status", "pending"); // Status awal pemesanan

            // Simpan ke koleksi bookings di Firestore
            db.collection("bookings")
                    .add(booking)
                    .addOnSuccessListener(documentReference -> {
                        // Jika berhasil disimpan, lanjut ke form data penumpang
                        Toast.makeText(SearchResultsActivity.this, "Pemesanan berhasil!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SearchResultsActivity.this, PassengerDataFormActivity.class);
                        intent.putExtra("bookingId", documentReference.getId()); // Kirim ID booking ke activity berikutnya
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Jika gagal menyimpan ke Firestore
                        Toast.makeText(SearchResultsActivity.this, "Pemesanan gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        recyclerViewSchedules.setLayoutManager(new LinearLayoutManager(this)); // Tampilkan list secara vertikal
        recyclerViewSchedules.setAdapter(scheduleAdapter); // Pasangkan adapter ke RecyclerView

        // Ambil data pencarian dari MainActivity
        Intent intent = getIntent();
        String searchQuery = intent.getStringExtra("searchQuery");

        if (searchQuery != null && !searchQuery.isEmpty()) {
            // Format pencarian: huruf pertama kapital
            String formattedQuery = capitalizeFirstLetter(searchQuery.trim());
            textViewSearchResult.setText("Hasil pencarian untuk: " + formattedQuery);
            filterSchedules(formattedQuery); // Jalankan filter berdasarkan kota tujuan
        } else {
            Log.e("SearchResultsActivity", "Query kosong atau null");
            Toast.makeText(this, "Kota tujuan tidak valid!", Toast.LENGTH_SHORT).show();
            finish(); // Kembali jika tidak ada query
        }
    }

    // Fungsi untuk mencari jadwal berdasarkan kota tujuan
    private void filterSchedules(String destinationCity) {
        scheduleList.clear(); // Kosongkan list terlebih dahulu
        Log.d("SearchResultsActivity", "Memulai filter untuk destinationCity: " + destinationCity);

        db.collection("schedules")
                .whereEqualTo("destinationCity", destinationCity) // Filter hanya berdasarkan kota tujuan
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d("SearchResultsActivity", "Jumlah dokumen ditemukan: " + querySnapshot.size());
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Map<String, Object> schedule = new HashMap<>(document.getData()); // Ambil data tiap dokumen
                        schedule.put("id", document.getId()); // Simpan ID dokumen juga
                        scheduleList.add(schedule); // Tambahkan ke list
                        Log.d("SearchResultsActivity", "Jadwal ditemukan: " + schedule.toString());
                    }
                    if (scheduleList.isEmpty()) {
                        // Jika tidak ada hasil
                        Log.w("SearchResultsActivity", "Tidak ada jadwal untuk tujuan: " + destinationCity);
                        Toast.makeText(this, "Tidak ada jadwal untuk tujuan " + destinationCity, Toast.LENGTH_SHORT).show();
                    }
                    scheduleAdapter.notifyDataSetChanged(); // Perbarui tampilan
                })
                .addOnFailureListener(e -> {
                    // Jika gagal mengambil data dari Firestore
                    Log.e("SearchResultsActivity", "Gagal mengambil jadwal: " + e.getMessage());
                    Toast.makeText(this, "Gagal mengambil jadwal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Fungsi untuk mengubah huruf pertama menjadi kapital
    private String capitalizeFirstLetter(String text) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
