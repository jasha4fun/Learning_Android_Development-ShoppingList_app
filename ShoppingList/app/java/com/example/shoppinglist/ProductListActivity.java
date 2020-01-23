package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppinglist.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class ProductListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private RecyclerView recyclerView;

    private TextView totalSumResult;

    //Global variable:

    private String type;
    private int amount;
    private int price;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlist);

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shopping List");

        totalSumResult = findViewById(R.id.total_amount);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser User = firebaseAuth.getCurrentUser();

        String userID = User.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Shopping List");

        databaseReference.keepSynced(true);

        recyclerView = findViewById(R.id.recycler_product);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalAmount = 0;

                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    Data data = snapshot.getValue(Data.class);

                    totalAmount += data.getAmount();

                    String stTotal = String.valueOf(totalAmount + ".00");

                    totalSumResult.setText(stTotal);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        floatingActionButton = findViewById(R.id.fab_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customDialog();

            }
        });

    }

    private void customDialog () {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(ProductListActivity.this);

        LayoutInflater inflater = LayoutInflater.from(ProductListActivity.this);

        View myView = inflater.inflate(R.layout.input_data, null);

        final AlertDialog dialog = myDialog.create();

        dialog.setView(myView);

        final EditText type = myView.findViewById(R.id.edit_type);
        final EditText amount = myView.findViewById(R.id.edit_amount);
        final EditText price = myView.findViewById(R.id.edit_price);

        Button buttonSave = myView.findViewById(R.id.button_save);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Type = type.getText().toString().trim();
                String Amount = amount.getText().toString().trim();
                String Price = price.getText().toString().trim();

                int amnt = Integer.parseInt(Amount);
                int prc = Integer.parseInt(Price);

                if (TextUtils.isEmpty(Type)) {
                    type.setError("Required field");
                    return;
                }

                if (TextUtils.isEmpty(Amount)) {
                    amount.setError("Requirde field");
                    return;
                }

                String id = databaseReference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(Type, amnt, prc, date, id);

                databaseReference.child(id).setValue(data);

                Toast.makeText(getApplicationContext(), "Data added!", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>().setQuery(databaseReference, Data.class).build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder> (options) {
            @Override
            protected void onBindViewHolder(MyViewHolder viewHolder, final int i, final Data data) {

                viewHolder.setType(data.getType());
                viewHolder.setAmount(data.getAmount());
                viewHolder.setPrice(data.getPrice());
                viewHolder.setDate(data.getDate());

                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(i).getKey();
                        type = data.getType();
                        price = data.getPrice();
                        amount = data.getAmount();


                        updateData();

                    }
                });

            }

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_data, parent, false);

                return new MyViewHolder(view);

            }

        };

        recyclerView.setAdapter(adapter);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View myView;

        public MyViewHolder(View productView) {
            super(productView);
            myView = productView;
        }

        public void setType (String type) {
            TextView Type = myView.findViewById(R.id.type);
            Type.setText(type);
        }

        public void setPrice (int prc) {
            TextView Price = myView.findViewById(R.id.price);
            String productPrice = String.valueOf(prc);
            Price.setText(productPrice);
        }

        public void setDate (String date) {
            TextView Date = myView.findViewById(R.id.date);
            Date.setText(date);
        }

        public void setAmount (int amt) {
            TextView Amount = myView.findViewById(R.id.amount);
            String totalAmount = String.valueOf(amt);
            Amount.setText(totalAmount);
        }

    }

    public void updateData() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(ProductListActivity.this);

        LayoutInflater inflater = LayoutInflater.from(ProductListActivity.this);

        View mView = inflater.inflate(R.layout.update_inputfield, null);

        final AlertDialog dialog = mydialog.create();

        dialog.setView(mView);

        final EditText edt_Type = mView.findViewById(R.id.edit_type_update);
        final EditText edt_Amount = mView.findViewById(R.id.edit_amount_update);
        final EditText edt_Price = mView.findViewById(R.id.edit_price_update);

        edt_Type.setText(type);
        edt_Type.setSelection(type.length());

        edt_Amount.setText(String.valueOf(amount));
        edt_Amount.setSelection(String.valueOf(amount).length());

        edt_Price.setText(String.valueOf(price));
        edt_Price.setSelection(String.valueOf(price).length());

        Button btnUpdate = mView.findViewById(R.id.button_EDIT_update);
        Button btnDelete = mView.findViewById(R.id.button_delete_update);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = edt_Type.getText().toString().trim();

                String mAmount = String.valueOf(amount);

                mAmount = edt_Amount.getText().toString().trim();

                String mPrice = String.valueOf(price);

                mPrice = edt_Price.getText().toString().trim();

                int intAmount = Integer.parseInt(mAmount);

                int intPrice = Integer.parseInt(mPrice);

                String date = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(type, intAmount, intPrice, date, post_key);

                databaseReference.child(post_key).setValue(data);

                dialog.dismiss();;

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child(post_key).removeValue();

                dialog.dismiss();

            }
        });



        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.log_out:
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
