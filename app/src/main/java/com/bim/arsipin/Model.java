package com.bim.arsipin;

public class Model {
    String id , judul , jenis , date , uri, arsip;

    public Model() {
    }

    public Model(String id, String judul, String jenis, String date, String uri, String arsip) {
        this.id = id;
        this.judul = judul;
        this.jenis = jenis;
        this.date = date;
        this.uri = uri;
        this.arsip = arsip;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getArsip() {
        return arsip;
    }

    public void setArsip(String arsip) {
        this.arsip = arsip;
    }
}
