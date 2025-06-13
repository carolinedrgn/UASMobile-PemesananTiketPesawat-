package com.example.ticketbookingapp.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.ticketbookingapp.R;

import java.util.HashMap;
import java.util.List;

public class FAQAdapter extends BaseExpandableListAdapter {

    // Context dari activity atau fragment tempat adapter ini digunakan
    private Context context;

    // List pertanyaan (bagian grup dari ExpandableListView)
    private List<String> listQuestions;

    // Map dari pertanyaan ke list jawaban (anak-anak dari grup)
    private HashMap<String, List<String>> listAnswers;

    // Konstruktor untuk menginisialisasi adapter
    public FAQAdapter(Context context, List<String> listQuestions, HashMap<String, List<String>> listAnswers) {
        this.context = context;
        this.listQuestions = listQuestions;
        this.listAnswers = listAnswers;
    }

    // Mengembalikan jumlah grup (jumlah pertanyaan)
    @Override
    public int getGroupCount() {
        return listQuestions.size();
    }

    // Mengembalikan jumlah anak (jawaban) dalam satu grup (pertanyaan)
    @Override
    public int getChildrenCount(int groupPosition) {
        return listAnswers.get(listQuestions.get(groupPosition)).size();
    }

    // Mengembalikan objek pertanyaan berdasarkan posisi grup
    @Override
    public Object getGroup(int groupPosition) {
        return listQuestions.get(groupPosition);
    }

    // Mengembalikan objek jawaban berdasarkan posisi grup dan posisi anak
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listAnswers.get(listQuestions.get(groupPosition)).get(childPosition);
    }

    // Mengembalikan ID unik untuk grup berdasarkan posisinya
    @Override
    public long getGroupId(int groupPosition) { return groupPosition; }

    // Mengembalikan ID unik untuk anak berdasarkan posisinya
    @Override
    public long getChildId(int groupPosition, int childPosition) { return childPosition; }

    // Menyatakan bahwa ID tidak stabil (bisa berubah)
    @Override
    public boolean hasStableIds() { return false; }

    // Mengembalikan tampilan (View) untuk satu grup (pertanyaan)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String question = (String) getGroup(groupPosition); // Ambil teks pertanyaan
        if (convertView == null) {
            // Jika view belum pernah dibuat, inflate layout standar Android untuk grup
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
        }
        // Set teks pertanyaan ke TextView bawaan
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(question);
        return convertView;
    }

    // Mengembalikan tampilan (View) untuk satu anak (jawaban)
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        String answer = (String) getChild(groupPosition, childPosition); // Ambil teks jawaban
        if (convertView == null) {
            // Jika view belum pernah dibuat, inflate layout standar Android untuk anak
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
        }
        // Set teks jawaban ke TextView bawaan
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(answer);
        return convertView;
    }

    // Menentukan apakah anak dapat dipilih
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
}
