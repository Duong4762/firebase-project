package com.example.class_ex.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Movie {
    private String id;
    private String title;
    private String posterUrl;
    private int durationMin;
    private String genre;
    private String description;

    public Movie() {}

    public static Movie from(DocumentSnapshot doc) {
        Movie m = new Movie();
        m.id = doc.getId();
        m.title = doc.getString("title");
        m.posterUrl = doc.getString("posterUrl");
        Long d = doc.getLong("durationMin");
        m.durationMin = d != null ? d.intValue() : 0;
        m.genre = doc.getString("genre");
        m.description = doc.getString("description");
        return m;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public String getPosterUrl() {
        return posterUrl != null ? posterUrl : "";
    }

    public int getDurationMin() {
        return durationMin;
    }

    public String getGenre() {
        return genre != null ? genre : "";
    }

    public String getDescription() {
        return description != null ? description : "";
    }
}
