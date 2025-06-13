package com.example.ticketbookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbookingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Activity ini menampilkan riwayat pemesanan pengguna yang login
public class HistoryActivity extends AppCompatActivity {

    // Komponen UI
    private RecyclerView recyclerViewHistory;
    private TextView textViewNoHistory;
    private ImageButton buttonBack;

    // Adapter dan data list
    private HistoryAdapter historyAdapter;
    private List<Map<String, Object>> bookingList;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history); // Atur layout activity_history.xml sebagai tampilan

        // Inisialisasi elemen UI
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        textViewNoHistory = findViewById(R.id.textViewNoHistory);
        buttonBack = findViewById(R.id.buttonBack);

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inisialisasi list pemesanan
        bookingList = new ArrayList<>();

        // Inisialisasi adapter dengan implementasi klik tombol hapus dan konfirmasi
        historyAdapter = new HistoryAdapter(bookingList,
                bookingId -> db.collection("bookings").document(bookingId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Jika penghapusan berhasil, tampilkan pesan dan muat ulang data
                            Toast.makeText(HistoryActivity.this, "Riwayat berhasil dihapus!", Toast.LENGTH_SHORT).show();
                            loadBookingHistory();
                        })
                        .addOnFailureListener(e -> {
                            // Jika gagal hapus, tampilkan pesan error
                            Toast.makeText(HistoryActivity.this, "Gagal menghapus riwayat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }),
                booking -> {
                    // Saat tombol konfirmasi ditekan, buka PaymentConfirmationActivity dan kirim ID booking
                    Intent intent = new Intent(HistoryActivity.this, PaymentConfirmationActivity.class);
                    intent.putExtra("bookingId", booking.get("id").toString());
                    startActivity(intent);
                });

        // Atur layout manager dan adapter untuk RecyclerView
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistory.setAdapter(historyAdapter);

        // Saat tombol kembali ditekan, kembali ke halaman sebelumnya
        buttonBack.setOnClickListener(v -> onBackPressed());

        // Muat data riwayat pemesanan
        loadBookingHistory();
    }

    // Fungsi untuk mengambil riwayat pemesanan dari Firestore
    private void loadBookingHistory() {
        FirebaseUser user = mAuth.getCurrentUser(); // Ambil user yang sedang login
        if (user != null) {
            String userId = user.getUid(); // Ambil UID user

            // Query Firestore untuk mendapatkan semua booking dengan userId sesuai
            db.collection("bookings")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        // Bersihkan list lama
                        bookingList.clear();

                        // Loop setiap dokumen hasil query
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Map<String, Object> booking = document.getData(); // Ambil data pemesanan
                            booking.put("id", document.getId()); // Tambahkan ID dokumen ke data
                            bookingList.add(booking); // Tambahkan ke daftar
                        }

                        // Jika tidak ada data, tampilkan teks “Tidak ada riwayat”
                        textViewNoHistory.setVisibility(bookingList.isEmpty() ? View.VISIBLE : View.GONE);

                        // Beri tahu adapter bahwa datanya berubah
                        historyAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        // Jika gagal mengambil data, tampilkan pesan error dan log
                        Log.e("HistoryActivity", "Gagal mengambil riwayat: " + e.getMessage());
                        Toast.makeText(this, "Gagal memuat riwayat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
