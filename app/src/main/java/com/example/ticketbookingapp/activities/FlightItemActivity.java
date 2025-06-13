package com.example.ticketbookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.ticketbookingapp.activities.PassengerDataFormActivity;
import com.example.ticketbookingapp.R;

// Activity ini menampilkan detail penerbangan yang dipilih dan tombol untuk mengisi data penumpang
public class FlightItemActivity extends AppCompatActivity {

    // Deklarasi tombol untuk mengisi data penumpang
    private Button buttonFillPassengerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flight_item_layout); // Mengatur layout yang digunakan, ganti sesuai file layout kamu

        // Inisialisasi tombol dari layout
        buttonFillPassengerData = findViewById(R.id.buttonFillPassengerData);

        // Atur aksi ketika tombol ditekan
        buttonFillPassengerData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Buat intent untuk berpindah ke halaman form data penumpang
                Intent intent = new Intent(FlightItemActivity.this, PassengerDataFormActivity.class);
                // Jalankan activity tujuan
                startActivity(intent);
            }
        });
    }

}
