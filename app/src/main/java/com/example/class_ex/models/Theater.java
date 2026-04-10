package com.example.class_ex.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Theater {
    private String id;
    private String name;
    private String address;

    public Theater() {}

    public static Theater from(DocumentSnapshot doc) {
        Theater t = new Theater();
        t.id = doc.getId();
        t.name = doc.getString("name");
        t.address = doc.getString("address");
        return t;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public String getAddress() {
        return address != null ? address : "";
    }
}
