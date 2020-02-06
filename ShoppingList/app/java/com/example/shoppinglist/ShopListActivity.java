package com.example.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toolbar;

import com.example.shoppinglist.Maps.ShopListAdapter;
import com.example.shoppinglist.Model.DbHelper;
import com.example.shoppinglist.Model.Shop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ShopListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private Toolbar supportActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        setContentView(R.layout.activity_shop_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab_shop_list);
        fab.setOnClickListener(v -> startActivity(new Intent(this, ShopEditActivity.class)));

        recyclerView = findViewById(R.id.shop_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        List<Shop> allShops = new ArrayList<>();
        ShopListAdapter adapter = new ShopListAdapter(allShops, this);
        recyclerView.setAdapter(adapter);

        showProgressDialog("Loading list...");
        new DbHelper(this).getAllShops().thenAccept(shops -> {
            adapter.shops = shops;
            adapter.notifyDataSetChanged();
            hideProgressDialog();
        });
    }

    public void setSupportActionBar(Toolbar supportActionBar) {
        this.supportActionBar = supportActionBar;
    }
}
