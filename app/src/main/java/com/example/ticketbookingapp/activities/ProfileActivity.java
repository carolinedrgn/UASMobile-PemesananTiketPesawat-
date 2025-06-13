package com.example.ticketbookingapp.activities;

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

public class ProfileActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail; // Input untuk username dan email
    private Button buttonUpdate, buttonBackHome; // Tombol update dan tombol kembali
    private FirebaseAuth mAuth; // Autentikasi Firebase
    private FirebaseFirestore db; // Instance Firestore
    private String uid; // UID pengguna yang sedang login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Mengatur layout XML yang digunakan

        mAuth = FirebaseAuth.getInstance(); // Inisialisasi FirebaseAuth
        db = FirebaseFirestore.getInstance(); // Inisialisasi Firestore

        // Menghubungkan komponen layout ke variabel Java
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonBackHome = findViewById(R.id.buttonBackHome);

        // Mengecek apakah user sedang login
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid(); // Simpan UID user yang login
            loadUserData(uid); // Panggil fungsi untuk memuat data user dari Firestore
        } else {
            // Jika tidak ada user login, tampilkan pesan dan keluar dari activity
            Toast.makeText(this, "Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Tombol untuk update profil dipencet
        buttonUpdate.setOnClickListener(v -> updateUserProfile());

        // Tombol kembali ke home (menutup activity)
        buttonBackHome.setOnClickListener(v -> {
            finish(); // Menutup ProfileActivity, kembali ke activity sebelumnya
        });
    }

    // Fungsi untuk mengambil data user dari Firestore dan menampilkannya di EditText
    private void loadUserData(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Ambil data dari dokumen Firestore
                        String username = documentSnapshot.getString("username");
                        String email = documentSnapshot.getString("email");

                        // Tampilkan data di input form
                        editTextUsername.setText(username != null ? username : "");
                        editTextEmail.setText(email != null ? email : "");
                    }
                })
                .addOnFailureListener(e -> {
                    // Jika gagal mengambil data dari Firestore
                    Log.e("ProfileActivity", "Gagal memuat data: " + e.getMessage());
                    Toast.makeText(this, "Gagal memuat data profil", Toast.LENGTH_SHORT).show();
                });
    }

    // Fungsi untuk menyimpan perubahan profil ke Firestore
    private void updateUserProfile() {
        // Ambil input dari form
        String newUsername = editTextUsername.getText().toString().trim();
        String newEmail = editTextEmail.getText().toString().trim();

        // Validasi: pastikan tidak kosong
        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat map data yang akan diperbarui
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("username", newUsername);
        updatedData.put("email", newEmail);

        // Lakukan update ke Firestore
        db.collection("users").document(uid)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    // Jika update berhasil
                    Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    // Bisa panggil finish() di sini jika ingin kembali otomatis
                })
                .addOnFailureListener(e -> {
                    // Jika update gagal
                    Log.e("ProfileActivity", "Gagal update profil: " + e.getMessage());
                    Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                });
    }
}
