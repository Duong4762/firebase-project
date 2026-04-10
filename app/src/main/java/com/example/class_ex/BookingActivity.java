package com.example.class_ex;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.class_ex.models.Showtime;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    public static final String EXTRA_SHOWTIME_ID = "showtimeId";
    public static final String EXTRA_MOVIE_ID = "movieId";
    public static final String EXTRA_MOVIE_TITLE = "movieTitle";
    public static final String EXTRA_THEATER_NAME = "theaterName";

    private String showtimeId;
    private String movieTitle;
    private String theaterName;
    private int seatCount = 1;
    private int maxSeats = 1;
    private long pricePerSeat;
    private long showTimeMillis;
    private TextInputLayout layoutTicketQty;
    private TextInputEditText inputTicketQty;
    private TextView summaryView;
    private TextView totalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        showtimeId = getIntent().getStringExtra(EXTRA_SHOWTIME_ID);
        movieTitle = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);
        theaterName = getIntent().getStringExtra(EXTRA_THEATER_NAME);
        if (showtimeId == null) {
            finish();
            return;
        }

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_booking);
        }

        summaryView = findViewById(R.id.textSummary);
        totalView = findViewById(R.id.textTotal);
        layoutTicketQty = findViewById(R.id.layoutTicketQty);
        inputTicketQty = findViewById(R.id.inputTicketQty);
        MaterialButton btn = findViewById(R.id.btnConfirm);

        inputTicketQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                onQuantityTextChangedForPreview();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("showtimes").document(showtimeId).get().addOnSuccessListener(doc -> {
            if (!doc.exists()) {
                Toast.makeText(this, R.string.err_showtime_missing, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            Showtime s = Showtime.from(doc);
            pricePerSeat = s.getPrice();
            showTimeMillis = s.getStartTimeMillis();
            maxSeats = s.getSeatsAvailable();
            if (maxSeats <= 0) {
                Toast.makeText(this, R.string.err_no_seats, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            layoutTicketQty.setHelperText(getString(R.string.helper_max_tickets, maxSeats));
            inputTicketQty.setText("1");
            seatCount = 1;
            layoutTicketQty.setError(null);
            updateSummary(summaryView, totalView);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        });

        btn.setOnClickListener(v -> confirmBooking());
    }

    private void onQuantityTextChangedForPreview() {
        if (summaryView == null || totalView == null) {
            return;
        }
        String raw = inputTicketQty.getText() != null ? inputTicketQty.getText().toString().trim() : "";
        if (raw.isEmpty()) {
            layoutTicketQty.setError(null);
            return;
        }
        int n;
        try {
            n = Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            layoutTicketQty.setError(getString(R.string.err_ticket_quantity_invalid, maxSeats));
            return;
        }
        if (n < 1 || n > maxSeats) {
            layoutTicketQty.setError(getString(R.string.err_ticket_quantity_invalid, maxSeats));
            return;
        }
        layoutTicketQty.setError(null);
        seatCount = n;
        updateSummary(summaryView, totalView);
    }

    private boolean validateQuantityForSubmit() {
        String raw = inputTicketQty.getText() != null ? inputTicketQty.getText().toString().trim() : "";
        if (raw.isEmpty()) {
            layoutTicketQty.setError(getString(R.string.err_ticket_quantity_empty));
            Toast.makeText(this, R.string.err_ticket_quantity_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        int n;
        try {
            n = Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            layoutTicketQty.setError(getString(R.string.err_ticket_quantity_invalid, maxSeats));
            return false;
        }
        if (n < 1 || n > maxSeats) {
            layoutTicketQty.setError(getString(R.string.err_ticket_quantity_invalid, maxSeats));
            Toast.makeText(this, getString(R.string.err_ticket_quantity_invalid, maxSeats), Toast.LENGTH_SHORT).show();
            return false;
        }
        seatCount = n;
        layoutTicketQty.setError(null);
        return true;
    }

    private void updateSummary(TextView summary, TextView total) {
        summary.setText(getString(R.string.booking_summary, movieTitle != null ? movieTitle : "", theaterName != null ? theaterName : ""));
        long t = pricePerSeat * seatCount;
        total.setText(getString(R.string.booking_total, t));
    }

    private void confirmBooking() {
        if (!validateQuantityForSubmit()) {
            return;
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            return;
        }
        String uid = auth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference showRef = db.collection("showtimes").document(showtimeId);
        DocumentReference ticketRef = db.collection("tickets").document();

        db.runTransaction(transaction -> {
            DocumentSnapshot snap = transaction.get(showRef);
            if (!snap.exists()) {
                throw new IllegalStateException("NOT_FOUND");
            }
            Showtime s = Showtime.from(snap);
            long seats = s.getSeatsAvailable();
            if (seats < seatCount) {
                throw new IllegalStateException("NOT_ENOUGH_SEATS");
            }
            transaction.update(showRef, "seatsAvailable", seats - seatCount);

            Map<String, Object> ticket = new HashMap<>();
            ticket.put("userId", uid);
            ticket.put("showtimeId", showtimeId);
            ticket.put("movieTitle", movieTitle != null ? movieTitle : "");
            ticket.put("theaterName", theaterName != null ? theaterName : "");
            ticket.put("seatCount", seatCount);
            ticket.put("showTimeMillis", showTimeMillis);
            ticket.put("createdAtMillis", System.currentTimeMillis());
            transaction.set(ticketRef, ticket);
            return null;
        }).addOnSuccessListener(v -> {
            Toast.makeText(this, R.string.booking_ok, Toast.LENGTH_SHORT).show();
            String tid = ticketRef.getId();
            TicketNotifications.showBookingConfirmed(
                    this,
                    tid,
                    movieTitle != null ? movieTitle : "",
                    theaterName != null ? theaterName : "",
                    seatCount,
                    showTimeMillis
            );
            ReminderScheduler.scheduleShowReminder(this, tid, movieTitle != null ? movieTitle : "", showTimeMillis);
            finish();
        }).addOnFailureListener(e -> {
            String msg = e.getMessage();
            for (Throwable t = e; t != null; t = t.getCause()) {
                if (t instanceof IllegalStateException && "NOT_ENOUGH_SEATS".equals(t.getMessage())) {
                    msg = getString(R.string.err_not_enough_seats);
                    break;
                }
            }
            Toast.makeText(this, msg != null ? msg : getString(R.string.err_booking), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
