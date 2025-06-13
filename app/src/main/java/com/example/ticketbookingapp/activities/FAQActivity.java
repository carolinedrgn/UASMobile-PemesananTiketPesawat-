package com.example.ticketbookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ExpandableListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketbookingapp.R;
import com.example.ticketbookingapp.models.FAQAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Activity ini digunakan untuk menampilkan halaman FAQ (Frequently Asked Questions)
public class FAQActivity extends AppCompatActivity {

    // Komponen UI
    ExpandableListView expandableListView; // Komponen untuk menampilkan daftar pertanyaan dan jawaban yang bisa diperluas
    List<String> listQuestions; // List pertanyaan (judul)
    HashMap<String, List<String>> listAnswers; // Jawaban dari setiap pertanyaan
    FAQAdapter adapter; // Adapter custom untuk mengatur tampilan ExpandableListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq); // Menghubungkan ke layout activity_faq.xml

        // Inisialisasi view
        expandableListView = findViewById(R.id.expandableListView);
        listQuestions = new ArrayList<>();
        listAnswers = new HashMap<>();

        // Siapkan data pertanyaan dan jawaban
        prepareData();

        // Pasang adapter custom ke expandable list
        adapter = new FAQAdapter(this, listQuestions, listAnswers);
        expandableListView.setAdapter(adapter);

        // Inisialisasi tombol kembali
        Button buttonBack = findViewById(R.id.buttonBackHomeFAQ);
        // Jika tombol ditekan, kembali ke halaman utama (MainActivity)
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(FAQActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Tutup FAQActivity agar tidak kembali ke sini saat tekan tombol back
        });

    }

    // Fungsi untuk menyiapkan data pertanyaan dan jawaban
    private void prepareData() {
        // Tambahkan pertanyaan ke dalam list
        listQuestions.add("Bagaimana cara memesan tiket?");
        listQuestions.add("Apakah saya bisa membatalkan tiket?");
        listQuestions.add("Bagaimana cara melihat e-ticket?");
        listQuestions.add("Apakah tersedia pilihan pembayaran digital?");
        listQuestions.add("Siapa yang bisa saya hubungi untuk bantuan?");

        // Buat daftar jawaban untuk setiap pertanyaan
        List<String> ans1 = new ArrayList<>();
        ans1.add("Anda bisa mencari jadwal, pilih tiket, isi data penumpang, lalu lanjut ke pembayaran.");

        List<String> ans2 = new ArrayList<>();
        ans2.add("Ya, Anda bisa membatalkan tiket melalui menu 'Riwayat Pemesanan' jika masih dalam waktu yang diizinkan.");

        List<String> ans3 = new ArrayList<>();
        ans3.add("E-ticket akan muncul di menu 'Tiket Saya' setelah pembayaran berhasil.");

        List<String> ans4 = new ArrayList<>();
        ans4.add("Ya, kami mendukung pembayaran via e-wallet seperti OVO, GoPay, dan lainnya.");

        List<String> ans5 = new ArrayList<>();
        ans5.add("Hubungi tim kami melalui email di bantuan@ticketapp.com.");

        // Hubungkan setiap pertanyaan dengan jawabannya dalam struktur HashMap
        listAnswers.put(listQuestions.get(0), ans1);
        listAnswers.put(listQuestions.get(1), ans2);
        listAnswers.put(listQuestions.get(2), ans3);
        listAnswers.put(listQuestions.get(3), ans4);
        listAnswers.put(listQuestions.get(4), ans5);
    }
}
