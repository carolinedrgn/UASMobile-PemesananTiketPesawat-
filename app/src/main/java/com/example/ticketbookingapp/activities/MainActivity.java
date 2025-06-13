package com.example.ticketbookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketbookingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    // Deklarasi elemen UI dan Firebase
    private TextView textViewUserName; // Menampilkan nama pengguna
    private EditText editTextSearch; // Kolom pencarian kota
    private LinearLayout layoutPlane, layoutShip, layoutTrain, layoutHistory; // Menu transport dan histori
    private Button buttonLogout; // Tombol untuk logout
    private FirebaseAuth mAuth; // Instance untuk autentikasi Firebase
    private FirebaseFirestore db; // Instance Firestore untuk ambil data user
    private ImageView imageProfile; // Gambar profil untuk ke halaman profil

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set layout activity_main.xml sebagai tampilan

        // Inisialisasi Firebase Authentication dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inisialisasi elemen UI dari layout XML
        textViewUserName = findViewById(R.id.textViewUserName);
        editTextSearch = findViewById(R.id.editTextSearch);
        layoutPlane = findViewById(R.id.layoutPlane);
        layoutShip = findViewById(R.id.layoutShip);
        layoutTrain = findViewById(R.id.layoutTrain);
        layoutHistory = findViewById(R.id.layoutHistory);
        buttonLogout = findViewById(R.id.buttonLogout);
        imageProfile = findViewById(R.id.imageProfile); // Gambar profil

        // Cek apakah user sudah login
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Jika sudah login, ambil data user dari Firestore menggunakan UID
            loadUserData(user.getUid());
        } else {
            // Jika belum login, arahkan ke halaman Login
            Toast.makeText(this, "Silakan login terlebih dahulu!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // Tutup activity ini agar user tidak bisa kembali tanpa login
        }

        // Klik menu Pesawat → buka BookingActivity dan kirim jenis transportasi
        layoutPlane.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookingActivity.class);
            intent.putExtra("transportType", "Pesawat"); // Kirim jenis transport ke activity booking
            startActivity(intent);
        });

        // Klik menu Kapal → buka FAQActivity
        layoutShip.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FAQActivity.class);
            startActivity(intent);
        });

        // Klik menu Kereta → buka PromoBannerActivity
        layoutTrain.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PromoBannerActivity.class);
            startActivity(intent);
        });

        // Klik menu Riwayat → buka HistoryActivity
        layoutHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Ketika user tekan enter di kolom pencarian
        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            String searchQuery = editTextSearch.getText().toString().trim(); // Ambil teks pencarian
            if (!searchQuery.isEmpty()) {
                // Jika input tidak kosong, buka SearchResultsActivity dan kirim kota tujuan
                Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                intent.putExtra("searchQuery", searchQuery); // Kirim keyword pencarian
                intent.putExtra("transportType", "Pesawat"); // Default transportasi untuk hasil pencarian
                startActivity(intent);
            } else {
                // Jika kosong, tampilkan peringatan
                Toast.makeText(this, "Masukkan kota tujuan terlebih dahulu!", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        // Klik tombol logout
        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut(); // Logout dari Firebase
            Toast.makeText(this, "Logout berhasil!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class)); // Arahkan ke login
            finish(); // Tutup halaman utama agar tidak bisa kembali
        });

        // Klik gambar profil → buka ProfileActivity
        imageProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Saat kembali ke halaman ini, ambil ulang data user jika login
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            loadUserData(user.getUid());
        }
    }

    // Fungsi untuk mengambil data user dari Firestore
    private void loadUserData(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Ambil dan tampilkan nama pengguna dari Firestore
                        String username = documentSnapshot.getString("username");
                        if (username != null && !username.isEmpty()) {
                            textViewUserName.setText(username);
                        } else {
                            textViewUserName.setText("user"); // Jika username kosong
                        }
                    } else {
                        textViewUserName.setText("user"); // Jika dokumen user tidak ditemukan
                    }
                })
                .addOnFailureListener(e -> {
                    // Jika gagal ambil data, tampilkan log dan default teks
                    Log.e("MainActivity", "Gagal mengambil data user: " + e.getMessage());
                    textViewUserName.setText("user");
                });
    }
}

//Saat aplikasi dibuka, sistem memeriksa apakah pengguna sudah login menggunakan Firebase Authentication. Jika belum, pengguna diarahkan ke halaman login. Jika sudah login, aplikasi mengambil data nama pengguna dari Firebase Firestore dan menampilkannya di halaman utama.
//Di halaman utama terdapat beberapa menu: booking pesawat, halaman FAQ, promo banner, dan history pemesanan. Pengguna juga bisa mencari kota tujuan lewat kolom pencarian. Gambar profil dapat diklik untuk membuka halaman profil dan mengupdate, dan tersedia tombol logout untuk keluar dari akun.
//Semua data pengguna tersimpan dan diambil dari Firebase, sehingga aplikasi dapat menyesuaikan isi tampilan berdasarkan akun yang sedang login.

