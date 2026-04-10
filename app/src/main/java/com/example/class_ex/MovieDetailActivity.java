package com.example.class_ex;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.class_ex.adapters.ShowtimeAdapter;
import com.example.class_ex.models.Movie;
import com.example.class_ex.models.Showtime;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieDetailActivity extends AppCompatActivity implements ShowtimeAdapter.OnShowtimeClickListener {

    public static final String EXTRA_MOVIE_ID = "movieId";
    public static final String EXTRA_MOVIE_TITLE = "movieTitle";

    private String movieId;
    private String movieTitle;
    private final Map<String, String> theaterNames = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        movieId = getIntent().getStringExtra(EXTRA_MOVIE_ID);
        movieTitle = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);
        if (movieId == null) {
            finish();
            return;
        }

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_showtimes);
        }

        RecyclerView rv = findViewById(R.id.recyclerShowtimes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ShowtimeAdapter adapter = new ShowtimeAdapter(theaterNames, this);
        rv.setAdapter(adapter);

        TextView desc = findViewById(R.id.textDescription);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("theaters").get().addOnSuccessListener(q -> {
            for (QueryDocumentSnapshot doc : q) {
                theaterNames.put(doc.getId(), doc.getString("name"));
            }
            db.collection("movies").document(movieId).get().addOnSuccessListener(ms -> {
                if (!ms.exists()) {
                    Toast.makeText(this, R.string.err_movie_missing, Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                Movie m = Movie.from(ms);
                if (movieTitle == null || movieTitle.isEmpty()) {
                    movieTitle = m.getTitle();
                }
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(m.getTitle());
                }
                desc.setText(m.getDescription());
            });
            db.collection("showtimes").whereEqualTo("movieId", movieId).get().addOnSuccessListener(sq -> {
                List<Showtime> list = new ArrayList<>();
                for (QueryDocumentSnapshot doc : sq) {
                    list.add(Showtime.from(doc));
                }
                adapter.setItems(list);
                if (list.isEmpty()) {
                    Toast.makeText(this, R.string.hint_empty_showtimes, Toast.LENGTH_LONG).show();
                }
            });
        }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    @Override
    public void onShowtimeClick(Showtime showtime) {
        String theater = theaterNames.get(showtime.getTheaterId());
        if (theater == null) {
            theater = showtime.getTheaterId();
        }
        Intent i = new Intent(this, BookingActivity.class);
        i.putExtra(BookingActivity.EXTRA_SHOWTIME_ID, showtime.getId());
        i.putExtra(BookingActivity.EXTRA_MOVIE_ID, movieId);
        i.putExtra(BookingActivity.EXTRA_MOVIE_TITLE, movieTitle);
        i.putExtra(BookingActivity.EXTRA_THEATER_NAME, theater);
        startActivity(i);
    }
}
