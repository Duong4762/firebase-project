package com.example.class_ex;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.class_ex.adapters.MovieAdapter;
import com.example.class_ex.models.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickListener {

    private static final int REQ_POST_NOTIFICATIONS = 1001;

    private MovieAdapter adapter;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_movies);
        }

        RecyclerView rv = findViewById(R.id.recyclerMovies);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter(this);
        rv.setAdapter(adapter);

        requestNotificationPermissionIfNeeded();
        loadMovies();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQ_POST_NOTIFICATIONS
            );
        }
    }

    private void loadMovies() {
        FirebaseFirestore.getInstance()
                .collection("movies")
                .get()
                .addOnSuccessListener(q -> {
                    List<Movie> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : q) {
                        list.add(Movie.from(doc));
                    }
                    adapter.setItems(list);
                    if (list.isEmpty()) {
                        Toast.makeText(this, R.string.hint_empty_movies, Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_my_tickets) {
            startActivity(new Intent(this, MyTicketsActivity.class));
            return true;
        }
        if (id == R.id.action_seed_sample) {
            SampleDataSeeder.seed(FirebaseFirestore.getInstance())
                    .addOnSuccessListener(v -> {
                        Toast.makeText(this, R.string.seed_ok, Toast.LENGTH_LONG).show();
                        loadMovies();
                    })
                    .addOnFailureListener(e -> Toast.makeText(
                            this,
                            getString(R.string.seed_fail, e.getMessage() != null ? e.getMessage() : ""),
                            Toast.LENGTH_LONG
                    ).show());
            return true;
        }
        if (id == R.id.action_logout) {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent i = new Intent(this, MovieDetailActivity.class);
        i.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.getId());
        i.putExtra(MovieDetailActivity.EXTRA_MOVIE_TITLE, movie.getTitle());
        startActivity(i);
    }
}
