package com.example.shoppinglist.Maps;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppinglist.Model.DbHelper;
import com.example.shoppinglist.Model.Shop;
import com.example.shoppinglist.ProductListActivity;
import com.example.shoppinglist.R;
import com.example.shoppinglist.ShopEditActivity;

public class ShopListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;
    private Shop shop;

    private TextView name;
    private TextView description;
    private TextView radius;

    public ShopListViewHolder(View view, Context context) {
        super(view);

        this.context = context;

        name = view.findViewById(R.id.shop_list_name);
        description = view.findViewById(R.id.shop_list_description);
        radius = view.findViewById(R.id.shop_list_radius);

        view.setOnClickListener(this);
    }

    public void bind(Shop shop) {
        this.shop = shop;

        name.setText(shop.name);
        description.setText(shop.description);
        if (shop.location != null) radius.setText(Float.toString(shop.location.radius));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this.context, ShopEditActivity.class);
        context.startActivity(intent);
    }
}