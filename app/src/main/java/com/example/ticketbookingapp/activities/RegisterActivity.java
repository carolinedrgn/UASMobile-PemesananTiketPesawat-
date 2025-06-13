package com.example.ticketbookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketbookingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // Komponen input & tombol yang ada di halaman register
    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonRegister;
    private TextView textViewLogin;

    // Firebase Auth = untuk autentikasi (login/register)
    // Firestore = untuk menyimpan data user ke database
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Tampilkan layout register

        // Inisialisasi Firebase Auth & Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Hubungkan variabel dengan komponen UI di layout XML
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);

        // Ketika tombol "Daftar" diklik, panggil fungsi registerUser()
        buttonRegister.setOnClickListener(v -> registerUser());

        // Kalau user klik "Sudah punya akun?", pindah ke halaman Login
        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Tutup halaman ini biar gak bisa balik pakai tombol back
        });
    }

    // Fungsi untuk mendaftarkan user
    private void registerUser() {
        // Ambil inputan dari EditText
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validasi: semua field harus diisi
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return; // Hentikan proses
        }

        // Register ke Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Kalau sukses, ambil objek user
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid(); // Ambil ID unik dari user
                            saveUserToFirestore(uid, username, email); // Simpan data tambahan ke Firestore
                        }
                    } else {
                        // Kalau gagal register, tampilkan pesan error
                        Toast.makeText(RegisterActivity.this, "Registrasi gagal: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Fungsi untuk menyimpan data user ke Firestore
    private void saveUserToFirestore(String uid, String username, String email) {
        // Buat data user dalam bentuk Map (key-value)
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username); // Simpan username
        userData.put("email", email); // Simpan email
        userData.put("createdAt", System.currentTimeMillis()); // Simpan waktu pendaftaran (timestamp)

        // Simpan ke koleksi 'users', dokumen berdasarkan UID
        db.collection("users")
                .document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    // Kalau berhasil disimpan, tampilkan pesan & pindah ke halaman login
                    Toast.makeText(RegisterActivity.this, "Registrasi berhasil! Data disimpan ke Firestore.",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Tutup halaman register
                })
                .addOnFailureListener(e -> {
                    // Kalau gagal simpan data ke Firestore
                    Toast.makeText(RegisterActivity.this, "Gagal menyimpan data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}
