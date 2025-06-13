package com.example.ticketbookingapp.activities; // Package tempat activity ini berada

import android.content.Intent; // Untuk berpindah dari satu activity ke activity lain
import android.os.Bundle; // Untuk menyimpan data saat activity dijalankan
import android.os.Handler; // Untuk memberi jeda waktu (delay)
import android.view.animation.Animation; // Untuk membuat efek animasi
import android.view.animation.AnimationUtils; // Untuk mengambil file animasi dari folder res/anim
import android.widget.ImageView; // Untuk menampilkan gambar/logo

import androidx.appcompat.app.AppCompatActivity; // Activity bawaan Android dengan fitur tambahan

import com.example.ticketbookingapp.R; // Untuk mengakses resource (layout, animasi, dll)

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // total durasi splash screen (3 detik)
    private static final int FADE_OUT_DELAY = 1000;  // durasi animasi fade out (1 detik sebelum selesai)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Memanggil fungsi bawaan untuk memulai activity
        setContentView(R.layout.activity_splash_screen); // Menentukan layout yang digunakan (activity_splash_screen.xml)

        ImageView logoImage = findViewById(R.id.logoImage); // Menghubungkan ImageView dengan ID logoImage di layout

        // Mulai animasi fade-in (gambar muncul perlahan)
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in); // Ambil file animasi fade_in.xml
        logoImage.startAnimation(fadeIn); // Jalankan animasi fade-in pada logo

        // Setelah SPLASH_DURATION - FADE_OUT_DELAY (yaitu 2 detik), mulai animasi fade-out (gambar menghilang perlahan)
        new Handler().postDelayed(() -> {
            Animation fadeOut = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.fade_out); // Ambil file animasi fade_out.xml
            logoImage.startAnimation(fadeOut); // Jalankan animasi fade-out pada logo
        }, SPLASH_DURATION - FADE_OUT_DELAY);

        // Setelah SPLASH_DURATION (3 detik), lanjut ke LoginActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class); // Intent untuk pindah ke LoginActivity
            startActivity(intent); // Jalankan activity LoginActivity
            finish(); // Tutup SplashScreenActivity agar tidak bisa kembali lagi ke sini
        }, SPLASH_DURATION);
    }
}
