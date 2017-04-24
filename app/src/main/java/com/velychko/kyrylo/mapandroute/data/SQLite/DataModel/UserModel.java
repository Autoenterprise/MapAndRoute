package com.velychko.kyrylo.mapandroute.data.SQLite.DataModel;

public class UserModel {

    public String name;
    public String password;

    public UserModel(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public UserModel() {
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
