package com.example.a4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.*;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ShoppingListAdapter extends FirestoreRecyclerAdapter<ShoppingItem, ShoppingListAdapter.ShoppingItemViewHolder> {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference itemsRef = database.getReference("items");

    public ShoppingListAdapter(FirestoreRecyclerOptions<ShoppingItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(ShoppingItemViewHolder holder, int position, ShoppingItem model) {
        holder.itemName.setText(model.getItemName());
        holder.quantity.setText(model.getQuantity());
        holder.price.setText(model.getPrice());

        holder.btnDelete.setOnClickListener(v -> {
            DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
            String itemId = snapshot.getId();

            firestore.collection("items").document(itemId).delete()
                    .addOnSuccessListener(aVoid -> {
                        itemsRef.child(itemId).removeValue()
                                .addOnSuccessListener(aVoid1 -> Toast.makeText(holder.itemView.getContext(), "Item deleted", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(), "Error deleting from Realtime Database", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(), "Error deleting from Firestore", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public ShoppingItemViewHolder onCreateViewHolder(ViewGroup group, int i) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.shopping_item, group, false);
        return new ShoppingItemViewHolder(view);
    }

    public class ShoppingItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, quantity, price;
        Button btnDelete;

        public ShoppingItemViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
