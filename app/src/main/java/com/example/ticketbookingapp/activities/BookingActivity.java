package com.example.ticketbookingapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbookingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Activity untuk melakukan pencarian jadwal transportasi dan melakukan pemesanan
public class BookingActivity extends AppCompatActivity {

    // Komponen UI
    private Spinner spinnerDepartureCity, spinnerDestinationCity;
    private EditText editTextDate;
    private Button buttonSearch;
    private ImageButton buttonBack;
    private RecyclerView recyclerViewSchedules;
    private TextView textViewTitle, textViewSearchResult;
    private TextView textViewDepartureCityLabel, textViewDestinationCityLabel, textViewDateLabel;

    // Adapter dan data list
    private ScheduleAdapter scheduleAdapter;
    private List<Map<String, Object>> scheduleList;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Inisialisasi Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Inisialisasi semua komponen UI dari layout
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewSearchResult = findViewById(R.id.textViewSearchResult);
        textViewDepartureCityLabel = findViewById(R.id.textViewDepartureCityLabel);
        textViewDestinationCityLabel = findViewById(R.id.textViewDestinationCityLabel);
        textViewDateLabel = findViewById(R.id.textViewDateLabel);
        spinnerDepartureCity = findViewById(R.id.spinnerDepartureCity);
        spinnerDestinationCity = findViewById(R.id.spinnerDestinationCity);
        editTextDate = findViewById(R.id.editTextDate);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonBack = findViewById(R.id.buttonBack);
        recyclerViewSchedules = findViewById(R.id.recyclerViewSchedules);

        // Tombol kembali ke MainActivity
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(BookingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Mengisi spinner kota dengan data kota tetap
        String[] cities = {"Jakarta", "Surabaya", "Bali", "Yogyakarta", "Medan"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartureCity.setAdapter(cityAdapter);
        spinnerDestinationCity.setAdapter(cityAdapter);

        // Menampilkan dialog pemilih tanggal saat editTextDate diklik
        editTextDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(BookingActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        editTextDate.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Inisialisasi RecyclerView untuk menampilkan daftar jadwal
        scheduleList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(scheduleList, schedule -> {
            // Cek apakah user sudah login
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Silakan login terlebih dahulu!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(BookingActivity.this, LoginActivity.class));
                finish();
                return;
            }

            // Simpan data pemesanan ke Firestore
            String userId = mAuth.getCurrentUser().getUid();
            Map<String, Object> booking = new HashMap<>();
            booking.put("userId", userId);
            booking.put("scheduleId", schedule.get("id").toString());
            booking.put("departureTime", schedule.get("departureTime").toString());
            booking.put("arrivalTime", schedule.get("arrivalTime").toString());
            booking.put("price", schedule.get("price").toString());
            booking.put("airline", schedule.get("airline").toString());
            booking.put("departureCity", schedule.get("departureCity").toString());
            booking.put("destinationCity", schedule.get("destinationCity").toString());
            booking.put("departureDate", schedule.get("departureDate").toString());
            booking.put("transportType", schedule.get("transportType").toString());
            booking.put("bookingTime", System.currentTimeMillis());
            booking.put("status", "pending"); // Status awal pemesanan

            // Simpan ke Firestore
            db.collection("bookings")
                    .add(booking)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(BookingActivity.this, "Pemesanan berhasil!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BookingActivity.this, PassengerDataFormActivity.class);
                        intent.putExtra("bookingId", documentReference.getId()); // Kirim ID pemesanan
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(BookingActivity.this, "Pemesanan gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
        recyclerViewSchedules.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSchedules.setAdapter(scheduleAdapter);

        // Cek apakah ada query pencarian dari search box
        Intent intent = getIntent();
        String searchQuery = intent.getStringExtra("searchQuery");
        String transportType = intent.getStringExtra("transportType");

        if (transportType != null) {
            textViewTitle.setText("Pesan Tiket " + transportType);
        }

        if (searchQuery != null && !searchQuery.isEmpty()) {
            // Jika dari search box, sembunyikan form
            textViewDepartureCityLabel.setVisibility(View.GONE);
            spinnerDepartureCity.setVisibility(View.GONE);
            textViewDestinationCityLabel.setVisibility(View.GONE);
            spinnerDestinationCity.setVisibility(View.GONE);
            textViewDateLabel.setVisibility(View.GONE);
            editTextDate.setVisibility(View.GONE);
            buttonSearch.setVisibility(View.GONE);

            // Tampilkan hasil pencarian
            textViewSearchResult.setText("Hasil pencarian untuk: " + searchQuery);
            textViewSearchResult.setVisibility(View.VISIBLE);

            // Cari jadwal berdasarkan tujuan
            filterSchedules(searchQuery);
        } else {
            // Jika bukan dari search box, gunakan tombol pencarian
            buttonSearch.setOnClickListener(v -> {
                String departureCity = spinnerDepartureCity.getSelectedItem().toString();
                String destinationCity = spinnerDestinationCity.getSelectedItem().toString();
                String date = editTextDate.getText().toString();

                // Validasi input
                if (departureCity.equals(destinationCity)) {
                    Toast.makeText(BookingActivity.this, "Kota asal dan tujuan tidak boleh sama!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (date.isEmpty()) {
                    Toast.makeText(BookingActivity.this, "Harap pilih tanggal keberangkatan!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cari jadwal sesuai input pengguna
                filterSchedules(destinationCity, departureCity, date);
            });
        }
    }

    // Fungsi untuk filter jadwal berdasarkan tujuan saja (digunakan saat searchQuery dari MainActivity)
    private void filterSchedules(String destinationCity) {
        scheduleList.clear();
        db.collection("schedules")
                .whereEqualTo("destinationCity", destinationCity)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> schedule = new HashMap<>(document.getData());
                        schedule.put("id", document.getId());
                        scheduleList.add(schedule);
                    }
                    if (scheduleList.isEmpty()) {
                        Toast.makeText(this, "Tidak ada jadwal untuk tujuan " + destinationCity, Toast.LENGTH_SHORT).show();
                    }
                    scheduleAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal mengambil jadwal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Fungsi untuk filter jadwal berdasarkan kota asal, tujuan, dan tanggal
    private void filterSchedules(String destinationCity, String departureCity, String date) {
        scheduleList.clear();
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // Konversi format tanggal dari input ke format yang sesuai di Firestore
            Date parsedDate = inputFormat.parse(date);
            String formattedDate = outputFormat.format(parsedDate);
            Log.d("BookingActivity", "Mencari jadwal: departureCity=" + departureCity + ", destinationCity=" + destinationCity + ", formattedDate=" + formattedDate);

            // Query ke Firestore
            db.collection("schedules")
                    .whereEqualTo("destinationCity", destinationCity)
                    .whereEqualTo("departureCity", departureCity)
                    .whereEqualTo("departureDate", formattedDate)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Map<String, Object> schedule = new HashMap<>(document.getData());
                            schedule.put("id", document.getId());
                            scheduleList.add(schedule);
                        }
                        if (scheduleList.isEmpty()) {
                            Toast.makeText(this, "Tidak ada jadwal untuk rute dan tanggal tersebut!", Toast.LENGTH_SHORT).show();
                        }
                        scheduleAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal mengambil jadwal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (ParseException e) {
            Toast.makeText(this, "Format tanggal tidak valid: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
