package com.example.class_ex.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Ticket {
    private String id;
    private String userId;
    private String showtimeId;
    private String movieTitle;
    private String theaterName;
    private int seatCount;
    private long showTimeMillis;
    private long createdAtMillis;

    public Ticket() {}

    public static Ticket from(DocumentSnapshot doc) {
        Ticket t = new Ticket();
        t.id = doc.getId();
        t.userId = doc.getString("userId");
        t.showtimeId = doc.getString("showtimeId");
        t.movieTitle = doc.getString("movieTitle");
        t.theaterName = doc.getString("theaterName");
        Long sc = doc.getLong("seatCount");
        t.seatCount = sc != null ? sc.intValue() : 0;
        Long st = doc.getLong("showTimeMillis");
        t.showTimeMillis = st != null ? st : 0L;
        Long c = doc.getLong("createdAtMillis");
        t.createdAtMillis = c != null ? c : 0L;
        return t;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId != null ? userId : "";
    }

    public String getShowtimeId() {
        return showtimeId != null ? showtimeId : "";
    }

    public String getMovieTitle() {
        return movieTitle != null ? movieTitle : "";
    }

    public String getTheaterName() {
        return theaterName != null ? theaterName : "";
    }

    public int getSeatCount() {
        return seatCount;
    }

    public long getShowTimeMillis() {
        return showTimeMillis;
    }

    public long getCreatedAtMillis() {
        return createdAtMillis;
    }
}
