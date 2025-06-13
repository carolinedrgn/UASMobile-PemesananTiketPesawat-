package com.example.ticketbookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketbookingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PassengerDataFormActivity extends AppCompatActivity {

    EditText editTextName, editTextIDNumber, editTextAddress; // Deklarasi input field untuk data penumpang
    Button buttonSubmit; // Tombol untuk submit data
    private FirebaseFirestore db; // Instance Firestore untuk akses database
    private FirebaseAuth mAuth; // Instance FirebaseAuth untuk autentikasi user
    private String bookingId; // Variabel untuk menyimpan ID pemesanan yang diteruskan dari activity sebelumnya

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PassengerDataForm", "onCreate dipanggil"); // Logging untuk debugging aktivitas onCreate
        setContentView(R.layout.activity_passenger_data_form); // Mengatur layout activity

        // Inisialisasi Firebase Firestore dan FirebaseAuth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Mengambil bookingId yang dikirim lewat Intent dari activity sebelumnya
        bookingId = getIntent().getStringExtra("bookingId");
        if (bookingId == null) {
            // Jika bookingId tidak ditemukan, tampilkan error dan hentikan activity
            Log.e("PassengerDataForm", "bookingId tidak ditemukan di intent");
            Toast.makeText(this, "Data pemesanan tidak valid", Toast.LENGTH_SHORT).show();
            finish(); // Mengakhiri activity saat ini
            return;
        }

        // Menghubungkan variabel dengan komponen di layout XML berdasarkan ID-nya
        editTextName = findViewById(R.id.editTextName);
        editTextIDNumber = findViewById(R.id.editTextIDNumber);
        editTextAddress = findViewById(R.id.editTextAddress);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Set listener ketika tombol Submit ditekan
        buttonSubmit.setOnClickListener(v -> {
            // Mengambil input dari user, lalu menghapus spasi di awal/akhir
            String name = editTextName.getText().toString().trim();
            String idNumber = editTextIDNumber.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();

            // Validasi input, jika ada field kosong tampilkan pesan error
            if (name.isEmpty() || idNumber.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data!", Toast.LENGTH_SHORT).show();
            } else {
                // Jika data lengkap, simpan ke Firestore
                savePassengerToFirestore(name, idNumber, address);
            }
        });
    }

    // Fungsi untuk menyimpan data penumpang ke Firestore
    private void savePassengerToFirestore(String name, String idNumber, String address) {
        FirebaseUser user = mAuth.getCurrentUser(); // Mendapatkan user yang sedang login

        if (user == null) {
            // Jika user belum login, beri notifikasi dan batalkan proses
            Toast.makeText(this, "Pengguna belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid(); // Mendapatkan UID user untuk referensi data

        // Membuat objek data penumpang dalam bentuk Map key-value
        Map<String, Object> passengerData = new HashMap<>();
        passengerData.put("name", name); // Nama penumpang
        passengerData.put("idNumber", idNumber); // Nomor identitas penumpang
        passengerData.put("address", address); // Alamat penumpang
        passengerData.put("userId", uid); // User ID pemilik data
        passengerData.put("bookingId", bookingId); // Booking ID untuk mengaitkan dengan pemesanan
        passengerData.put("createdAt", System.currentTimeMillis()); // Timestamp penyimpanan data

        // Menyimpan data ke koleksi "passengers" di Firestore
        db.collection("passengers")
                .add(passengerData)
                .addOnSuccessListener(documentReference -> {
                    // Jika berhasil simpan, tampilkan toast dan pindah ke activity konfirmasi pembayaran
                    Toast.makeText(this, "Data penumpang berhasil disimpan!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PassengerDataFormActivity.this, PaymentConfirmationActivity.class);
                    intent.putExtra("bookingId", bookingId); // Kirim bookingId ke activity berikutnya
                    startActivity(intent); // Mulai activity PaymentConfirmationActivity
                    finish(); // Tutup activity saat ini agar tidak kembali ke form data penumpang
                })
                .addOnFailureListener(e -> {
                    // Jika gagal simpan, tampilkan pesan error dengan informasi exception
                    Toast.makeText(this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
