package com.example.class_ex.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.class_ex.R;
import com.example.class_ex.models.Showtime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.VH> {

    public interface OnShowtimeClickListener {
        void onShowtimeClick(Showtime showtime);
    }

    private final List<Showtime> items = new ArrayList<>();
    private final Map<String, String> theaterNames;
    private final OnShowtimeClickListener listener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public ShowtimeAdapter(Map<String, String> theaterNames, OnShowtimeClickListener listener) {
        this.theaterNames = theaterNames;
        this.listener = listener;
    }

    public void setItems(List<Showtime> showtimes) {
        items.clear();
        if (showtimes != null) {
            items.addAll(showtimes);
            Collections.sort(items, (a, b) -> Long.compare(a.getStartTimeMillis(), b.getStartTimeMillis()));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_showtime, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Showtime s = items.get(position);
        String theater = theaterNames != null ? theaterNames.get(s.getTheaterId()) : null;
        if (theater == null) {
            theater = s.getTheaterId();
        }
        holder.theater.setText(theater);
        holder.time.setText(sdf.format(new Date(s.getStartTimeMillis())));
        holder.price.setText(holder.itemView.getContext().getString(R.string.price_format, s.getPrice()));
        holder.seats.setText(holder.itemView.getContext().getString(R.string.seats_left, s.getSeatsAvailable()));
        holder.itemView.setOnClickListener(v -> {
            if (s.getSeatsAvailable() > 0) {
                listener.onShowtimeClick(s);
            }
        });
        holder.itemView.setAlpha(s.getSeatsAvailable() > 0 ? 1f : 0.5f);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView theater;
        final TextView time;
        final TextView price;
        final TextView seats;

        VH(@NonNull View itemView) {
            super(itemView);
            theater = itemView.findViewById(R.id.textTheater);
            time = itemView.findViewById(R.id.textTime);
            price = itemView.findViewById(R.id.textPrice);
            seats = itemView.findViewById(R.id.textSeats);
        }
    }
}
