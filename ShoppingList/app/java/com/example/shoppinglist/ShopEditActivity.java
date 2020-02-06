package com.example.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.shoppinglist.Model.DbHelper;
import com.example.shoppinglist.Model.Shop;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.RuntimeExecutionException;

public class ShopEditActivity extends BaseActivity {

    private DbHelper dbHelper;
    private PlaceDetectionClient placeDetectionClient;
    private Toolbar supportActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DbHelper(this);
        placeDetectionClient = Places.getPlaceDetectionClient(this);

        setContentView(R.layout.activity_shop_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText name = findViewById(R.id.edit_shop_name);
        EditText description = findViewById(R.id.edit_shop_description);
        EditText url = findViewById(R.id.edit_shop_url);
        EditText latitude = findViewById(R.id.edit_shop_latitude);
        EditText longitude = findViewById(R.id.edit_shop_longitude);
        EditText radius = findViewById(R.id.edit_shop_radius);


        Intent intent = getIntent();
        String shopId = intent.getStringExtra(DbHelper.COLUMN_NAME.id.toString());

        if (shopId != null) {
            showProgressDialog("Loading product...");
            dbHelper.getSingleShop(shopId).thenAccept(shop -> {
                hideProgressDialog();

                name.setText(shop.name);
                description.setText(shop.description);
                url.setText(shop.url);

                if (shop.location == null) {
                    return;
                }

                latitude.setText(Double.toString(shop.location.latitude));
                longitude.setText(Double.toString(shop.location.longitude));
                radius.setText(Float.toString(shop.location.radius));
            });

        } else {
            findViewById(R.id.edit_shop_btn_delete).setVisibility(View.GONE);
        }

        findViewById(R.id.edit_shop_btn_save).setOnClickListener(v -> {
            showProgressDialog("Saving...");
            Shop newShop = new Shop(
                    shopId,
                    name.getText().toString(),
                    description.getText().toString(),
                    url.getText().toString());


            if (!TextUtils.isEmpty(latitude.getText()) &&
                    !TextUtils.isEmpty(longitude.getText()) &&
                    !TextUtils.isEmpty(radius.getText())) {

                newShop.location = new Shop.ShopLocation(
                        Double.parseDouble(longitude.getText().toString()),
                        Double.parseDouble(latitude.getText().toString()),
                        Float.parseFloat(radius.getText().toString()));
            }

            dbHelper.upsertShop(newShop).thenRun(() -> {
                hideProgressDialog();
                startActivity(new Intent(this, ShopListActivity.class));
            });

        });

        findViewById(R.id.edit_shop_btn_delete).setOnClickListener(v -> {
            showProgressDialog("Deleting...");
            dbHelper.deleteShop(shopId).thenRun(() -> {
                hideProgressDialog();
                startActivity(new Intent(this, ShopListActivity.class));
            });
        });

        findViewById(R.id.edit_shop_btn_cancel).setOnClickListener(v -> finish());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;

        }

        int servicesStatus = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (servicesStatus != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Play Services not available: " + servicesStatus, Toast.LENGTH_LONG).show();
            return;

        }

        showProgressDialog("Searching...");

        placeDetectionClient.getCurrentPlace(null).addOnCompleteListener(task -> {
            hideProgressDialog();

            PlaceLikelihoodBufferResponse likelyPlaces = null;
            try {
                likelyPlaces = task.getResult();
            } catch (RuntimeExecutionException exception) {
                Toast.makeText(this, "Failed retrieving places: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            if (likelyPlaces.iterator() == null || !likelyPlaces.iterator().hasNext()) {
                Toast.makeText(this, "Nothing found", Toast.LENGTH_LONG).show();
                return;
            }

            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                Log.i("Places", String.format("Place '%s' has likelihood: %g",
                        placeLikelihood.getPlace().getName(),
                        placeLikelihood.getLikelihood()));
            }

            Place place = likelyPlaces.iterator().next().getPlace();

            description.setText(place.getName());

            Uri uri = place.getWebsiteUri();
            if (uri != null) {
                url.setText(uri.toString());
            }

            LatLng latLng = place.getLatLng();
            if (latLng != null) {
                latitude.setText(Double.toString(latLng.latitude));
                longitude.setText(Double.toString(latLng.longitude));
                radius.setText(Float.toString(10));
            }

            likelyPlaces.release();
        });
    }

    public void setSupportActionBar(Toolbar supportActionBar) {
        this.supportActionBar = supportActionBar;
    }
}