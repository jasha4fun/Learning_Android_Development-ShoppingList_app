package com.example.shoppinglist.Model;

import android.content.Context;
import android.util.Log;

import com.example.shoppinglist.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DbHelper {

    public enum COLUMN_NAME {id, name, price, amount, date}

    private static GenericTypeIndicator<Map<String, Shop>> ListOfShopsType =
            new GenericTypeIndicator<Map<String, Shop>>() {
            };

    private final Context context;
    private final DatabaseReference shopsDbReference;

    public DbHelper(Context context) {
        this.context = context;

        shopsDbReference = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.firebase_ref_shops));
    }


    public CompletableFuture<List<Shop>> getAllShops() {

        final CompletableFuture<List<Shop>> result = new CompletableFuture<>();

        shopsDbReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        result.complete(new ArrayList<Shop>(dataSnapshot.getValue(ListOfShopsType).values()));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("TAG:", "Failed to read value.", error.toException());
                        result.cancel(false);
                    }
                });

        return result;
    }

    public CompletableFuture<Shop> getSingleShop(String id) {
        if (id == null) {
            return CompletableFuture.completedFuture(null);
        }

        final CompletableFuture<Shop> result = new CompletableFuture<>();

        shopsDbReference.child(id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            result.complete(null);
                        }

                        result.complete(snapshot.getValue(Shop.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("TAG:", "Failed to read value.", error.toException());
                        result.cancel(false);
                    }
                });

        return result;
    }

    public CompletableFuture<Shop> upsertShop(Shop shop) {
        return getSingleShop(shop.id).thenCompose(saved -> {
            if (saved == null) shop.id = shopsDbReference.push().getKey();

            CompletableFuture<Shop> result = new CompletableFuture<>();

            shopsDbReference.child(shop.id).setValue(shop, (error, ref) -> {
                if (error == null) {
                    result.complete(shop);
                } else {
                    result.cancel(false);
                }
            });

            return result;
        });
    }

    public CompletableFuture deleteShop(String shopId) {
        CompletableFuture result = new CompletableFuture();

        shopsDbReference.child(shopId).removeValue((error, ref) -> {
            if (error == null) result.complete(null);
            else result.cancel(false);
        });

        return result;
    }
}
