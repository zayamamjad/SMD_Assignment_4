package com.example.a4;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import androidx.appcompat.app.AppCompatActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class HomeActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ShoppingListAdapter adapter;
    private RecyclerView recyclerView;
    private Button btnLogout, btnAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerView);
        btnLogout = findViewById(R.id.btnLogout);
        btnAddItem = findViewById(R.id.btnAddItem);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Query to get all shopping items
        Query query = db.collection("items");

        FirestoreRecyclerOptions<ShoppingItem> options = new FirestoreRecyclerOptions.Builder<ShoppingItem>()
                .setQuery(query, ShoppingItem.class)
                .build();

        adapter = new ShoppingListAdapter(options);
        recyclerView.setAdapter(adapter);

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(HomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });

        btnAddItem.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, AddItemActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();  // Start listening for changes in Firestore
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();  // Stop listening for changes when the activity is stopped
    }
}
