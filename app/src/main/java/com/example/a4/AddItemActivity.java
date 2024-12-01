package com.example.a4;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddItemActivity extends AppCompatActivity {

    private EditText etItemName, etQuantity, etPrice;
    private Button btnSave;
    private FirebaseDatabase database;
    private DatabaseReference itemsRef;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etItemName = findViewById(R.id.etItemName);
        etQuantity = findViewById(R.id.etQuantity);
        etPrice = findViewById(R.id.etPrice);
        btnSave = findViewById(R.id.btnSave);

        database = FirebaseDatabase.getInstance();
        itemsRef = database.getReference("items");
        firestore = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(view -> {
            String itemName = etItemName.getText().toString().trim();
            String quantity = etQuantity.getText().toString().trim();
            String price = etPrice.getText().toString().trim();

            if (TextUtils.isEmpty(itemName) || TextUtils.isEmpty(quantity) || TextUtils.isEmpty(price)) {
                Toast.makeText(AddItemActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            String itemId = itemsRef.push().getKey();
            ShoppingItem item = new ShoppingItem(itemName, quantity, price);
            if (itemId != null) {
                itemsRef.child(itemId).setValue(item)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                firestore.collection("items").document(itemId).set(item)
                                        .addOnCompleteListener(firestoreTask -> {
                                            if (firestoreTask.isSuccessful()) {
                                                Toast.makeText(AddItemActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(AddItemActivity.this, "Failed to add item to Firestore", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(AddItemActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
