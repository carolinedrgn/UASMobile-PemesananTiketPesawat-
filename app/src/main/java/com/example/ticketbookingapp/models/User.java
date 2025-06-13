package com.example.ticketbookingapp.models;

// Kelas User merepresentasikan data pengguna aplikasi
public class User {
    // Variabel untuk menyimpan ID unik pengguna (biasanya dari database)
    private int id;

    // Variabel untuk menyimpan nama pengguna (username)
    private String username;

    // Variabel untuk menyimpan password pengguna
    private String password;

    // Variabel untuk menyimpan email pengguna
    private String email;

    // Konstruktor untuk membuat objek User dengan data lengkap
    public User(int id, String username, String password, String email) {
        this.id = id;               // Mengisi id pengguna
        this.username = username;   // Mengisi username pengguna
        this.password = password;   // Mengisi password pengguna
        this.email = email;         // Mengisi email pengguna
    }

    // Method untuk mendapatkan ID pengguna
    public int getId() {
        return id;
    }

    // Method untuk mendapatkan username pengguna
    public String getUsername() {
        return username;
    }

    // Method untuk mendapatkan password pengguna
    public String getPassword() {
        return password;
    }

    // Method untuk mendapatkan email pengguna
    public String getEmail() {
        return email;
    }
}
