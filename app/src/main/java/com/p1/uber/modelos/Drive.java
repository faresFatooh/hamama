package com.p1.uber.modelos;

public class Drive {
    String id;
    String name;
    String email;
    String vehicle;
    String place;

    public Drive(String id, String name, String email, String vehicle, String place) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.vehicle = vehicle;
        this.place = place;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getPlace() {
        return place;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
