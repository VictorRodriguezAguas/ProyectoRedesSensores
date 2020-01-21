package com.example.sensordemovimiento.models;

public class Nodo {
    private int id;
    private double latitud;
    private double longitud;
    private String mac;
    private String descripcion;

    public Nodo(int id) {
        this.id = id;
    }

    public Nodo(int id, double latitud, double longitud, String mac, String descripcion) {
        this.id = id;
        this.latitud = latitud;
        this.longitud = longitud;
        this.mac = mac;
        this.descripcion = descripcion;
    }

    public int getId(){
        return this.id;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Id-> " + id;
    }
}
