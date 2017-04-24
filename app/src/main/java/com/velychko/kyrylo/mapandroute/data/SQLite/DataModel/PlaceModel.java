package com.velychko.kyrylo.mapandroute.data.SQLite.DataModel;

public class PlaceModel {

    public String userName;
    public double latitude;
    public double longitude;
    public String name;

    public PlaceModel() {

    }

    public PlaceModel(String userName, double latitude, double longitude, String name) {
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    @Override
    public String toString() {
        return "PlaceModel{" +
                "userName='" + userName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                '}';
    }
}
