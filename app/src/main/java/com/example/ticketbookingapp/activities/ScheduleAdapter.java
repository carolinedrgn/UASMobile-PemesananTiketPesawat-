package com.example.ticketbookingapp.activities;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbookingapp.R;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    // List yang berisi data jadwal (schedule) dalam bentuk Map
    private List<Map<String, Object>> scheduleList;

    // Listener yang akan dipanggil saat tombol "Pesan" ditekan
    private Consumer<Map<String, Object>> onItemClickListener;

    // Konstruktor adapter untuk mengatur list dan listener
    public ScheduleAdapter(List<Map<String, Object>> scheduleList, Consumer<Map<String, Object>> onItemClickListener) {
        this.scheduleList = scheduleList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_schedule untuk setiap item di RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        // Ambil data jadwal pada posisi tertentu
        Map<String, Object> schedule = scheduleList.get(position);

        // Set teks pada setiap TextView sesuai dengan data dari jadwal
        holder.textViewDepartureTime.setText(schedule.get("departureTime").toString());
        holder.textViewArrivalTime.setText(schedule.get("arrivalTime").toString());
        holder.textViewPrice.setText(schedule.get("price").toString());
        holder.textViewAirline.setText(schedule.get("airline") + " (" + schedule.get("departureCity") + " → " + schedule.get("destinationCity") + ")");
        holder.textViewDate.setText("Tanggal: " + schedule.get("departureDate").toString());
        holder.textViewTransportType.setText("Transportasi: " + schedule.get("transportType").toString());

        // Saat tombol "Pesan" ditekan, jalankan fungsi listener yang sudah didefinisikan
        // Biasanya akan menyimpan data booking ke Firestore dan navigasi ke halaman berikutnya
        holder.buttonBook.setOnClickListener(v -> {
            // Jangan startActivity di sini, cukup jalankan listener saja agar navigasi dilakukan di activity
            onItemClickListener.accept(schedule);
        });
    }

    @Override
    public int getItemCount() {
        // Mengembalikan jumlah data yang akan ditampilkan di RecyclerView
        return scheduleList.size();
    }

    // ViewHolder adalah class yang menampung view-view dari layout item_schedule
    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDepartureTime, textViewArrivalTime, textViewPrice, textViewAirline;
        TextView textViewDate, textViewTransportType;
        Button buttonBook, buttonFillPassengerData;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            // Menghubungkan variabel dengan komponen UI di layout item_schedule
            textViewDepartureTime = itemView.findViewById(R.id.textViewDepartureTime);
            textViewArrivalTime = itemView.findViewById(R.id.textViewArrivalTime);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewAirline = itemView.findViewById(R.id.textViewAirline);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTransportType = itemView.findViewById(R.id.textViewTransportType);
            buttonBook = itemView.findViewById(R.id.buttonBook);
            buttonFillPassengerData = itemView.findViewById(R.id.buttonFillPassengerData); // bisa dihilangkan kalau tidak dipakai
        }
    }
}
