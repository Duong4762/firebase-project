package com.example.class_ex.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Showtime {
    private String id;
    private String movieId;
    private String theaterId;
    private long startTimeMillis;
    private long price;
    private int seatsAvailable;

    public Showtime() {}

    public static Showtime from(DocumentSnapshot doc) {
        Showtime s = new Showtime();
        s.id = doc.getId();
        s.movieId = doc.getString("movieId");
        s.theaterId = doc.getString("theaterId");
        Long st = doc.getLong("startTimeMillis");
        s.startTimeMillis = st != null ? st : 0L;
        Long p = doc.getLong("price");
        s.price = p != null ? p : 0L;
        Long seats = doc.getLong("seatsAvailable");
        s.seatsAvailable = seats != null ? seats.intValue() : 0;
        return s;
    }

    public String getId() {
        return id;
    }

    public String getMovieId() {
        return movieId != null ? movieId : "";
    }

    public String getTheaterId() {
        return theaterId != null ? theaterId : "";
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getPrice() {
        return price;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }
}
