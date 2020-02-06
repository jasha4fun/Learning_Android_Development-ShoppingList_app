package com.example.shoppinglist.Maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppinglist.Model.Shop;
import com.example.shoppinglist.R;

import java.util.List;

public class ShopListAdapter extends RecyclerView.Adapter<ShopListViewHolder> {

    public List<Shop> shops;
    private Context context;

    public ShopListAdapter(List<Shop> shops, Context context) {
        this.shops = shops;
        this.context = context;

    }

    @NonNull
    @Override
    public ShopListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.shop_list_item, parent, false);
        return new ShopListViewHolder(view, this.context);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopListViewHolder holder, int position) {
        holder.bind(shops.get(position));

    }

    @Override
    public int getItemCount() {

        return shops.size();
    }
}
