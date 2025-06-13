package com.example.ticketbookingapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketbookingapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PaymentConfirmationActivity extends AppCompatActivity {

    private TextView textViewTimer; // Teks untuk menampilkan waktu mundur
    private TextView textViewBookingDetails; // Teks untuk menampilkan detail pesanan
    private ImageView imageViewQRCode; // Gambar untuk menampilkan QR code
    private Button buttonBackHome; // Tombol kembali ke home
    private Button buttonConfirmManual; // Tombol konfirmasi manual pembayaran
    private CountDownTimer countDownTimer; // Objek untuk timer hitung mundur
    private FirebaseFirestore db; // Instance Firebase Firestore
    private String bookingId; // ID pesanan
    private boolean isPaymentConfirmed = false; // Flag untuk melacak apakah pembayaran sudah dikonfirmasi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation); // Set layout activity

        // Inisialisasi komponen UI
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewBookingDetails = findViewById(R.id.textViewBookingDetails);
        imageViewQRCode = findViewById(R.id.imageViewQRCode);
        buttonBackHome = findViewById(R.id.buttonBackHome);
        buttonConfirmManual = findViewById(R.id.buttonConfirmManual);

        db = FirebaseFirestore.getInstance(); // Inisialisasi Firestore
        bookingId = getIntent().getStringExtra("bookingId"); // Ambil bookingId dari Intent

        generateQRCode(bookingId); // Buat QR code dari bookingId
        startCountdown(); // Mulai timer hitung mundur
        loadBookingDetails(); // Ambil dan tampilkan detail booking dari Firestore

        // Event ketika tombol kembali ditekan
        buttonBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentConfirmationActivity.this, MainActivity.class);
            startActivity(intent); // Kembali ke halaman utama
            finish(); // Tutup activity ini
        });

        // Event ketika tombol konfirmasi manual ditekan
        buttonConfirmManual.setOnClickListener(v -> {
            if (isPaymentConfirmed) {
                Toast.makeText(this, "Pembayaran sudah dikonfirmasi!", Toast.LENGTH_SHORT).show();
                return; // Jika sudah dikonfirmasi, tidak perlu lanjut
            }
            buttonConfirmManual.setEnabled(false); // Nonaktifkan tombol agar tidak diklik berulang
            confirmPayment(); // Panggil fungsi konfirmasi pembayaran
        });
    }

    // Fungsi untuk membuat QR Code berdasarkan bookingId
    private void generateQRCode(String data) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
            imageViewQRCode.setImageBitmap(bitmap); // Tampilkan bitmap ke ImageView
        } catch (WriterException e) {
            Toast.makeText(this, "Gagal membuat QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Fungsi untuk memulai hitung mundur selama 5 menit
    private void startCountdown() {
        countDownTimer = new CountDownTimer(5 * 60 * 1000, 1000) { // 5 menit dalam milidetik
            public void onTick(long millisUntilFinished) {
                // Format sisa waktu menjadi MM:SS
                String time = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                textViewTimer.setText("Sisa waktu pembayaran: " + time);
            }

            public void onFinish() {
                textViewTimer.setText("Waktu habis!");
                if (!isPaymentConfirmed) {
                    confirmPayment(); // Jika waktu habis dan belum dibayar, otomatis konfirmasi
                }
            }
        }.start();
    }

    // Fungsi untuk mengubah status booking menjadi "paid" di Firestore
    private void confirmPayment() {
        db.collection("bookings").document(bookingId)
                .update("status", "paid")
                .addOnSuccessListener(aVoid -> {
                    isPaymentConfirmed = true; // Tandai sudah bayar
                    buttonConfirmManual.setEnabled(false); // Nonaktifkan tombol
                    Toast.makeText(PaymentConfirmationActivity.this, "Pembayaran dikonfirmasi!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PaymentConfirmationActivity.this, MainActivity.class);
                    startActivity(intent); // Kembali ke home setelah berhasil
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PaymentConfirmationActivity.this, "Gagal konfirmasi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    buttonConfirmManual.setEnabled(true); // Aktifkan kembali jika gagal
                });
    }

    // Fungsi untuk mengambil data pesanan dari Firestore dan menampilkannya
    private void loadBookingDetails() {
        db.collection("bookings").document(bookingId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Ambil data booking dari Firestore
                        String airline = documentSnapshot.getString("airline");
                        String departureCity = documentSnapshot.getString("departureCity");
                        String destinationCity = documentSnapshot.getString("destinationCity");
                        String departureDate = documentSnapshot.getString("departureDate");
                        String departureTime = documentSnapshot.getString("departureTime");
                        String arrivalTime = documentSnapshot.getString("arrivalTime");
                        String price = documentSnapshot.getString("price");
                        String status = documentSnapshot.getString("status");

                        // Jika status sudah "paid", tandai dan nonaktifkan tombol
                        if ("paid".equalsIgnoreCase(status)) {
                            isPaymentConfirmed = true;
                            buttonConfirmManual.setEnabled(false);
                            Toast.makeText(this, "Pembayaran sudah dikonfirmasi sebelumnya!", Toast.LENGTH_SHORT).show();
                        }

                        // Gabungkan semua info ke dalam satu string
                        String details = "Maskapai: " + airline + "\n" +
                                "Rute: " + departureCity + " - " + destinationCity + "\n" +
                                "Tanggal: " + departureDate + "\n" +
                                "Waktu: " + departureTime + " - " + arrivalTime + "\n" +
                                "Harga: Rp " + price;

                        textViewBookingDetails.setText(details); // Tampilkan ke UI
                    } else {
                        textViewBookingDetails.setText("Detail pesanan tidak ditemukan.");
                    }
                })
                .addOnFailureListener(e ->
                        textViewBookingDetails.setText("Gagal memuat data: " + e.getMessage()));
    }

    // Lifecycle method yang dipanggil ketika activity dihancurkan
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel(); // Hentikan timer jika activity ditutup
    }
}
