package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminActivity extends AppCompatActivity implements UserAdapter.OnDeleteClickListener {

    private RecyclerView userRecyclerView;
    private DatabaseHelper db;
    private UserAdapter userAdapter;
    private ArrayList<HashMap<String, String>> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = new DatabaseHelper(this);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUsers();
    }

    private void loadUsers() {
        Cursor cursor = db.getAllUsers();
        userList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("USERNAME", cursor.getString(cursor.getColumnIndex("USERNAME")));
                map.put("EMAIL", cursor.getString(cursor.getColumnIndex("EMAIL")));
                map.put("ROLE", cursor.getString(cursor.getColumnIndex("ROLE")));
                map.put("ID", cursor.getString(cursor.getColumnIndex("ID")));
                userList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();

        userAdapter = new UserAdapter(this, userList, this);
        userRecyclerView.setAdapter(userAdapter);
    }

    @Override
    public void onDeleteClick(String userId) {
        if (db.deleteUser(userId)) {
            Toast.makeText(this, "Utilisateur supprimé", Toast.LENGTH_SHORT).show();
            loadUsers(); // Rechargez la liste
        } else {
            Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
        }
    }
}
