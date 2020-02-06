package com.example.shoppinglist.Model;

public class Shop {
    public String id;
    public String name;
    public String description;
    public String url;
    public ShopLocation location;

    public Shop(String shopId, String s, String toString) {
    }

    public Shop(String id, String name, String description, String url) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
    }

    public static class ShopLocation {
        public double latitude;
        public double longitude;
        public float radius;

        public ShopLocation(double latitude, double longitude, float radius) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
        }
    }
}