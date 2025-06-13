package com.example.ticketbookingapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    // Deklarasi input dan tombol yang ada di layout login
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;

    // Firebase Auth untuk login, Firestore untuk ambil data user
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Tampilkan layout login

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Hubungkan variabel dengan komponen di layout
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        // Kalau tombol Login ditekan, jalankan fungsi loginUser()
        buttonLogin.setOnClickListener(v -> loginUser());

        // Kalau tulisan "Register" ditekan, pindah ke halaman Register
        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // Tutup halaman login
        });
    }

    private void loginUser() {
        // Ambil input email & password dari user
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Cek kalau input belum diisi
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password harus diisi!", Toast.LENGTH_SHORT).show();
            return; // Hentikan fungsi
        }

        // Proses login dengan Firebase Auth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Kalau login berhasil, ambil user yang sedang login
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid(); // Dapatkan UID-nya

                        // Ambil data tambahan dari Firestore berdasarkan UID
                        db.collection("users").document(uid).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // Ambil username dari Firestore
                                        String username = documentSnapshot.getString("username");

                                        // Simpan data ke penyimpanan lokal (SharedPreferences)
                                        SharedPreferences sharedPref = getSharedPreferences("UserData", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("username", username);
                                        editor.putString("email", user.getEmail());
                                        editor.apply(); // Simpan data

                                        // Pindah ke halaman utama
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Kalau data user di Firestore tidak ditemukan
                                        Toast.makeText(LoginActivity.this, "Data user tidak ditemukan!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Kalau gagal ambil data dari Firestore
                                    Toast.makeText(LoginActivity.this, "Gagal mengambil data user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Kalau login gagal (salah password, email tidak terdaftar, dll.)
                        Toast.makeText(LoginActivity.this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Cek kalau sebelumnya user sudah login (masih aktif)
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid(); // Ambil UID user

            // Ambil data user dari Firestore
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Ambil username dari Firestore
                            String username = documentSnapshot.getString("username");

                            // Simpan data user ke SharedPreferences (auto-login)
                            SharedPreferences sharedPref = getSharedPreferences("UserData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("username", username);
                            editor.putString("email", mAuth.getCurrentUser().getEmail());
                            editor.apply();

                            // Pindah ke MainActivity secara otomatis
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Tutup LoginActivity
                        }
                    });
        }
    }
}
