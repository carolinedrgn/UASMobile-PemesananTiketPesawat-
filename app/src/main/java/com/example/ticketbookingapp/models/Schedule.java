package com.example.ticketbookingapp.models;

// Kelas Schedule merepresentasikan data jadwal keberangkatan transportasi
public class Schedule {
    // Waktu keberangkatan (misal: "08:00")
    private String departureTime;

    // Waktu tiba (misal: "10:30")
    private String arrivalTime;

    // Harga tiket (misal: "250000")
    private String price;

    // Nama maskapai atau penyedia transportasi (misal: "Garuda Indonesia")
    private String airline;

    // Kota keberangkatan (misal: "Jakarta")
    private String departureCity;

    // Kota tujuan (misal: "Surabaya")
    private String destinationCity;

    // Tanggal keberangkatan (misal: "2025-06-15")
    private String departureDate;

    // Jenis transportasi (misal: "Pesawat", "Kereta", "Bus")
    private String transportType;

    // Konstruktor untuk menginisialisasi semua properti dari Schedule
    public Schedule(String departureTime, String arrivalTime, String price, String airline,
                    String departureCity, String destinationCity, String departureDate, String transportType) {
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.airline = airline;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.departureDate = departureDate;
        this.transportType = transportType;
    }

    // Getter untuk waktu keberangkatan
    public String getDepartureTime() {
        return departureTime;
    }

    // Getter untuk waktu tiba
    public String getArrivalTime() {
        return arrivalTime;
    }

    // Getter untuk harga tiket
    public String getPrice() {
        return price;
    }

    // Getter untuk nama maskapai atau penyedia transportasi
    public String getAirline() {
        return airline;
    }

    // Getter untuk kota keberangkatan
    public String getDepartureCity() {
        return departureCity;
    }

    // Getter untuk kota tujuan
    public String getDestinationCity() {
        return destinationCity;
    }

    // Getter untuk tanggal keberangkatan
    public String getDepartureDate() {
        return departureDate;
    }

    // Getter untuk jenis transportasi
    public String getTransportType() {
        return transportType;
    }
}
