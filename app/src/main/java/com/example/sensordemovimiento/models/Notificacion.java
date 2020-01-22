package com.example.sensordemovimiento.models;

/**
 * Esta clase define a Notificación para encapsular la información del api
 * @author: Mauricio Leiton Lázaro(mdleiton)
 * @version: 1.0
 */
public class Notificacion {
    private int id;
    private String fecha;
    private int region;
    private String urlVideo;
    private Nodo nodo;

    public Notificacion(int id, String fecha, int region, String urlVideo, Nodo nodo) {
        if(fecha.length()> 19) {
            this.fecha = fecha.substring(0, 19).replace("T", " ");
        }else{
            this.fecha = fecha;
        }
        this.id = id;
        this.region = region;
        this.urlVideo = urlVideo;
        this.nodo = nodo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }

    public Nodo getNodo() {
        return nodo;
    }

    public void setNodo(Nodo nodo) {
        this.nodo = nodo;
    }
}
