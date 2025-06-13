package com.example.ticketbookingapp.activities;

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

// Adapter untuk menampilkan data histori pemesanan dalam RecyclerView
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<Map<String, Object>> bookingList; // Daftar data pemesanan (berisi map setiap booking)
    private OnDeleteClickListener deleteClickListener; // Listener untuk tombol hapus
    private OnConfirmClickListener confirmClickListener; // Listener untuk tombol konfirmasi

    // Interface untuk aksi tombol Delete
    public interface OnDeleteClickListener {
        void onDeleteClick(String bookingId); // Mengirim ID pemesanan yang akan dihapus
    }

    // Interface untuk aksi tombol Confirm
    public interface OnConfirmClickListener {
        void onConfirmClick(Map<String, Object> booking); // Mengirim seluruh data pemesanan untuk dikonfirmasi
    }

    // Konstruktor adapter menerima daftar data, serta listener tombol hapus dan konfirmasi
    public HistoryAdapter(List<Map<String, Object>> bookingList,
                          OnDeleteClickListener deleteClickListener,
                          OnConfirmClickListener confirmClickListener) {
        this.bookingList = bookingList;
        this.deleteClickListener = deleteClickListener;
        this.confirmClickListener = confirmClickListener;
    }

    // Fungsi untuk membuat ViewHolder dari layout item_history.xml
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    // Menghubungkan data pemesanan ke tampilan pada posisi tertentu
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Map<String, Object> booking = bookingList.get(position); // Ambil data booking di posisi tertentu

        // Tampilkan detail pemesanan dalam TextView
        holder.textViewDetails.setText(
                "Maskapai: " + booking.get("airline") + "\n" +
                        "Rute: " + booking.get("departureCity") + " - " + booking.get("destinationCity") + "\n" +
                        "Tanggal: " + booking.get("departureDate") + "\n" +
                        "Waktu: " + booking.get("departureTime") + " - " + booking.get("arrivalTime") + "\n" +
                        "Harga: " + booking.get("price") + "\n" +
                        "Status: " + booking.get("status")
        );

        // Saat tombol Delete diklik, panggil listener dengan ID booking
        holder.buttonDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(booking.get("id").toString());
            }
        });

        // Saat tombol Confirm diklik, panggil listener dengan seluruh data booking
        holder.buttonConfirm.setOnClickListener(v -> {
            if (confirmClickListener != null) {
                confirmClickListener.onConfirmClick(booking);
            }
        });

        // Jika status sudah "Paid", maka tombol Confirm dinonaktifkan
        String status = (String) booking.get("status");
        holder.buttonConfirm.setEnabled(status == null || !status.equalsIgnoreCase("Paid"));
    }

    // Mengembalikan jumlah item dalam daftar booking
    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    // ViewHolder menyimpan referensi ke elemen UI dalam item_history.xml
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDetails; // Menampilkan detail pemesanan
        Button buttonDelete, buttonConfirm; // Tombol hapus dan konfirmasi

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDetails = itemView.findViewById(R.id.textViewDetails);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonConfirm = itemView.findViewById(R.id.buttonConfirm);
        }
    }
}
